package com.hypothetic.ten4.api.blockentity.device;

import com.hypothetic.ten4.api.blockentity.IClickFluidInteractable;
import com.hypothetic.ten4.api.blockentity.ITickable;
import com.hypothetic.ten4.api.capability.fluid.FluidClickInteractionHandler;
import com.hypothetic.ten4.api.recipe.Complex;
import com.hypothetic.ten4.api.recipe.IComplexRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public abstract class ComplexRecipeDeviceBlockEntity extends AbstractRecipeDeviceBlockEntity<RecipeInput, IComplexRecipe>
    implements ITickable, IClickFluidInteractable {
  public ComplexRecipeDeviceBlockEntity(BlockPos pos, BlockState state) {
    super(pos, state);
  }

  @Override
  public IFluidHandler getInteractedFluidInputView() {
    return new FluidClickInteractionHandler(fluidInventory, inputTanks);
  }

  @Override
  public IFluidHandler getInteractedFluidOutputView() {
    return new FluidClickInteractionHandler(fluidInventory, outputTanks);
  }

  @Override
  public boolean isValidInput(ItemStack stack) {
    if (level == null || level.isClientSide()) {
      return false;
    }

    // Type-exclusion: an already-present type may only merge into its existing
    // slot, never overflow into other input slots (independent of strict mode).
    if (!canMergeIntoExistingItemSlot(stack)) {
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

  /** True if the item type is absent from the input slots, or can merge into its existing slot. */
  protected boolean canMergeIntoExistingItemSlot(ItemStack stack) {
    for (int i : inputSlots) {
      ItemStack existing = inventory.getStackInSlot(i);
      if (existing.isEmpty() || !ItemStack.isSameItem(existing, stack)) {
        continue;
      }
      return existing.getCount() + stack.getCount() <= inventory.getSlotLimit(i);
    }
    return true; // type not present yet — occupies a fresh slot
  }

  @Override
  public boolean isValidInput(FluidStack stack) {
    if (level == null || level.isClientSide()) {
      return false;
    }

    // Type-exclusion: an already-present fluid may only merge into its existing
    // tank, never overflow into other input tanks (independent of strict mode).
    if (!canMergeIntoExistingFluidTank(stack)) {
      return false;
    }

    if (!isFluidStrictInput()) {
      return true;
    }

    return level.getRecipeManager().getAllRecipesFor(recipeType).stream()
        .anyMatch(h -> {
          for (Complex in : h.value().fluidInputs()) {
            if (in.test(stack)) {
              return true;
            }
          }
          return false;
        });
  }

  /** True if the fluid is absent from the input tanks, or can merge into its existing tank. */
  protected boolean canMergeIntoExistingFluidTank(FluidStack stack) {
    for (int i : inputTanks) {
      FluidStack existing = fluidInventory.getFluidInTank(i);
      if (existing.isEmpty() || !FluidStack.isSameFluidSameComponents(existing, stack)) {
        continue;
      }
      return existing.getAmount() + stack.getAmount() <= fluidInventory.getTankCapacity(i);
    }
    return true; // fluid not present yet — occupies a fresh tank
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
          fluidInventory.getTank(i).drain(t, IFluidHandler.FluidAction.EXECUTE, true);
          n -= t;
        }
      }
    }
  }
}
