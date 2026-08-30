package com.hypothetic.ten4.api.blockentity.device;

import com.hypothetic.ten4.api.container.sync.BuiltinSyncedFields;
import com.hypothetic.ten4.api.container.sync.Syncer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ComplexRecipeGeneratorBlockEntity extends ComplexRecipeDeviceBlockEntity {
  private int fuel;
  private int maxFuel;

  public ComplexRecipeGeneratorBlockEntity(BlockPos pos, BlockState state) {
    super(pos, state);
  }

  @Override
  protected void registerAdditionalSyncFields(Syncer syncer) {
    super.registerAdditionalSyncFields(syncer);
    syncer.register(BuiltinSyncedFields.FUEL);
    syncer.register(BuiltinSyncedFields.MAX_FUEL);
  }

  @Override
  protected void process() {
    if (isSignalEnabled() && hasEnoughOutputSpace()) {
      if (fuel > 0) {
        setEnergy(getEnergy() + getActualPower());
        fuel = Math.max(fuel - getActualPower(), 0);
        setChanged();
        triggerSound();
        setActive(true);
      } else {
        recipe = findRecipe();
        if (recipe != null) {
          fuel = recipe.time() * info.power;
          maxFuel = fuel;
          finish();
        } else {
          setActive(false);
        }
      }
    } else {
      setActive(false);
    }

    setChanged();

    syncer.set(BuiltinSyncedFields.FUEL, fuel);
    syncer.set(BuiltinSyncedFields.MAX_FUEL, maxFuel);
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.loadAdditional(tag, reg);
    fuel = tag.getInt("Fuel");
    maxFuel = tag.getInt("MaxFuel");
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.saveAdditional(tag, reg);
    tag.putInt("Fuel", fuel);
    tag.putInt("MaxFuel", maxFuel);
  }

  @Override
  protected boolean hasEnoughOutputSpace() {
    return hasEnergyOutputSpace() && super.hasEnoughOutputSpace();
  }

  protected boolean hasEnergyOutputSpace() {
    return getEnergy() + getActualPower() <= getEnergyCapacity();
  }
}
