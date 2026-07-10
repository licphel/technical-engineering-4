package com.hypothetic.ten4.lib.capability.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.Arrays;
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

    int maxRate = host.getMaxFluidExtract(cycle.peek());
    for (int tank = 0; tank < tanks.getTanks(); tank++) {
      FluidStack stack = tanks.getFluidInTank(tank);
      if (stack.isEmpty()) {
        continue;
      }

      int amount = stack.getAmount();
      int toDrain = Math.min(amount, maxRate);
      int per = Math.max(toDrain / available, 1);
      int rem = toDrain - per * available;

      for (Direction d : cycle) {
        IFluidHandler h = cache[d.ordinal()];
        if (h == null) {
          continue;
        }
        int amt = per + (rem > 0 ? 1 : 0);
        if (rem > 0) {
          rem--;
        }
        amt = Math.min(host.getMaxFluidExtract(d), amt);
        if (amt <= 0) {
          continue;
        }

        FluidStack drained = tanks.drain(new FluidStack(stack.getFluid(), amt), IFluidHandler.FluidAction.EXECUTE);
        if (!drained.isEmpty()) {
          h.fill(drained, IFluidHandler.FluidAction.EXECUTE);
        }
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
    int maxRate = host.getMaxFluidReceive(cycle.peek());

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

    int per = Math.max(maxRate / available, 1);
    int rem = maxRate - per * available;

    for (Direction d : cycle) {
      IFluidHandler h = cache[d.ordinal()];
      if (h == null) {
        continue;
      }
      int amt = per + (rem > 0 ? 1 : 0);
      if (rem > 0) {
        rem--;
      }
      amt = Math.min(host.getMaxFluidReceive(d), amt);
      if (amt <= 0) {
        continue;
      }

      FluidStack drained = h.drain(amt, IFluidHandler.FluidAction.EXECUTE);
      if (!drained.isEmpty()) {
        tanks.fill(drained, IFluidHandler.FluidAction.EXECUTE);
      }
    }
    cycle.offer(cycle.remove());
  }
}
