package com.hypothetic.ten4.api.blockentity.device;

import com.hypothetic.ten4.api.ITickable;
import com.hypothetic.ten4.api.container.sync.BuiltinSyncedFields;
import com.hypothetic.ten4.api.container.sync.Syncer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SimpleGeneratorBlockEntity extends AugmentableDeviceBlockEntity implements ITickable {
  public int fuel;
  public int maxFuel;

  public SimpleGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  protected void registerAdditionalSyncFields(Syncer syncer) {
    syncer.register(BuiltinSyncedFields.ENERGY);
    syncer.register(BuiltinSyncedFields.MAX_ENERGY);
    syncer.register(BuiltinSyncedFields.FUEL);
    syncer.register(BuiltinSyncedFields.MAX_FUEL);
  }

  @Override
  public void tick() {
    if (level == null || level.isClientSide()) {
      return;
    }

    setActive(fuel > 0);

    if (fuel > 0) {
      setEnergy(getEnergy() + getActualPower());
      fuel = Math.max(fuel - getActualPower(), 0);
      setChanged();
    }

    if (isSignalEnabled()) {
      queuedPushPull();

      if (hasOutputSpace() && fuel <= 0) {
        int v = tryFueling(true);
        if (v > 0) {
          tryFueling(false);
          fuel = v * info.power;
          maxFuel = fuel;
        }
      }

      setChanged();
    }

    syncer.set(BuiltinSyncedFields.ENERGY, getEnergy());
    syncer.set(BuiltinSyncedFields.MAX_ENERGY, this.getEnergyCapacity());
    syncer.set(BuiltinSyncedFields.FUEL, fuel);
    syncer.set(BuiltinSyncedFields.MAX_FUEL, maxFuel);
    syncer.set(BuiltinSyncedFields.POWER, getActualPower());
    synchronizeBasicData();
  }

  public boolean hasOutputSpace() {
    return getEnergy() + getActualPower() <= this.getEnergyCapacity();
  }

  public abstract int tryFueling(boolean simulate);

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.saveAdditional(tag, reg);
    tag.putInt("Fuel", fuel);
    tag.putInt("MaxFuel", maxFuel);
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.loadAdditional(tag, reg);
    fuel = tag.getInt("Fuel");
    maxFuel = tag.getInt("MaxFuel");
  }
}
