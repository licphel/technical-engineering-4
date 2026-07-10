package com.hypothetic.ten4.lib.container;

import com.hypothetic.ten4.lib.blockentity.device.AbstractDeviceBlockEntity;
import com.hypothetic.ten4.lib.blockentity.device.AugmentableDeviceBlockEntity;
import com.hypothetic.ten4.lib.capability.item.ItemInventory;
import com.hypothetic.ten4.lib.container.sync.SyncedFieldReader;
import com.hypothetic.ten4.lib.item.IAugment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class ContainerMenu extends AbstractContainerMenu {
  private static final int PLAYER_MIN = 0;
  private static final int PLAYER_MAX = 36;
  private static final int HOTBAR_MIN = 0;
  private static final int HOTBAR_MAX = 9;
  private static final double INTERACTABLE_DISTANCE = 4.0;

  protected final AbstractDeviceBlockEntity blockEntity;
  protected final SyncedFieldReader attrs;
  protected final ContainerData data;

  public ContainerMenu(MenuType<?> type, int containerId, Inventory playerInventory,
                       AbstractDeviceBlockEntity blockEntity, ContainerMenuLayout layout) {
    super(type, containerId);
    this.blockEntity = blockEntity;
    this.data = blockEntity.getContainerData();
    this.attrs = blockEntity.getAttributes().createReader();

    addDataSlots(data);
    addPlayerSlots(playerInventory); // head [0...36) slots. for quickMoveStack

    for (int[] s : layout.getSlots()) {
      addSlot(new ManualSlot((ItemInventory) blockEntity.getInventory(), s[0], s[1], s[2]));
    }
  }

  public static boolean isInBackpack(int slot) {
    return slot >= PLAYER_MIN && slot < PLAYER_MAX;
  }

  public static boolean isInHotbar(int slot) {
    return slot >= HOTBAR_MIN && slot < HOTBAR_MAX;
  }

  protected void addPlayerSlots(Inventory playerInventory) {
    for (int r = 0; r < 3; r++) {
      for (int c = 0; c < 9; c++) {
        addSlot(new Slot(playerInventory, c + r * 9 + 9, 8 + c * 18, 84 + r * 18));
      }
    }
    for (int c = 0; c < 9; c++) {
      addSlot(new Slot(playerInventory, c, 8 + c * 18, 142));
    }
  }

  public AbstractDeviceBlockEntity getBlockEntity() {
    return blockEntity;
  }

  public SyncedFieldReader fieldsReader() {
    return attrs;
  }

  @Override
  public ItemStack quickMoveStack(Player player, int slotId) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = slots.get(slotId);

    if (slot.hasItem()) {
      ItemStack slotStack = slot.getItem();
      itemstack = slotStack.copy();

      if (itemstack.is(Items.OBSIDIAN)) {
        // Handle augment slots
        if (isInBackpack(slotId)) {
          if (this instanceof AugmentableContainerMenu acm) {
            int augStart = acm.getAugmentStartIndex();
            if (!moveItemStackTo(slotStack, augStart, augStart + AugmentableDeviceBlockEntity.AUGMENT_CAPACITY, false)) {
              return ItemStack.EMPTY;
            }
          }
          return ItemStack.EMPTY;
        } else {
          if (!moveItemStackTo(slotStack, PLAYER_MIN, PLAYER_MAX, false)) {
            return ItemStack.EMPTY;
          }
        }
      } else {
        if (isInBackpack(slotId)) {
          if (!this.moveItemStackTo(slotStack, PLAYER_MAX, slots.size(), false)) {
            if (!isInHotbar(slotId)) {
              if (!this.moveItemStackTo(slotStack, HOTBAR_MIN, HOTBAR_MAX, false)) {
                return ItemStack.EMPTY;
              }
            }
          }
        } else if (!this.moveItemStackTo(slotStack, PLAYER_MIN, PLAYER_MAX, false)) {
          return ItemStack.EMPTY;
        }
      }

      if (slotStack.getCount() == 0) {
        slot.set(ItemStack.EMPTY);
      } else {
        slot.setChanged();
      }
      if (slotStack.getCount() == itemstack.getCount()) {
        return ItemStack.EMPTY;
      }
      slot.onTake(player, slotStack);
    }

    return itemstack;
  }

  @Override
  public boolean stillValid(Player player) {
    Level level = blockEntity.getLevel();
    BlockPos pos = blockEntity.getBlockPos();

    return level != null && level.getBlockEntity(pos) == blockEntity
        && player.canInteractWithBlock(pos, INTERACTABLE_DISTANCE);
  }
}
