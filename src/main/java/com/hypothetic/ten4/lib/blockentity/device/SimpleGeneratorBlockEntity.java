package com.hypothetic.ten4.lib.blockentity.device;

import com.hypothetic.ten4.lib.blockentity.FaceMode;
import com.hypothetic.ten4.lib.blockentity.ITickable;
import com.hypothetic.ten4.lib.container.sync.BuiltinSyncedFields;
import com.hypothetic.ten4.lib.container.sync.Syncer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SimpleGeneratorBlockEntity extends AugmentableDeviceBlockEntity implements ITickable {
  public int fuel;
  public int maxFuel;

  public SimpleGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);

    /*
     * Generator has its upside active.
     */
    setEnergyFaceMode(Direction.UP, FaceMode.ACTIVE_EXTRACT);
  }

  @Override
  protected void initAttributes(Syncer syncer) {
    syncer.register(BuiltinSyncedFields.ENERGY);
    syncer.register(BuiltinSyncedFields.MAX_ENERGY);
    syncer.register(BuiltinSyncedFields.FUEL);
    syncer.register(BuiltinSyncedFields.MAX_FUEL);
    syncer.register(BuiltinSyncedFields.EFFICIENCY);
    syncer.register(BuiltinSyncedFields.ACTIVE);
    syncer.register(BuiltinSyncedFields.SIG_MODE);
  }

  @Override
  public void tick() {
    if (level == null || level.isClientSide()) {
      return;
    }

    setActive(fuel > 0);

    if (fuel > 0) {
      setEnergy(getEnergy() + getEfficiency());
      fuel = Math.max(fuel - getEfficiency(), 0);
    }

    if (isSignalEnabled()) {
      queuedPushPull();

      if (hasOutputSpace() && fuel <= 0) {
        int v = tryFueling(true);
        if (v > 0) {
          tryFueling(false);
          fuel = v * getBasicEfficiency();
          maxFuel = fuel;
        }
      }
    }

    syncer.set(BuiltinSyncedFields.ENERGY, getEnergy());
    syncer.set(BuiltinSyncedFields.MAX_ENERGY, getMaxEnergy());
    syncer.set(BuiltinSyncedFields.FUEL, fuel);
    syncer.set(BuiltinSyncedFields.MAX_FUEL, maxFuel);
    syncer.set(BuiltinSyncedFields.EFFICIENCY, getEfficiency());
    syncer.set(BuiltinSyncedFields.ACTIVE, isActive());
    syncer.set(BuiltinSyncedFields.SIG_MODE, sigMode.ordinal());
  }

  public boolean hasOutputSpace() {
    return getEnergy() + efficiency <= getMaxEnergy();
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
