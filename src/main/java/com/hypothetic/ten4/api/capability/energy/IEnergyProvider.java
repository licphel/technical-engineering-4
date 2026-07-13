package com.hypothetic.ten4.api.capability.energy;

public interface IEnergyProvider {
  int getEnergy();

  void setEnergy(int e);

  int getEnergyCapacity();

  int getEnergyThroughput();
}
