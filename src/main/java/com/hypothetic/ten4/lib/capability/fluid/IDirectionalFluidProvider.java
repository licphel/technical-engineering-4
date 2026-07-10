package com.hypothetic.ten4.lib.capability.fluid;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import org.jetbrains.annotations.Nullable;
import java.util.Queue;

public interface IDirectionalFluidProvider {
  IFluidHandler getTanks();

  int getMaxFluidExtract(@Nullable Direction d);

  int getMaxFluidReceive(@Nullable Direction d);

  default boolean isFluidValid(int tank, FluidStack stack) {
    return getTanks().isFluidValid(tank, stack);
  }

  default boolean canExtractFluid(@Nullable Direction d) {
    return getMaxFluidExtract(d) > 0;
  }

  default boolean canReceiveFluid(@Nullable Direction d) {
    return  getMaxFluidReceive(d) > 0;
  }

  Queue<Direction> getFluidPushingCycle();

  Queue<Direction> getFluidPullingCycle();
}
