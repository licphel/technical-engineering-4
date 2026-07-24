package com.hypothetic.ten4.api.blockentity.device;

import com.hypothetic.ten4.api.capability.item.ItemInventory;
import com.hypothetic.ten4.api.capability.item.ItemSlot;
import com.hypothetic.ten4.api.capability.item.SlotOption;
import com.hypothetic.ten4.api.item.IAugment;
import com.hypothetic.ten4.api.item.MuteAugment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public abstract class AugmentableDeviceBlockEntity extends AbstractDeviceBlockEntity {
  public static final int AUGMENT_CAPACITY = 4;
  public static final int AUGMENT_SLOT_LIMIT = 4;

  protected ItemInventory augments;

  public AugmentableDeviceBlockEntity(BlockPos pos, BlockState state) {
    super(pos, state);

    augments = new ItemInventory();
    augments.setChangeListener(this::setChanged);
    augments.setStillValidCheck(p -> level != null && level.getBlockEntity(worldPosition) == this);
    for (int i = 0; i < AUGMENT_CAPACITY; i++) {
      augments.add(new ItemSlot(SlotOption.BOTH)
          .setValidator(s -> s.getItem() instanceof IAugment<?>)
          .setSlotLimit(AUGMENT_SLOT_LIMIT)
      );
    }
  }

  public int countAugment(Class<? extends IAugment<?>> clazz) {
    int j = 0;
    for (int i = 0; i < augments.size(); i++) {
      ItemStack stack = augments.getItem(i);
      if (clazz.isAssignableFrom(stack.getItem().getClass())) {
        j += stack.getCount();
      }
    }
    return j;
  }

  public int applyAugments(IAugment.AugmentableField entry, int value) {
    for (int i = 0; i < augments.size(); i++) {
      ItemStack stack = augments.getItem(i);

      if (stack.getItem() instanceof IAugment<?> augment) {
        for (int j = 0; j < stack.getCount(); j++) {
          value = augment.modifier(entry, value);
        }
      }
    }
    return value;
  }

  public ItemInventory getAugments() {
    return augments;
  }

  @Override
  public int getEnergyCapacity() {
    return applyAugments(IAugment.AugmentableField.ENERGY_CAPACITY, super.getEnergyCapacity());
  }

  @Override
  public int getEnergyThroughput() {
    return applyAugments(IAugment.AugmentableField.ENERGY_THROUGHPUT, super.getEnergyThroughput());
  }

  @Override
  public int getFluidThroughput() {
    return applyAugments(IAugment.AugmentableField.FLUID_THROUGHPUT, super.getFluidThroughput());
  }

  @Override
  public int getItemThroughput() {
    return applyAugments(IAugment.AugmentableField.ITEM_THROUGHPUT, super.getItemThroughput());
  }

  @Override
  public int getActualPower() {
    return applyAugments(IAugment.AugmentableField.POWER, super.getActualPower());
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.loadAdditional(tag, reg);

    augments.fromTag(tag.getCompound("augments"), reg);
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.saveAdditional(tag, reg);

    tag.put("augments", augments.createTag(reg));
  }

  @Override
  public void getLoot(List<ItemStack> loot, boolean shouldDropSelf) {
    super.getLoot(loot, shouldDropSelf);

    for (int i = 0; i < augments.getSlots(); i++) {
      if (augments.getStackInSlot(i).isEmpty()) {
        continue;
      }
      loot.add(augments.getStackInSlot(i));
    }
  }

  public void triggerSound() {
    if (countAugment(MuteAugment.class) == 0) {
      onSoundPlay();
    }
  }
}
