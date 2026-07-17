package com.hypothetic.ten4.api.blockentity.device;

import com.hypothetic.ten4.api.blockentity.ITickable;
import com.hypothetic.ten4.api.container.sync.BuiltinSyncedFields;
import com.hypothetic.ten4.api.container.sync.Syncer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRecipeDeviceBlockEntity<I extends RecipeInput, T extends Recipe<I>>
    extends AugmentableDeviceBlockEntity implements ITickable {
  protected final List<Integer> inputSlots = new ArrayList<>();
  protected final List<Integer> inputTanks = new ArrayList<>();
  protected final List<Integer> outputSlots = new ArrayList<>();
  protected final List<Integer> outputTanks = new ArrayList<>();
  protected final RecipeType<T> recipeType;
  protected @Nullable T recipe;
  protected @Nullable T lastRecipe;
  protected int progress = 0;
  protected int maxProgress = 0;

  public AbstractRecipeDeviceBlockEntity(BlockPos pos, BlockState state) {
    super(pos, state);
    recipeType = getRecipeType();
  }

  @Override
  public void onLoad() {
    super.onLoad();
    initializeRecipeAutomation();
  }

  @Override
  protected void registerAdditionalSyncFields(Syncer syncer) {
    syncer.register(BuiltinSyncedFields.ENERGY);
    syncer.register(BuiltinSyncedFields.MAX_ENERGY);
    syncer.register(BuiltinSyncedFields.PROGRESS);
    syncer.register(BuiltinSyncedFields.MAX_PROGRESS);
  }

  @Override
  protected List<Integer> getComparatorSignalSlots() {
    return outputSlots;
  }

  @Override
  protected List<Integer> getComparatorSignalTanks() {
    return outputTanks;
  }

  @Override
  public void tick() {
    if (level == null || level.isClientSide()) {
      return;
    }

    process();

    if (isSignalEnabled()) {
      queuedPushPull();
    }

    syncer.set(BuiltinSyncedFields.ENERGY, getEnergy());
    syncer.set(BuiltinSyncedFields.MAX_ENERGY, this.getEnergyCapacity());
    syncer.set(BuiltinSyncedFields.PROGRESS, getProgress());
    syncer.set(BuiltinSyncedFields.MAX_PROGRESS, getMaxProgress());
    syncer.set(BuiltinSyncedFields.POWER, getActualPower());
    synchronizeBasicData();
  }

  protected void process() {
    if (hasEnoughOutputSpace() && isSignalEnabled() && isEnergySufficient()) {
      lastRecipe = recipe;
      recipe = findRecipe();

      if (recipe == null) {
        progress = 0;
        if (lastRecipe == null) {
          setActive(false); // Fix: on completion, device toggles its ACTIVE. IDK why
        }
        setChanged();
        return;
      }

      setActive(true);
      maxProgress = getRecipeTime(recipe) * info.power;
      int consumed = getActualPower();
      if (consumed <= 0) {
        setActive(false);
        return;
      } else {
        triggerSound();
      }

      progress += consumed;
      setEnergy(getEnergy() - consumed);

      if (progress >= maxProgress) {
        finish();
        progress = 0;
        recipe = null;
      }

      setChanged();
    } else {
      setActive(false);

      if (progress > 0 && !isEnergySufficient()) {
        progress = Math.max(0, progress - info.power);
        setChanged();
      }
    }
  }

  protected @Nullable T findRecipe() {
    if (level == null || level.isClientSide()) {
      return null;
    }

    for (RecipeHolder<T> rh : level.getRecipeManager().getAllRecipesFor(recipeType)) {
      T r = rh.value();
      if (doesRecipeMatch(r)) {
        return r;
      }
    }

    return null;
  }

  protected abstract boolean hasEnoughOutputSpace();

  protected abstract void finish();

  protected abstract boolean doesRecipeMatch(T r);

  protected abstract RecipeType<T> getRecipeType();

  protected abstract int getRecipeTime(T r);

  protected abstract void initializeRecipeAutomation();

  protected boolean canFitItemOutput(ItemStack s) {
    return inventory.forceInsert(s.copy(), outputSlots, true).isEmpty();
  }

  protected void giveItemOutput(ItemStack s) {
    inventory.forceInsert(s.copy(), outputSlots, false);
  }

  protected boolean canFitFluidOutput(FluidStack f) {
    return fluidInventory.forceFill(f.copy(), outputTanks, IFluidHandler.FluidAction.SIMULATE) >= f.getAmount();
  }

  protected void giveFluidOutput(FluidStack f) {
    fluidInventory.forceFill(f.copy(), outputTanks, IFluidHandler.FluidAction.EXECUTE);
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.loadAdditional(tag, reg);
    progress = tag.getInt("Progress");
    maxProgress = tag.getInt("MaxProgress");
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.saveAdditional(tag, reg);
    tag.putInt("Progress", progress);
    tag.putInt("MaxProgress", maxProgress);
  }

  public int getProgress() {
    return progress;
  }

  public int getMaxProgress() {
    return maxProgress;
  }
}
