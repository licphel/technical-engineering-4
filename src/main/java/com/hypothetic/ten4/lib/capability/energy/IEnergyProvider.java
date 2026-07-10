package com.hypothetic.ten4.lib.capability.energy;

public interface IEnergyProvider {
  int getEnergy();

  void setEnergy(int e);

  int getMaxEnergy();

  int getMaxEnergyExtract();

  int getMaxEnergyReceive();
}
