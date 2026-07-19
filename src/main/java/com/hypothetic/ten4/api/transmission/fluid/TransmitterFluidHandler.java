package com.hypothetic.ten4.api.transmission.fluid;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class TransmitterFluidHandler implements IFluidHandler {
  private final FluidTransmitter transmitter;
  private final @Nullable Direction side;

  public TransmitterFluidHandler(FluidTransmitter transmitter, @Nullable Direction side) {
    this.transmitter = transmitter;
    this.side = side;
  }

  private @Nullable FluidNetwork net() {
    return transmitter.getNetwork();
  }

  private FluidStack buf() {
    FluidNetwork n = net();
    return n != null ? n.getFluid() : transmitter.getBuffer();
  }

  private long cap() {
    FluidNetwork n = net();
    return n != null ? n.getCapacity() : transmitter.getCapacity();
  }

  @Override
  public int getTanks() {
    return 1;
  }

  @Override
  public FluidStack getFluidInTank(int tank) {
    return buf();
  }

  @Override
  public int getTankCapacity(int tank) {
    return (int) Math.min(cap(), Integer.MAX_VALUE);
  }

  @Override
  public boolean isFluidValid(int tank, FluidStack stack) {
    return true;
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    if (!transmitter.getConnectionTypeRaw(side).isPullOrNormal()) {
      return 0;
    }
    int max = (int) Math.min(resource.getAmount(), transmitter.getThroughput());
    if (max <= 0) {
      return 0;
    }
    FluidNetwork net = net();
    if (net != null) {
      return net.receiveFluid(resource.copyWithAmount(max), action);
    }
    FluidStack local = transmitter.getBuffer();
    if (!local.isEmpty() && !FluidStack.isSameFluidSameComponents(local, resource)) {
      return 0;
    }
    int space = (int) Math.min(cap() - local.getAmount(), Integer.MAX_VALUE);
    int toAdd = Math.min(max, space);
    if (toAdd > 0 && action.execute()) {
      if (local.isEmpty()) {
        transmitter.setBuffer(resource.copyWithAmount(toAdd));
      } else {
        local.grow(toAdd);
      }
    }
    return toAdd;
  }

  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    if (!transmitter.getConnectionTypeRaw(side).isPushOrNormal()) {
      return FluidStack.EMPTY;
    }
    int max = (int) Math.min(resource.getAmount(), transmitter.getThroughput());
    if (max <= 0) {
      return FluidStack.EMPTY;
    }
    FluidStack buf = buf();
    if (buf.isEmpty() || !FluidStack.isSameFluidSameComponents(buf, resource)) {
      return FluidStack.EMPTY;
    }
    int toDrain = Math.min(max, buf.getAmount());
    if (action.execute()) {
      buf.shrink(toDrain);
    }
    return resource.copyWithAmount(toDrain);
  }

  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    if (!transmitter.getConnectionTypeRaw(side).isPushOrNormal()) {
      return FluidStack.EMPTY;
    }
    int max = (int) Math.min(maxDrain, transmitter.getThroughput());
    if (max <= 0) {
      return FluidStack.EMPTY;
    }
    FluidStack buf = buf();
    if (buf.isEmpty()) {
      return FluidStack.EMPTY;
    }
    int toDrain = Math.min(max, buf.getAmount());
    if (action.execute()) {
      buf.shrink(toDrain);
    }
    return buf.copyWithAmount(toDrain);
  }
}
