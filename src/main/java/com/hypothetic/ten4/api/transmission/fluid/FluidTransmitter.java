package com.hypothetic.ten4.api.transmission.fluid;

import com.hypothetic.ten4.api.transmission.BufferedTransmitter;
import com.hypothetic.ten4.api.transmission.ITransmitterProvider;
import com.hypothetic.ten4.api.transmission.Transmitter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public class FluidTransmitter extends BufferedTransmitter<IFluidHandler, FluidNetwork, FluidStack, FluidTransmitter> {
  private FluidStack buffer = FluidStack.EMPTY; // local buffer when orphaned
  private FluidStack syncedBuffer = FluidStack.EMPTY;
  private long syncedCapacity;

  public FluidTransmitter(ITransmitterProvider tile, long capacity, long throughput) {
    super(tile, capacity, throughput);
  }

  @Override
  public FluidStack releaseShare() {
    FluidStack share = buffer;
    buffer = FluidStack.EMPTY;
    return share;
  }

  @Override
  @Nullable
  public IFluidHandler getAcceptor(Direction side, Level level, BlockPos targetPos) {
    return level.getCapability(Capabilities.FluidHandler.BLOCK, targetPos, side.getOpposite());
  }

  public FluidStack getBuffer() {
    return buffer;
  }

  public void setBuffer(FluidStack s) {
    buffer = s;
  }

  public long getBufferAmount() {
    return buffer.getAmount();
  }

  @Override
  public FluidNetwork createEmptyNetwork(UUID id) {
    return new FluidNetwork(id);
  }

  @Override
  public FluidNetwork createNetworkByMerging(Collection<FluidNetwork> nets) {
    return new FluidNetwork(nets);
  }

  @Override
  public boolean supportsTransmission(Transmitter<?, ?, ?> other) {
    return other instanceof FluidTransmitter;
  }

  @Override
  protected boolean isContentsCompatible(Transmitter<?, ?, ?> other) {
    if (other instanceof FluidTransmitter ft) {
      FluidStack mine = getEffectiveFluid();
      FluidStack theirs = ft.getEffectiveFluid();
      if (mine.isEmpty() || theirs.isEmpty()) return true;
      return FluidStack.isSameFluidSameComponents(mine, theirs);
    }
    return true;
  }

  private FluidStack getEffectiveFluid() {
    FluidNetwork net = getNetwork();
    return net != null && !net.getFluid().isEmpty() ? net.getFluid() : buffer;
  }

  @Override
  protected boolean isValidAcceptor(Direction side) {
    if (getLevel() == null) {
      return false;
    }
    return getLevel().getCapability(Capabilities.FluidHandler.BLOCK, getBlockPos().relative(side), side.getOpposite()) != null;
  }

  @Override
  public void takeShare() {
    FluidNetwork net = getNetwork();
    if (net != null) {
      FluidStack netBuf = net.getFluid();
      if (!netBuf.isEmpty()) {
        int size = net.getTransmitters().size();
        if (size > 0) {
          int share = netBuf.getAmount() / size;
          netBuf.shrink(share);
          buffer = netBuf.copyWithAmount(share);
        }
      }
    }
  }

  public FluidStack getSyncedFluid() {
    return syncedBuffer;
  }

  public void syncClientFluid(float scale, FluidStack fluid) {
    syncClientScale(scale);
    syncedBuffer = fluid;
  }
}
