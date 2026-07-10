package com.hypothetic.ten4.lib.capability.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ItemInventory implements IItemHandlerModifiable, Container {
  private static final ItemSlot EMPTY = new ItemSlot(SlotOption.BOTH);
  private final List<ItemSlot> slots = new ArrayList<>();
  private @Nullable Runnable changeListener;
  private @Nullable Predicate<Player> stillValidCheck;

  public ItemInventory add(ItemSlot slot) {
    slots.add(slot);
    return this;
  }

  public void setChangeListener(@Nullable Runnable listener) {
    this.changeListener = listener;
  }

  public void setStillValidCheck(@Nullable Predicate<Player> check) {
    this.stillValidCheck = check;
  }

  public int size() {
    return slots.size();
  }

  public ItemSlot getSlot(int i) {
    return slots.get(i);
  }

  @Override
  public int getContainerSize() {
    return slots.size();
  }

  @Override
  public boolean isEmpty() {
    for (ItemSlot s : slots) {
      if (!s.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public ItemStack getItem(int i) {
    ItemSlot slot = getSlotSafely(i);
    return slot == null ? ItemStack.EMPTY : slot.getStackInSlot(0);
  }

  @Override
  public ItemStack removeItem(int i, int amount) {
    ItemSlot slot = getSlotSafely(i);
    if (slot == null) {
      return ItemStack.EMPTY;
    }
    ItemStack taken = slot.extractItem(0, amount, false);
    if (!taken.isEmpty()) {
      setChanged();
    }
    return taken;
  }

  @Override
  public ItemStack removeItemNoUpdate(int i) {
    ItemSlot slot = getSlotSafely(i);
    if (slot == null) {
      return ItemStack.EMPTY;
    }
    ItemStack s = slot.getStack();
    slot.setStack(ItemStack.EMPTY);
    return s;
  }

  @Override
  public void setItem(int i, ItemStack stack) {
    ItemSlot slot = getSlotSafely(i);
    if (slot != null) {
      slot.setStack(stack);
      setChanged();
    }
  }

  @Override
  public void setChanged() {
    if (changeListener != null) {
      changeListener.run();
    }
  }

  @Override
  public boolean stillValid(Player player) {
    return stillValidCheck == null || stillValidCheck.test(player);
  }

  @Override
  public boolean canPlaceItem(int i, ItemStack stack) {
    return isItemValid(i, stack);
  }

  @Override
  public int countItem(Item item) {
    int c = 0;
    for (ItemSlot s : slots) {
      ItemStack st = s.getStack();
      if (st.is(item)) {
        c += st.getCount();
      }
    }
    return c;
  }

  @Override
  public boolean hasAnyMatching(Predicate<ItemStack> pred) {
    for (ItemSlot s : slots) {
      if (pred.test(s.getStack())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void clearContent() {
    for (ItemSlot s : slots) {
      s.setStack(ItemStack.EMPTY);
    }
    setChanged();
  }

  @Override
  public int getSlots() {
    return slots.size();
  }

  @Override
  public ItemStack getStackInSlot(int i) {
    return getItem(i);
  }

  @Override
  public ItemStack insertItem(int i, ItemStack s, boolean sim) {
    ItemSlot slot = getSlotSafely(i);
    return slot == null ? s : slot.insertItem(0, s, sim);
  }

  @Override
  public ItemStack extractItem(int i, int a, boolean sim) {
    ItemSlot slot = getSlotSafely(i);
    return slot == null ? ItemStack.EMPTY : slot.extractItem(0, a, sim);
  }

  @Override
  public int getSlotLimit(int i) {
    ItemSlot slot = getSlotSafely(i);
    return slot == null ? 0 : slot.getSlotLimit(0);
  }

  @Override
  public boolean isItemValid(int i, ItemStack s) {
    ItemSlot slot = getSlotSafely(i);
    return slot != null && slot.isItemValid(0, s);
  }

  @Override
  public void setStackInSlot(int i, ItemStack stack) {
    setItem(i, stack);
  }

  public ItemStack forceInsert(ItemStack stack, List<Integer> destination, boolean sim) {
    ItemStack rem = stack.copy();
    for (int i : destination) {
      if (i < 0 || i >= slots.size()) {
        continue;
      }
      ItemStack result = slots.get(i).insertItem(rem, sim, true);
      int inserted = rem.getCount() - result.getCount();
      if (inserted > 0) {
        rem.shrink(inserted);
      }
    }
    return rem;
  }

  public ItemStack forceExtract(int amount, List<Integer> destination, boolean sim) {
    for (int i : destination) {
      if (i < 0 || i >= slots.size()) {
        continue;
      }
      if (slots.get(i).isEmpty()) {
        continue;
      }
      ItemStack result = slots.get(i).extractItem(amount, sim, true);
      if (!result.isEmpty()) {
        return result;
      }
    }
    return ItemStack.EMPTY;
  }

  public CompoundTag createTag(HolderLookup.Provider reg) {
    CompoundTag tag = new CompoundTag();
    ListTag list = new ListTag();
    for (int i = 0; i < slots.size(); i++) {
      if (!slots.get(i).isEmpty()) {
        CompoundTag e = new CompoundTag();
        e.putInt("Slot", i);
        list.add(slots.get(i).getStack().save(reg, e));
      }
    }
    tag.put("Items", list);
    return tag;
  }

  public void fromTag(CompoundTag tag, HolderLookup.Provider reg) {
    ListTag list = tag.getList("Items", CompoundTag.TAG_COMPOUND);
    for (ItemSlot s : slots) {
      s.readFromNBT(reg, new CompoundTag());
    }
    for (int i = 0; i < list.size(); i++) {
      CompoundTag e = list.getCompound(i);
      int idx = e.getInt("Slot");
      if (idx >= 0 && idx < slots.size()) {
        slots.get(idx).readFromNBT(reg, e);
      }
    }
  }

  private @Nullable ItemSlot getSlotSafely(int i) {
    return i >= 0 && i < slots.size() ? slots.get(i) : null;
  }
}
