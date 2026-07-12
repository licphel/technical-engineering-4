package com.hypothetic.ten4.api.capability.item;

import net.minecraft.world.item.ItemStack;

public interface IItemSlot {
  ItemStack getStack();

  void setStack(ItemStack stack);

  boolean isEmpty();

  boolean isItemValid(ItemStack stack);

  boolean canExtract();

  int getSlotLimit();

  SlotOption getType();
}
