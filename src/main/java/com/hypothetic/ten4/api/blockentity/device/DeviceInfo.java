package com.hypothetic.ten4.api.blockentity.device;

import com.hypothetic.ten4.api.capability.fluid.FluidTank;
import com.hypothetic.ten4.api.capability.item.ItemSlot;

import java.util.ArrayList;
import java.util.List;

public final class DeviceInfo {
  int energyCapacity;
  int energyThroughput;
  int itemThroughput;
  int fluidThroughput;
  int power;
  List<ItemSlot> slots = new ArrayList<>();
  List<FluidTank> tanks = new ArrayList<>();
  boolean hasEnergy = false;
  boolean hasFluid = false;
  boolean hasItem = false;

  public DeviceInfo enableEnergy() {
    hasEnergy = true;
    return this;
  }

  public DeviceInfo enableFluid() {
    hasFluid = true;
    return this;
  }

  public DeviceInfo enableItem() {
    hasItem = true;
    return this;
  }

  public DeviceInfo setEnergyCapacity(int energyCapacity) {
    this.energyCapacity = energyCapacity;
    return this;
  }

  public DeviceInfo setEnergyThroughput(int energyThroughput) {
    this.energyThroughput = energyThroughput;
    return this;
  }

  public DeviceInfo setItemThroughput(int itemThroughput) {
    this.itemThroughput = itemThroughput;
    return this;
  }

  public DeviceInfo setFluidThroughput(int fluidThroughput) {
    this.fluidThroughput = fluidThroughput;
    return this;
  }

  public DeviceInfo setPower(int power) {
    this.power = power;
    return this;
  }

  public DeviceInfo addSlot(ItemSlot slot) {
    this.slots.add(slot);
    return this;
  }

  public DeviceInfo addTank(FluidTank tank) {
    this.tanks.add(tank);
    return this;
  }
}
