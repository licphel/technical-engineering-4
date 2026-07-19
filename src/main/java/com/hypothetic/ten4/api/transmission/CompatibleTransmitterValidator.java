package com.hypothetic.ten4.api.transmission;

import com.hypothetic.ten4.api.transmission.fluid.FluidNetwork;
import com.hypothetic.ten4.api.transmission.fluid.FluidTransmitter;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class CompatibleTransmitterValidator<AC, NET extends Network<AC, NET, T>, T extends Transmitter<AC, NET, T>> {
  private FluidStack fluidContent = FluidStack.EMPTY;
  private @Nullable DyeColor color;

  public CompatibleTransmitterValidator(T start) {
    if (start instanceof FluidTransmitter ft) {
      this.fluidContent = ft.getBuffer().isEmpty() ? FluidStack.EMPTY : ft.getBuffer().copy();
    }
    this.color = start.getColor();
  }

  public boolean isTransmitterCompatible(Transmitter<?, ?, ?> transmitter) {
    if (transmitter instanceof FluidTransmitter ft) {
      return compareFluids(ft.getBuffer());
    }
    return true;
  }

  public boolean isNetworkCompatible(NET network) {
    if (network instanceof FluidNetwork fn) {
      return compareFluids(fn.getFluid());
    }
    return true;
  }

  public boolean isColorCompatible(Transmitter<?, ?, ?> t) {
    DyeColor tc = t.getColor();
    if (color == null) {
      color = tc;
      return true;
    }
    return tc == null || color == tc;
  }

  private boolean compareFluids(FluidStack other) {
    if (other.isEmpty()) {
      return true; // empty is compatible with anything; don't overwrite fluidContent
    }
    if (fluidContent.isEmpty()) {
      fluidContent = other.copy();
      return true;
    }
    return FluidStack.isSameFluidSameComponents(fluidContent, other);
  }
}
