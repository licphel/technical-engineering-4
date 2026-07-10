package com.hypothetic.ten4.lib.capability.energy;

import net.minecraft.core.Direction;

import org.jetbrains.annotations.Nullable;
import java.util.Queue;

public interface IDirectionalEnergyProvider {
  int getEnergy();

  void setEnergy(int e);

  int getMaxEnergy();

  int getMaxEnergyExtract(@Nullable Direction d);

  int getMaxEnergyReceive(@Nullable Direction d);

  default boolean canExtractEnergy(@Nullable Direction d) {
    return getMaxEnergyExtract(d) > 0;
  }

  default boolean canReceiveEnergy(@Nullable Direction d) {
    return  getMaxEnergyReceive(d) > 0;
  }

  Queue<Direction> getEnergyPushingCycle();

  Queue<Direction> getEnergyPullingCycle();
}
