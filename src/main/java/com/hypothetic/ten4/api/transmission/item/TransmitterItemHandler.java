package com.hypothetic.ten4.api.transmission.item;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class TransmitterItemHandler implements IItemHandler {
  final ItemTransmitter transmitter;
  final Direction side;

  public TransmitterItemHandler(ItemTransmitter transmitter, Direction side) {
    this.transmitter = transmitter;
    this.side = side;
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
    if (stack.isEmpty() || transmitter.getNetwork() == null) {
      return stack; // no network → reject
    }
    ItemTransmitter.TransitEntry e = transmitter.transitEntry;
    // Clean up stale empty entry
    if (e != null && e.stack.isEmpty()) {
      e = null;
    }
    int space = getSlotLimit(slot);
    if (e != null) {
      if (!ItemStack.isSameItemSameComponents(e.stack, stack)) {
        return stack;
      }
      space -= e.stack.getCount();
      if (space <= 0) {
        return stack;
      }
    }
    // Check route before accepting
    byte[] route = e == null ? RouteFinder.findRoute(transmitter.getNetwork(), transmitter.getBlockPos(), stack) : e.route;
    if (route.length == 0) {
      return stack; // no valid destination → reject
    }
    int limit = Math.min(stack.getCount(), space);
    if (!simulate) {
      if (e == null) {
        e = new ItemTransmitter.TransitEntry();
        e.id = transmitter.allocateId();
        e.stack = stack.copyWithCount(limit);
        e.entrySide = (byte) side.ordinal();
        e.route = route;
        e.index = 0;
        e.progress = 0;
        e.exitSide = route[0];
        transmitter.transitEntry = e;
      } else {
        e.stack.grow(limit);
      }
    }
    ItemStack leftover = stack.copy();
    leftover.shrink(limit);
    return leftover.isEmpty() ? ItemStack.EMPTY : leftover;
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
