package com.hypothetic.ten4.api.capability.energy;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;

public interface IDirectionalEnergyProvider {
  int getEnergy();

  void setEnergy(int e);

  int getEnergyCapacity();

  int getEnergyThroughput();

  boolean canExtractEnergy(@Nullable Direction d);

  boolean canReceiveEnergy(@Nullable Direction d);

  Queue<Direction> getEnergyPushingCycle();

  Queue<Direction> getEnergyPullingCycle();
}
