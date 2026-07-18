package com.hypothetic.ten4.api.blockentity.device;

import com.hypothetic.ten4.api.blockentity.ITickable;
import com.hypothetic.ten4.api.recipe.Complex;
import com.hypothetic.ten4.api.recipe.IComplexRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public abstract class ComplexRecipeDeviceBlockEntity extends AbstractRecipeDeviceBlockEntity<RecipeInput, IComplexRecipe> implements ITickable {
  public ComplexRecipeDeviceBlockEntity(BlockPos pos, BlockState state) {
    super(pos, state);
  }

  @Override
  public boolean isValidInput(ItemStack stack) {
    if (level == null || level.isClientSide()) {
      return false;
    }

    if (!isItemStrictInput()) {
      return true;
    }

    return level.getRecipeManager().getAllRecipesFor(recipeType).stream()
        .anyMatch(h -> {
          for (Complex in : h.value().itemInputs()) {
            if (in.test(stack)) {
              return true;
            }
          }
          return false;
        });
  }

  @Override
  public boolean isValidInput(FluidStack stack) {
    if (level == null || level.isClientSide()) {
      return false;
    }

    if (!isItemStrictInput()) {
      return true;
    }

    return level.getRecipeManager().getAllRecipesFor(recipeType).stream()
        .anyMatch(h -> {
          for (Complex in : h.value().itemInputs()) {
            if (in.test(stack)) {
              return true;
            }
          }
          return false;
        });
  }

  protected boolean hasEnoughOutputSpace() {
    if (recipe == null) {
      return true;
    }
    for (Complex entry : recipe.itemOutputs()) {
      ItemStack s = entry.symbolItem();
      if (s.isEmpty()) {
        continue;
      }
      if (!canFitItemOutput(s)) {
        return false;
      }
    }
    for (Complex entry : recipe.fluidOutputs()) {
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

  protected boolean doesRecipeMatch(IComplexRecipe r) {
    for (Complex in : r.itemInputs()) {
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
    for (Complex in : r.fluidInputs()) {
      boolean f = false;
      for (Integer i : inputTanks) {
        if (in.test(fluidInventory.getFluidInTank(i))) {
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
  protected int getRecipeTime(IComplexRecipe r) {
    return r.time();
  }

  protected void shrinkInputs() {
    assert recipe != null;

    for (Complex in : recipe.itemInputs()) {
      if (in.isCatalyst()) {
        continue;
      }
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
    for (Complex in : recipe.fluidInputs()) {
      if (in.isCatalyst()) {
        continue;
      }
      int n = in.count();
      for (Integer i : inputTanks) {
        FluidStack f = fluidInventory.getFluidInTank(i);
        if (in.containsFluid(f.getFluid())) {
          int t = Math.min(n, f.getAmount());
          fluidInventory.getTank(i).drain(t, IFluidHandler.FluidAction.EXECUTE);
          n -= t;
        }
      }
    }
  }
}
