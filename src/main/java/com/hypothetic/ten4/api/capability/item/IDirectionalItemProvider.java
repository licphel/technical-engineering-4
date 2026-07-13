package com.hypothetic.ten4.api.capability.item;

import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;

public interface IDirectionalItemProvider {
  IItemHandler getInventory();

  Container getContainer();

  int getItemThroughput();

  default boolean isItemValid(int slot, ItemStack stack) {
    return getInventory().isItemValid(slot, stack);
  }

  boolean canExtractItem(@Nullable Direction d);

  boolean canReceiveItem(@Nullable Direction d);

  Queue<Direction> getItemPushingCycle();

  Queue<Direction> getItemPullingCycle();
}
