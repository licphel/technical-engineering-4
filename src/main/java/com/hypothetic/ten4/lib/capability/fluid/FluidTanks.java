package com.hypothetic.ten4.lib.capability.fluid;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

public class FluidTanks implements IFluidHandler {
  private final List<FluidTank> tanks = new ArrayList<>();

  public FluidTanks add(FluidTank tank) {
    tanks.add(tank);
    return this;
  }

  @Override
  public int getTanks() {
    return tanks.size();
  }

  @Override
  public FluidStack getFluidInTank(int tank) {
    return tank < tanks.size() ? tanks.get(tank).getFluid() : FluidStack.EMPTY;
  }

  @Override
  public int getTankCapacity(int tank) {
    return tank < tanks.size() ? tanks.get(tank).getCapacity() : 0;
  }

  @Override
  public boolean isFluidValid(int tank, FluidStack stack) {
    if (tank >= tanks.size()) {
      return false;
    }
    return tanks.get(tank).isFluidValid(0, stack);
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    if (resource.isEmpty()) {
      return 0;
    }
    FluidStack remaining = resource.copyWithAmount(resource.getAmount());
    int totalFilled = 0;

    for (FluidTank tank : tanks) {
      if (remaining.isEmpty()) {
        break;
      }
      if (!tank.isFluidValid(remaining)) {
        continue;
      }
      int filled = tank.fill(remaining, action);
      totalFilled += filled;
      if (filled > 0 && action.execute()) {
        remaining.shrink(filled);
      }
    }
    return totalFilled;
  }

  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    if (resource.isEmpty()) {
      return FluidStack.EMPTY;
    }
    int toDrain = resource.getAmount();

    for (FluidTank tank : tanks) {
      if (!tank.canDrain()) {
        continue;
      }
      FluidStack tankFluid = tank.getFluidInTank(0);
      if (tankFluid.isEmpty() || !tankFluid.is(resource.getFluid())) {
        continue;
      }
      return tank.drain(new FluidStack(resource.getFluid(), Math.min(toDrain, tankFluid.getAmount())), action);
    }
    return FluidStack.EMPTY;
  }

  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    if (maxDrain <= 0) {
      return FluidStack.EMPTY;
    }

    for (FluidTank tank : tanks) {
      if (!tank.canDrain()) {
        continue;
      }
      FluidStack drained = tank.drain(maxDrain, action);
      if (!drained.isEmpty()) {
        return drained;
      }
    }
    return FluidStack.EMPTY;
  }

  public int forceFill(FluidStack resource, List<Integer> destination, FluidAction action) {
    if (resource.isEmpty()) {
      return 0;
    }
    FluidStack remaining = resource.copy();
    int total = 0;

    for (int i : destination) {
      if (i < 0 || i >= tanks.size()) {
        continue;
      }
      if (remaining.isEmpty()) {
        break;
      }
      int filled = tanks.get(i).fill(remaining, action, true);
      total += filled;
      if (filled > 0 && action.execute()) {
        remaining.shrink(filled);
      }
    }
    return total;
  }

  public FluidStack forceDrain(FluidStack resource, List<Integer> destination, FluidAction action) {
    for (int i : destination) {
      if (i < 0 || i >= tanks.size()) {
        continue;
      }
      FluidStack tankFluid = tanks.get(i).getFluidInTank(0);
      if (!tankFluid.isEmpty() && tankFluid.is(resource.getFluid())) {
        return tanks.get(i).drain(new FluidStack(resource.getFluid(), Math.min(resource.getAmount(), tankFluid.getAmount())), action, true);
      }
    }
    return FluidStack.EMPTY;
  }

  public FluidTank getTank(int index) {
    return tanks.get(index);
  }

  public boolean isOutputFull(FluidStack output, int firstOutputTank, int lastOutputTank) {
    for (int i = firstOutputTank; i <= lastOutputTank && i < tanks.size(); i++) {
      FluidStack existing = tanks.get(i).getFluidInTank(0);
      if (existing.isEmpty()) {
        return false;
      }
      if (existing.is(output.getFluid()) && existing.getAmount() + output.getAmount() <= tanks.get(i).getTankCapacity(0)) {
        return false;
      }
    }
    return true;
  }

  public CompoundTag createTag(HolderLookup.Provider reg) {
    CompoundTag tag = new CompoundTag();
    for (int i = 0; i < tanks.size(); i++) {
      tag.put("T" + i, tanks.get(i).writeToNBT(reg, new CompoundTag()));
    }
    return tag;
  }

  public void fromTag(CompoundTag tag, HolderLookup.Provider reg) {
    for (int i = 0; i < tanks.size(); i++) {
      if (tag.contains("T" + i)) {
        tanks.get(i).readFromNBT(reg, tag.getCompound("T" + i));
      }
    }
  }
}
