package com.hypothetic.ten4.api.capability.fluid;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class DirectionalFluidHandler implements IFluidHandler {
  private final IDirectionalFluidProvider host;
  private final @Nullable Direction side;

  public DirectionalFluidHandler(IDirectionalFluidProvider host, @Nullable Direction side) {
    this.host = host;
    this.side = side;
  }

  @Override
  public int getTanks() {
    return host.getTanks().getTanks();
  }

  @Override
  public FluidStack getFluidInTank(int tank) {
    return host.getTanks().getFluidInTank(tank);
  }

  @Override
  public int getTankCapacity(int tank) {
    return host.getTanks().getTankCapacity(tank);
  }

  @Override
  public boolean isFluidValid(int tank, FluidStack stack) {
    return host.canReceiveFluid(side) && host.isFluidValid(tank, stack);
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    if (resource.isEmpty() || !host.canReceiveFluid(side)) {
      return 0;
    }
    int cap = host.getMaxFluidReceive(side);
    if (cap <= 0) {
      return 0;
    }
    return host.getTanks().fill(resource.copyWithAmount(Math.min(resource.getAmount(), cap)), action);
  }

  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    if (resource.isEmpty() || !host.canExtractFluid(side)) {
      return FluidStack.EMPTY;
    }
    int cap = host.getMaxFluidExtract(side);
    if (cap <= 0) {
      return FluidStack.EMPTY;
    }
    return host.getTanks().drain(resource.copyWithAmount(Math.min(resource.getAmount(), cap)), action);
  }

  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    if (maxDrain <= 0 || !host.canExtractFluid(side)) {
      return FluidStack.EMPTY;
    }
    int cap = host.getMaxFluidExtract(side);
    if (cap <= 0) {
      return FluidStack.EMPTY;
    }
    return host.getTanks().drain(Math.min(maxDrain, cap), action);
  }
}
