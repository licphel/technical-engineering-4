package com.hypothetic.ten4.lib.container;

import com.hypothetic.ten4.lib.capability.item.ItemInventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ManualSlot extends Slot {
  private final ItemInventory inventory;
  private final int slot;
  private boolean active = true;

  public ManualSlot(ItemInventory inv, int slot, int x, int y) {
    super(inv, slot, x, y);
    this.inventory = inv;
    this.slot = slot;
  }

  public ManualSlot replace(AbstractContainerMenu menu, int newX, int newY) {
    ManualSlot newSlot = new ManualSlot(inventory, slot, newX, newY);
    newSlot.setActive(active);
    newSlot.index = index;
    menu.slots.set(index, newSlot);
    return newSlot;
  }

  @Override
  public boolean mayPlace(ItemStack stack) {
    return isActive() && inventory.isItemValid(slot, stack);
  }

  @Override
  public ItemStack getItem() {
    return inventory.getItem(slot);
  }

  @Override
  public void set(ItemStack stack) {
    inventory.setItem(slot, stack);
    setChanged();
  }

  @Override
  public int getMaxStackSize() {
    return inventory.getSlotLimit(slot);
  }

  @Override
  public int getMaxStackSize(ItemStack stack) {
    return Math.min(stack.getMaxStackSize(), inventory.getSlotLimit(slot));
  }

  @Override
  public ItemStack remove(int amount) {
    return inventory.forceExtract(amount, List.of(slot), false);
  }

  @Override
  public boolean mayPickup(Player player) {
    /*
     * Always removable.
     * Do not let !canExtract check forbid player picking up action.
     */
    return true;
  }

  @Override
  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
