package com.hypothetic.ten4.api.capability.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.Arrays;
import java.util.Queue;

public final class EnergyQueue {
  private static final ThreadLocal<IEnergyStorage[]> CACHE = ThreadLocal.withInitial(() -> new IEnergyStorage[6]);

  private EnergyQueue() {
  }

  public static void push(Level level, BlockPos pos, IDirectionalEnergyProvider host) {
    if (host.getEnergyPushingCycle().isEmpty()) {
      return;
    }
    int energy = host.getEnergy();
    if (energy <= 0) {
      return;
    }

    IEnergyStorage[] cache = CACHE.get();
    Arrays.fill(cache, null);

    Queue<Direction> cycle = host.getEnergyPushingCycle();
    int available = 0;

    for (Direction d : cycle) {
      if (!host.canExtractEnergy(d)) {
        continue;
      }
      IEnergyStorage s = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos.relative(d), d.getOpposite());
      if (s != null && s.canReceive()) {
        cache[d.ordinal()] = s;
        available++;
      }
    }
    if (available == 0) {
      cycle.offer(cycle.remove());
      return;
    }

    int maxRate = host.getMaxEnergyExtract(cycle.peek());
    int per = Math.max(Math.min(energy / available, maxRate), 1);
    int rem = energy - per * available;

    for (Direction d : cycle) {
      IEnergyStorage s = cache[d.ordinal()];
      if (s == null) {
        continue;
      }
      int amt = per + (rem > 0 ? 1 : 0);
      if (rem > 0) {
        rem--;
      }
      amt = Math.min(host.getEnergy(), Math.min(host.getMaxEnergyExtract(d), amt));
      if (amt <= 0) {
        continue;
      }
      int accepted = s.receiveEnergy(amt, false);
      host.setEnergy(host.getEnergy() - accepted);
    }
    cycle.offer(cycle.remove());
  }

  public static void pull(Level level, BlockPos pos, IDirectionalEnergyProvider host) {
    if (host.getEnergyPullingCycle().isEmpty()) {
      return;
    }
    if (host.getEnergy() >= host.getMaxEnergy()) {
      return;
    }

    IEnergyStorage[] cache = CACHE.get();
    Arrays.fill(cache, null);

    Queue<Direction> cycle = host.getEnergyPullingCycle();
    int available = 0;
    int space = host.getMaxEnergy() - host.getEnergy();

    for (Direction d : cycle) {
      if (!host.canReceiveEnergy(d)) {
        continue;
      }
      IEnergyStorage s = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos.relative(d), d.getOpposite());
      if (s != null && s.canExtract()) {
        cache[d.ordinal()] = s;
        available++;
      }
    }
    if (available == 0) {
      cycle.offer(cycle.remove());
      return;
    }

    int maxRate = host.getMaxEnergyReceive(cycle.peek());
    int per = Math.max(Math.min(space / available, maxRate), 1);
    int rem = space - per * available;

    for (Direction d : cycle) {
      IEnergyStorage s = cache[d.ordinal()];
      if (s == null) {
        continue;
      }
      int amt = per + (rem > 0 ? 1 : 0);
      if (rem > 0) {
        rem--;
      }
      amt = Math.min(host.getMaxEnergy() - host.getEnergy(), Math.min(host.getMaxEnergyReceive(d), amt));
      if (amt <= 0) {
        continue;
      }
      int got = s.extractEnergy(amt, false);
      if (got > 0) {
        host.setEnergy(host.getEnergy() + got);
      }
    }
    cycle.offer(cycle.remove());
  }
}
