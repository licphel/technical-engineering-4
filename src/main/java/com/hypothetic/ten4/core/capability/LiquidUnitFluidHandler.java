package com.hypothetic.ten4.core.capability;

import com.hypothetic.ten4.core.item.LiquidUnitItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class LiquidUnitFluidHandler implements IFluidHandlerItem {
  private final ItemStack stack;

  public LiquidUnitFluidHandler(ItemStack stack) {
    this.stack = stack;
  }

  @Override
  public ItemStack getContainer() {
    return stack;
  }

  @Override
  public int getTanks() {
    return 1;
  }

  @Override
  public FluidStack getFluidInTank(int tank) {
    return LiquidUnitItem.getStoredFluid(stack);
  }

  @Override
  public int getTankCapacity(int tank) {
    return LiquidUnitItem.getCapacity(stack);
  }

  @Override
  public boolean isFluidValid(int tank, FluidStack stack) {
    return true;
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    if (resource.isEmpty()) {
      return 0;
    }
    int cap = LiquidUnitItem.getCapacity(stack);
    if (cap <= 0) {
      return 0;
    }
    FluidStack current = LiquidUnitItem.getStoredFluid(stack);
    if (!current.isEmpty() && !FluidStack.isSameFluidSameComponents(resource, current)) {
      return 0;
    }
    int space = cap - current.getAmount();
    int toFill = Math.min(resource.getAmount(), space);
    if (toFill <= 0) {
      return 0;
    }
    if (action.execute()) {
      FluidStack next = current.isEmpty() ? resource.copyWithAmount(toFill) : current.copy();
      if (!current.isEmpty()) {
        next.grow(toFill);
      }
      LiquidUnitItem.setStoredFluid(stack, next);
    }
    return toFill;
  }

  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    if (resource.isEmpty()) {
      return FluidStack.EMPTY;
    }
    FluidStack current = LiquidUnitItem.getStoredFluid(stack);
    if (current.isEmpty() || !FluidStack.isSameFluidSameComponents(resource, current)) {
      return FluidStack.EMPTY;
    }
    return drainAmount(Math.min(resource.getAmount(), current.getAmount()), action);
  }

  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    if (maxDrain <= 0) {
      return FluidStack.EMPTY;
    }
    FluidStack current = LiquidUnitItem.getStoredFluid(stack);
    if (current.isEmpty()) {
      return FluidStack.EMPTY;
    }
    return drainAmount(Math.min(maxDrain, current.getAmount()), action);
  }

  private FluidStack drainAmount(int amount, FluidAction action) {
    FluidStack current = LiquidUnitItem.getStoredFluid(stack);
    FluidStack drained = current.copyWithAmount(amount);
    if (action.execute()) {
      FluidStack remaining = current.copy();
      remaining.shrink(amount);
      LiquidUnitItem.setStoredFluid(stack, remaining);
    }
    return drained;
  }
}
