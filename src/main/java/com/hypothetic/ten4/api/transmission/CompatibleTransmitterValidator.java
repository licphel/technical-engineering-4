package com.hypothetic.ten4.api.transmission;

import com.hypothetic.ten4.api.transmission.fluid.FluidNetwork;
import com.hypothetic.ten4.api.transmission.fluid.FluidTransmitter;
import net.neoforged.neoforge.fluids.FluidStack;

public class CompatibleTransmitterValidator<AC, NET extends Network<AC, NET, T>, T extends Transmitter<AC, NET, T>> {
  private FluidStack fluidContent = FluidStack.EMPTY;

  public CompatibleTransmitterValidator(T start) {
    if (start instanceof FluidTransmitter ft) {
      this.fluidContent = ft.getBuffer().isEmpty() ? FluidStack.EMPTY : ft.getBuffer().copy();
    }
  }

  public boolean isTransmitterCompatible(Transmitter<?, ?, ?> transmitter) {
    if (transmitter instanceof FluidTransmitter ft) {
      return compareFluids(ft.getBuffer());
    }
    return true;
  }

  @SuppressWarnings("rawtypes")
  public boolean isNetworkCompatible(NET network) {
    if (network instanceof FluidNetwork fn) {
      // Check network's stored validator first
      BufferedNetwork bn = (BufferedNetwork) network;
      if (bn.getValidator() instanceof CompatibleTransmitterValidator other) {
        return compareFluids(other.fluidContent);
      }
      return compareFluids(fn.getFluid());
    }
    return true;
  }

  private boolean compareFluids(FluidStack other) {
    if (fluidContent.isEmpty()) {
      fluidContent = other;
      return true;
    }
    return other.isEmpty() || FluidStack.isSameFluidSameComponents(fluidContent, other);
  }
}
