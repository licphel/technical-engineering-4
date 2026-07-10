package com.hypothetic.ten4.lib.capability.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public interface IItemSlot {
  ItemStack getStack();

  void setStack(ItemStack stack);

  boolean isEmpty();

  boolean isItemValid(ItemStack stack);

  boolean canExtract();

  int getSlotLimit();

  SlotOption getType();

  CompoundTag writeToNBT(HolderLookup.Provider reg, CompoundTag tag);
}
