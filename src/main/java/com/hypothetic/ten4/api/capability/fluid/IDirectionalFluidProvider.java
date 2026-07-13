package com.hypothetic.ten4.api.capability.fluid;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;

public interface IDirectionalFluidProvider {
  IFluidHandler getTanks();

  int getFluidThroughput();

  default boolean isFluidValid(int tank, FluidStack stack) {
    return getTanks().isFluidValid(tank, stack);
  }

  boolean canExtractFluid(@Nullable Direction d);

  boolean canReceiveFluid(@Nullable Direction d);

  Queue<Direction> getFluidPushingCycle();

  Queue<Direction> getFluidPullingCycle();
}
