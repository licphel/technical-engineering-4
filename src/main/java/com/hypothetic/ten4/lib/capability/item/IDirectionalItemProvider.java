package com.hypothetic.ten4.lib.capability.item;

import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import org.jetbrains.annotations.Nullable;
import java.util.Queue;

public interface IDirectionalItemProvider {
  IItemHandler getInventory();

  Container getContainer();

  int getMaxItemExtract(@Nullable Direction d);

  int getMaxItemReceive(@Nullable Direction d);

  default boolean isItemValid(int slot, ItemStack stack) {
    return getInventory().isItemValid(slot, stack);
  }

  default boolean canExtractItem(@Nullable Direction d) {
    return getMaxItemExtract(d) > 0;
  }

  default boolean canReceiveItem(@Nullable Direction d) {
    return  getMaxItemReceive(d) > 0;
  }

  Queue<Direction> getItemPushingCycle();

  Queue<Direction> getItemPullingCycle();
}
