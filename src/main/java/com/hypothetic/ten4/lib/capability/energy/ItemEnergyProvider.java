package com.hypothetic.ten4.lib.capability.energy;

import com.hypothetic.ten4.lib.util.DataComponentHelper;
import net.minecraft.world.item.ItemStack;

public class ItemEnergyProvider implements IEnergyProvider {
  private final ItemStack stack;
  private final int capacity;
  private final int maxExtract;
  private final int maxReceive;

  public ItemEnergyProvider(ItemStack stack, int capacity, int maxReceive, int maxExtract) {
    this.stack = stack;
    this.capacity = capacity;
    this.maxReceive = maxReceive;
    this.maxExtract = maxExtract;
  }

  @Override
  public int getEnergy() {
    return DataComponentHelper.getInt(stack, "Energy");
  }

  @Override
  public void setEnergy(int e) {
    DataComponentHelper.setInt(stack, "Energy", Math.clamp(e, 0, capacity));
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
