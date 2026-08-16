package com.hypothetic.ten4.api.capability.fluid;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

public class FluidClickInteractionHandler implements IFluidHandler {
  private final FluidInventory inventory;
  private final List<Integer> tanks;

  public FluidClickInteractionHandler(FluidInventory inventory, List<Integer> tanks) {
    this.inventory = inventory;
    this.tanks = tanks;
  }

  @Override
  public int getTanks() {
    return tanks.size();
  }

  @Override
  public FluidStack getFluidInTank(int tank) {
    return tank >= 0 && tank < tanks.size() ? inventory.getFluidInTank(tanks.get(tank)) : FluidStack.EMPTY;
  }

  @Override
  public int getTankCapacity(int tank) {
    return tank >= 0 && tank < tanks.size() ? inventory.getTankCapacity(tanks.get(tank)) : 0;
  }

  @Override
  public boolean isFluidValid(int tank, FluidStack stack) {
    return tank >= 0 && tank < tanks.size() && inventory.isFluidValid(tanks.get(tank), stack);
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    return inventory.forceFill(resource, tanks, action);
  }

  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    return inventory.forceDrain(resource, tanks, action);
  }

  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    if (maxDrain <= 0) {
      return FluidStack.EMPTY;
    }
    for (int i : tanks) {
      FluidStack f = inventory.getFluidInTank(i);
      if (!f.isEmpty()) {
        return inventory.forceDrain(new FluidStack(f.getFluid(), maxDrain), tanks, action);
      }
    }
    return FluidStack.EMPTY;
  }
}
