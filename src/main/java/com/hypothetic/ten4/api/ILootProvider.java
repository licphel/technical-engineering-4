package com.hypothetic.ten4.api;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface ILootProvider {
  void getLoot(List<ItemStack> loot);
}
