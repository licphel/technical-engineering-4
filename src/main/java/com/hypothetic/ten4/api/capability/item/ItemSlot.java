package com.hypothetic.ten4.api.capability.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ItemSlot implements IItemSlot, IItemHandlerModifiable {
  protected @Nullable Predicate<ItemStack> validator;
  protected SlotOption type = SlotOption.BOTH;
  protected ItemStack stack = ItemStack.EMPTY;

  public ItemSlot() {
  }

  public ItemSlot(SlotOption type) {
    this.type = type;
  }

  public ItemSlot setValidator(@Nullable Predicate<ItemStack> v) {
    validator = v;
    return this;
  }

  @Override
  public ItemStack getStack() {
    return stack;
  }

  @Override
  public void setStack(ItemStack stack) {
    this.stack = stack;
  }

  @Override
  public boolean isEmpty() {
    return stack.isEmpty();
  }

  @Override
  public boolean isItemValid(ItemStack s) {
    if (!type.canReceive()) {
      return false;
    }
    return validator == null || validator.test(s);
  }

  @Override
  public boolean canExtract() {
    return type.canExtract();
  }

  @Override
  public int getSlotLimit() {
    if (stack.isEmpty()) {
      return Integer.MAX_VALUE;
    }
    return stack.getMaxStackSize();
  }

  @Override
  public SlotOption getType() {
    return type;
  }

  public ItemSlot setType(SlotOption t) {
    type = t;
    return this;
  }

  public void writeToNBT(HolderLookup.Provider reg, CompoundTag tag) {
    if (!stack.isEmpty()) {
      tag.put("Item", stack.save(reg));
    }
  }

  public ItemSlot readFromNBT(HolderLookup.Provider reg, CompoundTag tag) {
    if (tag.contains("Item")) {
      stack = ItemStack.parse(reg, tag.getCompound("Item")).orElse(ItemStack.EMPTY);
    }
    return this;
  }

  @Override
  public int getSlots() {
    return 1;
  }

  @Override
  public ItemStack getStackInSlot(int s) {
    return stack;
  }

  @Override
  public ItemStack insertItem(int s, ItemStack in, boolean sim) {
    return insertItem(in, sim, false);
  }

  @Override
  public ItemStack extractItem(int s, int amt, boolean sim) {
    return extractItem(amt, sim, false);
  }

  @Override
  public int getSlotLimit(int s) {
    return getSlotLimit();
  }

  @Override
  public boolean isItemValid(int s, ItemStack st) {
    return isItemValid(st);
  }

  public ItemStack insertItem(ItemStack incoming, boolean sim, boolean force) {
    if (incoming.isEmpty()) {
      return incoming;
    }
    if (!force && !isItemValid(incoming)) {
      return incoming;
    }
    int limit = getSlotLimit();
    if (!stack.isEmpty()) {
      if (!ItemStack.isSameItemSameComponents(stack, incoming)) {
        return incoming;
      }
      limit -= stack.getCount();
    }
    if (limit <= 0) {
      return incoming;
    }
    int take = Math.min(incoming.getCount(), limit);
    if (!sim && take > 0) {
      if (stack.isEmpty()) {
        stack = incoming.copyWithCount(take);
      } else {
        stack.grow(take);
      }
    }
    return take >= incoming.getCount() ? ItemStack.EMPTY : incoming.copyWithCount(incoming.getCount() - take);
  }

  public ItemStack extractItem(int amount, boolean sim, boolean force) {
    if (amount <= 0 || stack.isEmpty()) {
      return ItemStack.EMPTY;
    }
    if (!force && !canExtract()) {
      return ItemStack.EMPTY;
    }
    int take = Math.min(amount, stack.getCount());
    ItemStack result = stack.copyWithCount(take);
    if (!sim && take > 0) {
      if (take >= stack.getCount()) {
        stack = ItemStack.EMPTY;
      } else {
        stack.shrink(take);
      }
    }
    return result;
  }

  @Override
  public void setStackInSlot(int slot, ItemStack stack) {
    this.stack = stack.copy();
  }
}
