package com.hypothetic.ten4.lib.blockentity;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface ILootProvider {
  void getLoot(List<ItemStack> loot);
}
