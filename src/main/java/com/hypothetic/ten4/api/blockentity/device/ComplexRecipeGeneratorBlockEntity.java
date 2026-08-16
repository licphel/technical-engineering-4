package com.hypothetic.ten4.api.blockentity.device;

import com.hypothetic.ten4.api.container.sync.BuiltinSyncedFields;
import com.hypothetic.ten4.api.container.sync.Syncer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ComplexRecipeGeneratorBlockEntity extends ComplexRecipeDeviceBlockEntity {
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
  public void tick() {
    super.tick();

    if (level != null && !level.isClientSide()) {
      // Fuel gauge mirrors REMAINING recipe progress: fuel% = 1 - progress/maxProgress.
      syncer.set(BuiltinSyncedFields.MAX_FUEL, getMaxProgress());
      syncer.set(BuiltinSyncedFields.FUEL, getMaxProgress() - getProgress());
    }
  }

  @Override
  protected boolean hasEnoughOutputSpace() {
    return hasEnergyOutputSpace() && super.hasEnoughOutputSpace();
  }

  protected boolean hasEnergyOutputSpace() {
    return getEnergy() + getActualPower() <= getEnergyCapacity();
  }

  @Override
  protected void process() {
    setActive(progress != maxProgress);

    if (isSignalEnabled()) {
      lastRecipe = recipe;
      recipe = findRecipe();

      if (recipe == null) {
        progress = 0;
        setChanged();
        return;
      }

      maxProgress = getRecipeTime(recipe) * info.power;
      int generated = getActualPower();
      if (generated <= 0) {
        return;
      } else {
        triggerSound();
      }

      progress += generated;
      setEnergy(getEnergy() + generated);

      if (progress >= maxProgress) {
        if (hasEnoughOutputSpace()) {
          finish();
          progress = 0;
        }
        recipe = null;
      }

      setChanged();
    } else {
      if (progress > 0 && !hasEnergyOutputSpace()) {
        progress = Math.max(0, progress - info.power);
        setChanged();
      }
    }
  }
}
