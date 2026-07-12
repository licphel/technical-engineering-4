package com.hypothetic.ten4.api.container;

import com.hypothetic.ten4.api.blockentity.device.AugmentableDeviceBlockEntity;
import com.hypothetic.ten4.api.capability.item.ItemInventory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import java.util.ArrayList;
import java.util.List;

public class AugmentableContainerMenu extends ContainerMenu {
  protected final List<ManualSlot> augmentSlots = new ArrayList<>();

  public AugmentableContainerMenu(MenuType<?> type, int containerId, Inventory playerInventory,
                                  AugmentableDeviceBlockEntity blockEntity, ContainerMenuLayout layout) {
    super(type, containerId, playerInventory, blockEntity, layout);
    addAugmentSlots(blockEntity);
  }

  private void addAugmentSlots(AugmentableDeviceBlockEntity be) {
    ItemInventory augments = be.getAugments();

    for (int i = 0; i < AugmentableDeviceBlockEntity.AUGMENT_CAPACITY; i++) {
      ManualSlot slot = new ManualSlot(augments, i, Integer.MAX_VALUE, Integer.MAX_VALUE);
      augmentSlots.add(slot);
      addSlot(slot);
    }
  }

  @Override
  public AugmentableDeviceBlockEntity getBlockEntity() {
    return (AugmentableDeviceBlockEntity) super.getBlockEntity();
  }

  public int getAugmentStartIndex() {
    return augmentSlots.getFirst().index;
  }

  public boolean isAugmentSlot(int slotIndex) {
    int augStart = getAugmentStartIndex();
    return slotIndex >= augStart && slotIndex < augStart + AugmentableDeviceBlockEntity.AUGMENT_CAPACITY;
  }

  public List<ManualSlot> getAugmentSlots() {
    return augmentSlots;
  }
}
