package com.hypothetic.ten4.api.capability.energy;

import java.util.function.IntSupplier;

public class EnergyTank implements IEnergyProvider {
  private int energy;
  private final IntSupplier capacity;
  private final IntSupplier throughput;

  public EnergyTank(int capacity, int throughput) {
    this.capacity = () -> capacity;
    this.throughput = () -> throughput;
  }

  public EnergyTank(IntSupplier capacity, IntSupplier throughput) {
    this.capacity = capacity;
    this.throughput = throughput;
  }

  @Override
  public int getEnergy() {
    return energy;
  }

  @Override
  public void setEnergy(int e) {
    energy = Math.clamp(e, 0, capacity.getAsInt());
  }

  @Override
  public int getEnergyCapacity() {
    return capacity.getAsInt();
  }

  @Override
  public int getEnergyThroughput() {
    return throughput.getAsInt();
  }
}
