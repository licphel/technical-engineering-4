package com.hypothetic.ten4.api.capability.energy;

public class EnergyTank implements IEnergyProvider {
  private int energy;
  private int capacity;
  private int throughput;

  public int getCapacity() {
    return capacity;
  }

  public void setCapacity(int capacity) {
    this.capacity = capacity;
  }

  public int getThroughput() {
    return throughput;
  }

  public void setThroughput(int throughput) {
    this.throughput = throughput;
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
  public int getEnergyCapacity() {
    return capacity;
  }

  @Override
  public int getEnergyThroughput() {
    return throughput;
  }
}
