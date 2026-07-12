package com.hypothetic.ten4.api.blockentity.device;

import com.hypothetic.ten4.api.blockentity.ITickable;
import com.hypothetic.ten4.api.container.sync.BuiltinSyncedFields;
import com.hypothetic.ten4.api.container.sync.Syncer;
import com.hypothetic.ten4.api.recipe.RecipeEntry;
import com.hypothetic.ten4.api.recipe.ModRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class RecipeDeviceBlockEntity extends AugmentableDeviceBlockEntity implements ITickable {
  protected final List<Integer> inputSlots = new ArrayList<>();
  protected final List<Integer> inputTanks = new ArrayList<>();
  protected final List<Integer> outputSlots = new ArrayList<>();
  protected final List<Integer> outputTanks = new ArrayList<>();
  protected final RecipeType<ModRecipe> recipeType;
  protected @Nullable ModRecipe recipe;
  protected @Nullable ModRecipe lastRecipe;
  protected int progress = 0;
  protected int maxProgress = 0;

  public RecipeDeviceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
    this.recipeType = getRecipeType();
  }

  @Override
  protected void initAttributes(Syncer syncer) {
    syncer.register(BuiltinSyncedFields.ENERGY);
    syncer.register(BuiltinSyncedFields.MAX_ENERGY);
    syncer.register(BuiltinSyncedFields.PROGRESS);
    syncer.register(BuiltinSyncedFields.MAX_PROGRESS);
    syncer.register(BuiltinSyncedFields.EFFICIENCY);
  }

  @Override
  public int getComparatorSignal() {
    return switch (comparatorMode) {
      case OUTPUT_ITEMS -> {
        if (outputSlots.isEmpty()) {
          yield  0;
        }

        float f = 0.0F;

        for (int i : outputSlots) {
          ItemStack itemstack = inventory.getItem(i);
          if (!itemstack.isEmpty()) {
            f += (float) itemstack.getCount() / (float) inventory.getSlotLimit(i);
          }
        }

        f /= (float) outputSlots.size();
        yield Mth.lerpDiscrete(f, 0, 15);
      }
      case OUTPUT_FLUID -> {
        if (outputTanks.isEmpty()) {
          yield  0;
        }

        float f = 0.0F;

        for (int i : outputTanks) {
          FluidStack s = fluidTanks.getFluidInTank(i);
          if (!s.isEmpty()) {
            f += (float) s.getAmount() / (float) fluidTanks.getTankCapacity(i);
          }
        }

        f /= (float) outputTanks.size();
        yield Mth.lerpDiscrete(f, 0, 15);
      }
      default -> super.getComparatorSignal();
    };
  }

  @Override
  public boolean isValidInput(ItemStack stack) {
    if (level == null || level.isClientSide()) {
      return false;
    }

    if (!strictInput) {
      return true;
    }

    return level.getRecipeManager().getAllRecipesFor(recipeType).stream()
        .anyMatch(h -> {
          for (RecipeEntry in : h.value().itemInputs()) {
            if (in.test(stack)) {
              return true;
            }
          }
          return false;
        });
  }

  @Override
  public void onLoad() {
    super.onLoad();
    initializeRecipeAutomation();
  }

  @Override
  public void tick() {
    if (level == null || level.isClientSide()) {
      return;
    }

    process();

    if (isSignalEnabled() && getEnergy() > 0) {
      queuedPushPull();
    }

    syncer.set(BuiltinSyncedFields.ENERGY, getEnergy());
    syncer.set(BuiltinSyncedFields.MAX_ENERGY, getMaxEnergy());
    syncer.set(BuiltinSyncedFields.PROGRESS, getProgress());
    syncer.set(BuiltinSyncedFields.MAX_PROGRESS, getMaxProgress());
    syncer.set(BuiltinSyncedFields.EFFICIENCY, getEfficiency());
    synchronizeBasicData();
  }

  protected void process() {
    if (hasEnoughOutputSpace() && isSignalEnabled() && isEnergySufficient()) {
      lastRecipe = recipe;
      recipe = findRecipe();

      if (recipe == null || lastRecipe != recipe) {
        progress = 0;
        setActive(false);
        setChanged();
        return;
      }

      setActive(true);
      maxProgress = recipe.time() * getBasicEfficiency();
      int consumed = getEfficiency();
      if (consumed <= 0) {
        setActive(false);
        return;
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
        progress = Math.max(0, progress - getBasicEfficiency());
        setChanged();
      }
    }
  }

  protected boolean hasEnoughOutputSpace() {
    if (recipe == null) {
      return true;
    }
    for (RecipeEntry entry : recipe.itemOutputs()) {
      ItemStack s = entry.symbolItem();
      if (s.isEmpty()) {
        continue;
      }
      if (!canFitItemOutput(s)) {
        return false;
      }
    }
    for (RecipeEntry entry : recipe.fluidOutputs()) {
      FluidStack f = entry.symbolFluid();
      if (f.isEmpty()) {
        continue;
      }
      if (!canFitFluidOutput(f)) {
        return false;
      }
    }
    return true;
  }

  protected void finish() {
    assert recipe != null;
    delayPushFor(2); // For comparator signal propagation

    for (ItemStack s : recipe.generateItems()) {
      if (!s.isEmpty()) {
        giveItemOutput(s);
      }
    }
    for (FluidStack s : recipe.generateFluids()) {
      if (!s.isEmpty()) {
        giveFluidOutput(s);
      }
    }
    shrinkInputs();
  }

  protected boolean canFitItemOutput(ItemStack s) {
    return inventory.forceInsert(s.copy(), outputSlots, true).isEmpty();
  }

  protected void giveItemOutput(ItemStack s) {
    inventory.forceInsert(s.copy(), outputSlots, false);
  }

  protected boolean canFitFluidOutput(FluidStack f) {
    return fluidTanks.forceFill(f.copy(), outputTanks, IFluidHandler.FluidAction.SIMULATE) >= f.getAmount();
  }

  protected void giveFluidOutput(FluidStack f) {
    fluidTanks.forceFill(f.copy(), outputTanks, IFluidHandler.FluidAction.EXECUTE);
  }

  protected void shrinkInputs() {
    assert recipe != null;

    for (RecipeEntry in : recipe.itemInputs()) {
      int n = in.count();
      for (Integer i : inputSlots) {
        ItemStack s = inventory.getStackInSlot(i);
        if (!s.isEmpty() && in.containsItem(s.getItem())) {
          int t = Math.min(n, s.getCount());
          inventory.getSlot(i).extractItem(t, false, true);
          n -= t;
        }
      }
    }
    for (RecipeEntry in : recipe.fluidInputs()) {
      int n = in.count();
      for (Integer i : inputTanks) {
        FluidStack f = fluidTanks.getFluidInTank(i);
        if (in.containsFluid(f.getFluid())) {
          int t = Math.min(n, f.getAmount());
          fluidTanks.getTank(i).drain(t, IFluidHandler.FluidAction.EXECUTE);
          n -= t;
        }
      }
    }
  }

  protected boolean doesRecipeMatch(ModRecipe r) {
    for (RecipeEntry in : r.itemInputs()) {
      boolean f = false;
      for (Integer i : inputSlots) {
        if (in.test(inventory.getStackInSlot(i))) {
          f = true;
          break;
        }
      }
      if (!f) {
        return false;
      }
    }
    for (RecipeEntry in : r.fluidInputs()) {
      boolean f = false;
      for (Integer i : inputTanks) {
        if (in.test(fluidTanks.getFluidInTank(i))) {
          f = true;
          break;
        }
      }
      if (!f) {
        return false;
      }
    }
    return true;
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

  protected abstract void initializeRecipeAutomation();

  protected abstract RecipeType<ModRecipe> getRecipeType();

  protected @Nullable ModRecipe findRecipe() {
    if (level == null || level.isClientSide()) {
      return null;
    }
    return level.getRecipeManager().getAllRecipesFor(recipeType).stream()
        .map(RecipeHolder::value).filter(this::doesRecipeMatch).findFirst().orElse(null);
  }
}
