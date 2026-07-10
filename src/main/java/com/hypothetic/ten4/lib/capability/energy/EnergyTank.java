package com.hypothetic.ten4.lib.capability.energy;

public class EnergyTank implements IEnergyProvider {
  private int energy;
  private int capacity;
  private int maxReceive;
  private int maxExtract;

  public void setCapacity(int capacity) {
    this.capacity = capacity;
  }

  public int getCapacity() {
    return capacity;
  }

  public void setMaxReceive(int maxReceive) {
    this.maxReceive = maxReceive;
  }

  public int getMaxReceive() {
    return maxReceive;
  }

  public void setMaxExtract(int maxExtract) {
    this.maxExtract = maxExtract;
  }

  public int getMaxExtract() {
    return maxExtract;
  }

  @Override
  public int getEnergy() {
    return energy;
  }

  @Override
  public void setEnergy(int e) {
    energy = Math.clamp(e, 0, capacity);
  }

  @Override
  public int getMaxEnergy() {
    return capacity;
  }

  @Override
  public int getMaxEnergyExtract() {
    return maxExtract;
  }

  @Override
  public int getMaxEnergyReceive() {
    return maxReceive;
  }
}
