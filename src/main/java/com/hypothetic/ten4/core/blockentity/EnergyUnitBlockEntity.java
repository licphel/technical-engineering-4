package com.hypothetic.ten4.core.blockentity;

import com.hypothetic.ten4.api.blockentity.ITickable;
import com.hypothetic.ten4.api.blockentity.device.AugmentableDeviceBlockEntity;
import com.hypothetic.ten4.api.blockentity.device.DeviceInfo;
import com.hypothetic.ten4.api.capability.item.ItemSlot;
import com.hypothetic.ten4.api.capability.item.SlotOption;
import com.hypothetic.ten4.api.container.AugmentableContainerMenu;
import com.hypothetic.ten4.api.container.ContainerMenuLayout;
import com.hypothetic.ten4.api.container.sync.BuiltinSyncedFields;
import com.hypothetic.ten4.api.container.sync.Syncer;
import com.hypothetic.ten4.core.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class EnergyUnitBlockEntity extends AugmentableDeviceBlockEntity implements ITickable {
  public EnergyUnitBlockEntity(BlockPos pos, BlockState state) {
    super(pos, state);
  }

  @Override
  protected DeviceInfo makeDeviceInfo() {
    return OtherTiers.ENERGY_UNIT.get()
        .addSlot(new ItemSlot(SlotOption.BOTH, 1).setValidator(this::isValidInput))
        .addSlot(new ItemSlot(SlotOption.BOTH, 1).setValidator(this::isValidInput));
  }

  @Override
  protected void registerAdditionalSyncFields(Syncer syncer) {
    syncer.register(BuiltinSyncedFields.ENERGY);
    syncer.register(BuiltinSyncedFields.MAX_ENERGY);
  }

  @Override
  public boolean isValidInput(ItemStack stack) {
    return !isItemStrictInput() || stack.getCapability(Capabilities.EnergyStorage.ITEM) != null;
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int cid, Inventory inv, Player p) {
    ContainerMenuLayout layout = new ContainerMenuLayout()
        .add(0, 44, 35)
        .add(1, 116, 35);
    return new AugmentableContainerMenu(ModMenus.ENERGY_UNIT.get(), cid, inv, this, layout);
  }

  @Override
  public void tick() {
    if (level == null || level.isClientSide()) {
      return;
    }

    boolean sig = isSignalEnabled();
    setActive(energy > 0 && sig);

    if (sig) {
      queuedPushPull();

      // Slot 0: input — drain the item's energy into the unit
      ItemStack input = inventory.getStackInSlot(0);
      if (!input.isEmpty()) {
        IEnergyStorage itemEnergy = input.getCapability(Capabilities.EnergyStorage.ITEM);
        if (itemEnergy != null) {
          int space = getEnergyCapacity() - getEnergy();
          int toMove = Math.min(space, getEnergyThroughput());
          if (toMove > 0) {
            setEnergy(getEnergy() + itemEnergy.extractEnergy(toMove, false));
          }
        }
      }

      // Slot 1: output — charge the item from the unit
      ItemStack output = inventory.getStackInSlot(1);
      if (!output.isEmpty()) {
        IEnergyStorage itemEnergy = output.getCapability(Capabilities.EnergyStorage.ITEM);
        if (itemEnergy != null) {
          int toMove = Math.min(itemEnergy.receiveEnergy(Integer.MAX_VALUE, true),
              Math.min(getEnergy(), getEnergyThroughput()));
          if (toMove > 0) {
            setEnergy(getEnergy() - itemEnergy.receiveEnergy(toMove, false));
          }
        }
      }

      setChanged();
    }

    syncer.set(BuiltinSyncedFields.ENERGY, getEnergy());
    syncer.set(BuiltinSyncedFields.MAX_ENERGY, getEnergyCapacity());
    synchronizeBasicData();
  }

  @Override
  public ItemStack createSelfStack() {
    ItemStack stack = super.createSelfStack();
    CompoundTag beTag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    beTag.putInt("Energy", getEnergy());
    beTag.putInt("EnergyCapacity", getEnergyCapacity());
    stack.set(DataComponents.CUSTOM_DATA, CustomData.of(beTag));
    return stack;
  }

  @Override
  public void onPlaced(@Nullable LivingEntity entity, ItemStack stack) {
    super.onPlaced(entity, stack);
    if (level == null || level.isClientSide() || stack.isEmpty()) {
      return;
    }
    CustomData cd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
    if (cd.isEmpty()) {
      return;
    }
    CompoundTag beTag = cd.copyTag();
    if (beTag.contains("Energy")) {
      setEnergy(beTag.getInt("Energy"));
    }
  }
}
