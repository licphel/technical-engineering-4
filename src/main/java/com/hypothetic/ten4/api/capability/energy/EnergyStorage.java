package com.hypothetic.ten4.api.capability.energy;

import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyStorage implements IEnergyStorage {
  private final IEnergyProvider host;

  public EnergyStorage(IEnergyProvider host) {
    this.host = host;
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    if (!canReceive()) {
      return 0;
    }
    int v = Math.min(host.getEnergyCapacity() - host.getEnergy(), Math.min(maxReceive, host.getEnergyThroughput()));
    if (v <= 0) {
      return 0;
    }
    if (!simulate) {
      host.setEnergy(host.getEnergy() + v);
    }
    return v;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    if (!canExtract()) {
      return 0;
    }
    int v = Math.min(host.getEnergy(), Math.min(maxExtract, host.getEnergyThroughput()));
    if (v <= 0) {
      return 0;
    }
    if (!simulate) {
      host.setEnergy(host.getEnergy() - v);
    }
    return v;
  }

  @Override
  public int getEnergyStored() {
    return host.getEnergy();
  }

  @Override
  public int getMaxEnergyStored() {
    return host.getEnergyCapacity();
  }

  @Override
  public boolean canExtract() {
    return true;
  }

  @Override
  public boolean canReceive() {
    return true;
  }
}
