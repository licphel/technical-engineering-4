package com.hypothetic.ten4.lib.blockentity.device;

import com.hypothetic.ten4.lib.capability.item.ItemInventory;
import com.hypothetic.ten4.lib.capability.item.ItemSlot;
import com.hypothetic.ten4.lib.capability.item.SlotOption;
import com.hypothetic.ten4.lib.item.IAugment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AugmentableDeviceBlockEntity extends AbstractDeviceBlockEntity {
  protected ItemInventory augments = new ItemInventory();

  public AugmentableDeviceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  protected void initializeCapabilities() {
    super.initializeCapabilities();

    augments.setChangeListener(this::setChanged);
    augments.setStillValidCheck(p -> level != null && level.getBlockEntity(worldPosition) == this);
    for (int i = 0; i < AUGMENT_CAPACITY; i++) {
      augments.add(new ItemSlot(SlotOption.BOTH).setValidator(s -> s.is(Items.OBSIDIAN)));
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

  public int applyAugments(IAugment.ModifiableEntry entry, int value) {
    for (int i = 0; i < augments.size(); i++) {
      ItemStack stack = augments.getItem(i);

      if (stack.getItem() instanceof IAugment<?> augment) {
        value = augment.modifier(entry, value);
      }
    }
    return value;
  }

  public ItemInventory getAugments() {
    return augments;
  }

  @Override
  public int getEfficiency() {
    return applyAugments(IAugment.ModifiableEntry.EFFICIENCY, super.getEfficiency());
  }

  @Override
  public int getMaxEnergy() {
    return applyAugments(IAugment.ModifiableEntry.ENERGY_CAPACITY, super.getMaxEnergy());
  }

  @Override
  public int getMaxEnergyExtract(@Nullable Direction d) {
    return applyAugments(IAugment.ModifiableEntry.MAX_ENERGY_EXTRACT, super.getMaxEnergyExtract(d));
  }

  @Override
  public int getMaxEnergyReceive(@Nullable Direction d) {
    return applyAugments(IAugment.ModifiableEntry.MAX_ENERGY_RECEIVE, super.getMaxEnergyReceive(d));
  }

  @Override
  public int getMaxItemExtract(@Nullable Direction d) {
    return applyAugments(IAugment.ModifiableEntry.MAX_ITEM_EXTRACT, super.getMaxItemExtract(d));
  }

  @Override
  public int getMaxItemReceive(@Nullable Direction d) {
    return applyAugments(IAugment.ModifiableEntry.MAX_ITEM_RECEIVE, super.getMaxItemReceive(d));
  }

  @Override
  public int getMaxFluidExtract(@Nullable Direction d) {
    return applyAugments(IAugment.ModifiableEntry.MAX_FLUID_EXTRACT, super.getMaxFluidExtract(d));
  }

  @Override
  public int getMaxFluidReceive(@Nullable Direction d) {
    return applyAugments(IAugment.ModifiableEntry.MAX_FLUID_RECEIVE, super.getMaxFluidReceive(d));
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.saveAdditional(tag, reg);

    tag.put("augments", augments.createTag(reg));
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.loadAdditional(tag, reg);

    augments.fromTag(tag.getCompound("augments"), reg);
  }

  @Override
  public void getLoot(List<ItemStack> loot) {
    super.getLoot(loot);

    for (int i = 0; i < augments.getSlots(); i++) {
      if(augments.getStackInSlot(i).isEmpty()) {
        continue;
      }
      loot.add(augments.getStackInSlot(i));
    }
  }
}
