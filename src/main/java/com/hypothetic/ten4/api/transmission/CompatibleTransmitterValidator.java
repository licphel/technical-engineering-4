package com.hypothetic.ten4.api.transmission;

import com.hypothetic.ten4.api.transmission.fluid.FluidTransmitter;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

/**
 * Mekanism-style validator: created from BFS start point.
 * Checks fluid/chemical compatibility only. Color is handled by isValidTransmitterBasic.
 */
public class CompatibleTransmitterValidator<AC, NET extends DynamicNetwork<AC, NET, T>, T extends Transmitter<AC, NET, T>> {

  @Nullable private FluidStack allowedFluid;

  public CompatibleTransmitterValidator(T start) {
    if (start instanceof FluidTransmitter ft) {
      this.allowedFluid = ft.getBuffer().isEmpty() ? FluidStack.EMPTY : ft.getBuffer().copy();
    }
  }

  /** Check if an orphan fluid transmitter has compatible fluid with the start. */
  public boolean isTransmitterCompatible(Transmitter<?, ?, ?> transmitter) {
    if (transmitter instanceof FluidTransmitter ft) {
      return compareFluids(ft.getBuffer());
    }
    return true;
  }

  /** Check if a found fluid network has compatible fluid. */
  @SuppressWarnings({"rawtypes"})
  public boolean isNetworkCompatible(NET network) {
    if (network instanceof com.hypothetic.ten4.api.transmission.fluid.FluidNetwork fn) {
      // Check network's stored validator first
      if (network instanceof DynamicBufferedNetwork bn && bn.getValidator() instanceof CompatibleTransmitterValidator other) {
        return compareFluids(other.allowedFluid);
      }
      return compareFluids(fn.getFluid());
    }
    return true;
  }

  private boolean compareFluids(FluidStack other) {
    if (allowedFluid == null || allowedFluid.isEmpty()) {
      allowedFluid = other;
      return true;
    }
    return other.isEmpty() || FluidStack.isSameFluidSameComponents(allowedFluid, other);
  }
}
