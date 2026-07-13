package com.hypothetic.ten4.api.capability.energy;

import com.hypothetic.ten4.util.DataComponentHelper;
import net.minecraft.world.item.ItemStack;

public class ItemEnergyProvider implements IEnergyProvider {
  private final ItemStack stack;
  private final int capacity;
  private final int throughput;

  public ItemEnergyProvider(ItemStack stack, int capacity, int throughput) {
    this.stack = stack;
    this.capacity = capacity;
    this.throughput = throughput;
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
  public int getEnergyCapacity() {
    return capacity;
  }

  @Override
  public int getEnergyThroughput() {
    return throughput;
  }
}
