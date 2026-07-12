package com.hypothetic.ten4.api.capability.fluid;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class FluidTank implements IFluidTank, IFluidHandler {
  protected @Nullable Predicate<FluidStack> validator;
  protected TankOption option = TankOption.BOTH;
  protected FluidStack fluid = FluidStack.EMPTY;
  protected int capacity;

  public FluidTank(int capacity) {
    this.capacity = capacity;
  }

  public FluidTank setValidator(@Nullable Predicate<FluidStack> validator) {
    if (validator != null) {
      this.validator = validator;
    }
    return this;
  }

  public FluidTank setOption(TankOption option) {
    this.option = option;
    return this;
  }

  public FluidStack getFluid() {
    return fluid;
  }

  public int getFluidAmount() {
    return fluid.getAmount();
  }

  public int getCapacity() {
    return capacity;
  }

  public FluidTank setCapacity(int capacity) {
    this.capacity = capacity;
    return this;
  }

  public boolean isFluidValid(FluidStack stack) {
    if (!option.canReceive()) {
      return false;
    }
    if (validator == null) {
      return true;
    }
    return validator.test(stack);
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    return fill(resource, action, false);
  }

  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    return drain(maxDrain, action, false);
  }

  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    return drain(resource, action, false);
  }

  public void setFluid(FluidStack stack) {
    this.fluid = stack;
  }

  public int fill(FluidStack resource, FluidAction action, boolean force) {
    if (resource.isEmpty()) {
      return 0;
    }
    if (!force && !isFluidValid(resource)) {
      return 0;
    }
    if (action.simulate()) {
      if (fluid.isEmpty()) {
        return Math.min(capacity, resource.getAmount());
      }
      if (!FluidStack.isSameFluidSameComponents(fluid, resource)) {
        return 0;
      }
      return Math.min(capacity - fluid.getAmount(), resource.getAmount());
    }
    if (fluid.isEmpty()) {
      fluid = resource.copyWithAmount(Math.min(capacity, resource.getAmount()));
      return fluid.getAmount();
    }
    if (!FluidStack.isSameFluidSameComponents(fluid, resource)) {
      return 0;
    }
    int filled = capacity - fluid.getAmount();

    if (resource.getAmount() < filled) {
      fluid.grow(resource.getAmount());
      filled = resource.getAmount();
    } else {
      fluid.setAmount(capacity);
    }

    return filled;
  }

  public FluidStack drain(int maxDrain, FluidAction action, boolean force) {
    if (!force && !canDrain()) {
      return FluidStack.EMPTY;
    }

    int drained = maxDrain;
    if (fluid.getAmount() < drained) {
      drained = fluid.getAmount();
    }
    FluidStack stack = fluid.copyWithAmount(drained);
    if (action.execute() && drained > 0) {
      fluid.shrink(drained);
    }
    return stack;
  }

  public FluidStack drain(FluidStack resource, FluidAction action, boolean force) {
    if (!force && !canDrain()) {
      return FluidStack.EMPTY;
    }
    if (resource.isEmpty() || !FluidStack.isSameFluidSameComponents(resource, fluid)) {
      return FluidStack.EMPTY;
    }
    return drain(resource.getAmount(), action, force);
  }

  public FluidTank readFromNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
    fluid = FluidStack.parseOptional(lookupProvider, nbt.getCompound("Fluid"));
    return this;
  }

  public CompoundTag writeToNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
    if (!fluid.isEmpty()) {
      nbt.put("Fluid", fluid.save(lookupProvider));
    }

    return nbt;
  }

  @Override
  public int getTanks() {
    return 1;
  }

  @Override
  public FluidStack getFluidInTank(int tank) {
    return getFluid();
  }

  @Override
  public int getTankCapacity(int tank) {
    return getCapacity();
  }

  @Override
  public boolean isFluidValid(int tank, FluidStack stack) {
    return isFluidValid(stack);
  }

  public boolean isEmpty() {
    return fluid.isEmpty();
  }

  public int getSpace() {
    return Math.max(0, capacity - fluid.getAmount());
  }

  public boolean canDrain() {
    return option.canExtract();
  }
}
