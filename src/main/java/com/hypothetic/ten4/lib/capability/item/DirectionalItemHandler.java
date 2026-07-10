package com.hypothetic.ten4.lib.capability.item;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import org.jetbrains.annotations.Nullable;

public class DirectionalItemHandler implements IItemHandler {
  private final IDirectionalItemProvider host;
  private final @Nullable Direction side;

  public DirectionalItemHandler(IDirectionalItemProvider host, @Nullable Direction side) {
    this.host = host;
    this.side = side;
  }

  @Override
  public int getSlots() {
    return host.getInventory().getSlots();
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    return host.getInventory().getStackInSlot(slot);
  }

  @Override
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
    if (stack.isEmpty() || !host.canReceiveItem(side)) {
      return stack;
    }
    if (!host.isItemValid(slot, stack)) {
      return stack;
    }
    int cap = host.getMaxItemReceive(side);
    if (cap <= 0) {
      return stack;
    }
    return host.getInventory().insertItem(slot, stack.copyWithCount(Math.min(stack.getCount(), cap)), simulate);
  }

  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    if (amount <= 0 || !host.canExtractItem(side)) {
      return ItemStack.EMPTY;
    }
    int cap = host.getMaxItemExtract(side);
    if (cap <= 0) {
      return ItemStack.EMPTY;
    }
    return host.getInventory().extractItem(slot, Math.min(amount, cap), simulate);
  }

  @Override
  public int getSlotLimit(int slot) {
    return host.getInventory().getSlotLimit(slot);
  }

  @Override
  public boolean isItemValid(int slot, ItemStack stack) {
    return host.canReceiveItem(side) && host.isItemValid(slot, stack);
  }
}
