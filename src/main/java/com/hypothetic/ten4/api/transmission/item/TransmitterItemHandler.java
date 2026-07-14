package com.hypothetic.ten4.api.transmission.item;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class TransmitterItemHandler implements IItemHandler {
  ItemTransmitter transmitter;

  public TransmitterItemHandler(ItemTransmitter transmitter) {
    this.transmitter = transmitter;
  }

  @Override
  public int getSlots() {
    return 1;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    ItemTransmitter.TransitEntry e = transmitter.transitEntry;
    return e != null ? e.stack.copy() : ItemStack.EMPTY;
  }

  @Override
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
    return stack; // push via network, not direct insertion
  }

  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    ItemTransmitter.TransitEntry e = transmitter.transitEntry;
    if (e == null) {
      return ItemStack.EMPTY;
    }
    int take = Math.min(amount, e.stack.getCount());
    ItemStack ret = e.stack.copyWithCount(take);
    if (!simulate) {
      e.stack.shrink(take);
      if (e.stack.isEmpty()) {
        e.stack = ItemStack.EMPTY;
      }
    }
    return ret;
  }

  @Override
  public int getSlotLimit(int slot) {
    return transmitter.getSlotCapacity();
  }

  @Override
  public boolean isItemValid(int slot, ItemStack stack) {
    return true;
  }
}
