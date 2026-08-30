package com.hypothetic.ten4.core.capability;

import com.hypothetic.ten4.core.item.EnergyUnitItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EnergyUnitEnergyHandler implements IEnergyStorage {
  private final ItemStack stack;

  public EnergyUnitEnergyHandler(ItemStack stack) {
    this.stack = stack;
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    int cap = EnergyUnitItem.getEnergyCapacity(stack);
    int stored = EnergyUnitItem.getStoredEnergy(stack);
    int toReceive = Math.min(maxReceive, Math.max(0, cap - stored));
    if (toReceive > 0 && !simulate) {
      EnergyUnitItem.setStoredEnergy(stack, stored + toReceive);
    }
    return toReceive;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    int stored = EnergyUnitItem.getStoredEnergy(stack);
    int toExtract = Math.min(maxExtract, stored);
    if (toExtract > 0 && !simulate) {
      EnergyUnitItem.setStoredEnergy(stack, stored - toExtract);
    }
    return toExtract;
  }

  @Override
  public int getEnergyStored() {
    return EnergyUnitItem.getStoredEnergy(stack);
  }

  @Override
  public int getMaxEnergyStored() {
    return EnergyUnitItem.getEnergyCapacity(stack);
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
