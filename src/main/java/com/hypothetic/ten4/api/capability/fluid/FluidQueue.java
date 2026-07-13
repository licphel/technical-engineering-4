package com.hypothetic.ten4.api.capability.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;

public final class FluidQueue {
  private static final ThreadLocal<IFluidHandler[]> CACHE = ThreadLocal.withInitial(() -> new IFluidHandler[6]);

  private FluidQueue() {
  }

  public static void push(Level level, BlockPos pos, IDirectionalFluidProvider host) {
    if (host.getFluidPushingCycle().isEmpty()) {
      return;
    }

    IFluidHandler tanks = host.getTanks();
    IFluidHandler[] cache = CACHE.get();
    Arrays.fill(cache, null);
    Queue<Direction> cycle = host.getFluidPushingCycle();
    int available = 0;

    for (Direction d : cycle) {
      if (!host.canExtractFluid(d)) {
        continue;
      }
      IFluidHandler h = level.getCapability(Capabilities.FluidHandler.BLOCK, pos.relative(d), d.getOpposite());
      if (h != null) {
        cache[d.ordinal()] = h;
        available++;
      }
    }
    if (available == 0) {
      cycle.offer(cycle.remove());
      return;
    }

    int maxRate = host.getFluidThroughput();
    for (int tank = 0; tank < tanks.getTanks(); tank++) {
      FluidStack stack = tanks.getFluidInTank(tank);
      if (stack.isEmpty()) {
        continue;
      }

      // 1. Per-target sim-fill to find actual capacity
      Map<Direction, Integer> planned = new LinkedHashMap<>();
      int totalMovable = 0;
      int remaining = Math.min(stack.getAmount(), maxRate);
      for (Direction d : cycle) {
        IFluidHandler h = cache[d.ordinal()];
        if (h == null || remaining <= 0) {
          continue;
        }
        int filled = h.fill(new FluidStack(stack.getFluid(), remaining), IFluidHandler.FluidAction.SIMULATE);
        if (filled > 0) {
          planned.put(d, filled);
          totalMovable += filled;
          remaining -= filled;
        }
      }
      if (totalMovable <= 0) {
        continue;
      }

      // 2. Real drain only what all targets can accept
      FluidStack drained = tanks.drain(new FluidStack(stack.getFluid(), totalMovable), IFluidHandler.FluidAction.EXECUTE);
      if (drained.isEmpty()) {
        continue;
      }

      // 3. Distribute matching planned amounts
      FluidStack toDistribute = drained.copy();
      for (var e : planned.entrySet()) {
        IFluidHandler h = cache[e.getKey().ordinal()];
        int amt = Math.min(e.getValue(), toDistribute.getAmount());
        if (amt <= 0) {
          continue;
        }
        FluidStack portion = new FluidStack(toDistribute.getFluid(), amt);
        int leftoverAmt = amt - h.fill(portion, IFluidHandler.FluidAction.EXECUTE);
        if (leftoverAmt > 0) {
          toDistribute.grow(leftoverAmt - amt); // put back
        }
        toDistribute.shrink(amt);
      }
      // Return any undistributed remainder
      if (!toDistribute.isEmpty()) {
        tanks.fill(toDistribute, IFluidHandler.FluidAction.EXECUTE);
      }
    }
    cycle.offer(cycle.remove());
  }

  public static void pull(Level level, BlockPos pos, IDirectionalFluidProvider host) {
    if (host.getFluidPullingCycle().isEmpty()) {
      return;
    }

    IFluidHandler tanks = host.getTanks();
    IFluidHandler[] cache = CACHE.get();
    Arrays.fill(cache, null);
    Queue<Direction> cycle = host.getFluidPullingCycle();
    int available = 0;

    for (Direction d : cycle) {
      if (!host.canReceiveFluid(d)) {
        continue;
      }
      IFluidHandler h = level.getCapability(Capabilities.FluidHandler.BLOCK, pos.relative(d), d.getOpposite());
      if (h != null) {
        cache[d.ordinal()] = h;
        available++;
      }
    }
    if (available == 0) {
      cycle.offer(cycle.remove());
      return;
    }

    for (Direction d : cycle) {
      IFluidHandler h = cache[d.ordinal()];
      if (h == null) {
        continue;
      }
      int maxPull = host.getFluidThroughput();
      // 1. Sim-drain from neighbor
      FluidStack simDrained = h.drain(maxPull, IFluidHandler.FluidAction.SIMULATE);
      if (simDrained.isEmpty()) {
        continue;
      }

      // 2. Sim-fill into host to find actual capacity
      int filled = tanks.fill(simDrained, IFluidHandler.FluidAction.SIMULATE);
      if (filled <= 0) {
        continue;
      }

      // 3. Real drain + fill only what fits
      FluidStack drained = h.drain(filled, IFluidHandler.FluidAction.EXECUTE);
      if (!drained.isEmpty()) {
        tanks.fill(drained, IFluidHandler.FluidAction.EXECUTE);
      }
    }
    cycle.offer(cycle.remove());
  }
}
