package com.hypothetic.ten4.api.transmission.fluid;

import com.hypothetic.ten4.api.blockentity.internet.FluidDuctBlockEntity;
import com.hypothetic.ten4.api.transmission.BufferedTransmitter;
import com.hypothetic.ten4.api.transmission.CompatibleTransmitterValidator;
import com.hypothetic.ten4.api.transmission.ConnectionType;
import com.hypothetic.ten4.api.transmission.ITransmitterProvider;
import com.hypothetic.ten4.api.transmission.Transmitter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public class FluidTransmitter extends BufferedTransmitter<IFluidHandler, FluidNetwork, FluidStack, FluidTransmitter> {
  public static final int DUCT_LENGTH = 128;
  final long capacity;

  private FluidStack buffer = FluidStack.EMPTY; // local buffer when orphaned
  private FluidStack syncedBuffer = FluidStack.EMPTY;
  private long syncedCapacity;

  public FluidTransmitter(ITransmitterProvider tile, long capacity, int ticksPerBlock) {
    super(tile);
    this.capacity = capacity;
  }

  // ---- BufferedTransmitter ----
  @Override public long getCapacity() { return capacity; }

  @Override public FluidStack releaseShare() {
    FluidStack share = buffer;
    buffer = FluidStack.EMPTY;
    return share;
  }

  @Override public CompatibleTransmitterValidator<?, ?, ?> getNewOrphanValidator() {
    return super.getNewOrphanValidator(); // constructor handles fluid
  }

  @Override public void takeShare() {
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

  @Override @Nullable public IFluidHandler getAcceptor(Direction side, Level level, BlockPos targetPos) {
    return level.getCapability(Capabilities.FluidHandler.BLOCK, targetPos, side.getOpposite());
  }

  // ---- Local buffer (orphaned state) ----
  public FluidStack getBuffer() { return buffer; }
  public void setBuffer(FluidStack s) { buffer = s; }
  public long getBufferAmount() { return buffer.getAmount(); }

  // ---- Client sync (scale = network fill 0..1, fluid = type for color) ----
  public float getClientScale() { return clientScale; }
  public FluidStack getSyncedFluid() { return syncedBuffer; }
  public void applyFluidSync(float scale, FluidStack fluid) { clientScale = scale; syncedBuffer = fluid; }
  private float clientScale;

  // ---- PULL from world (called per-transmitter before network tick) ----
  public void pullFromAcceptors(FluidNetwork network, Level level) {
    BlockPos myPos = getBlockPos();
    long space = capacity - buffer.getAmount();
    if (space <= 0) return;

    for (Direction side : Direction.values()) {
      if (getConnectionTypeRaw(side) != ConnectionType.PULL) continue;
      BlockPos t = myPos.relative(side);
      IFluidHandler src = level.getCapability(Capabilities.FluidHandler.BLOCK, t, side.getOpposite());
      if (src != null) {
        FluidStack drained = src.drain((int) Math.min(space, 1000), IFluidHandler.FluidAction.EXECUTE);
        if (!drained.isEmpty()) {
          if (buffer.isEmpty()) buffer = drained.copy();
          else if (FluidStack.isSameFluidSameComponents(buffer, drained)) buffer.grow(drained.getAmount());
          space -= drained.getAmount();
        }
      }
      if (space <= 0) break;
    }
  }

  // ---- Boilerplate ----
  @Override public FluidNetwork createEmptyNetwork(UUID id) { return new FluidNetwork(id); }
  @Override public FluidNetwork createNetworkByMerging(Collection<FluidNetwork> nets) { return new FluidNetwork(nets); }
  @Override public boolean supportsTransmission(Transmitter<?, ?, ?> other) { return other instanceof FluidTransmitter; }
  @Override protected boolean isValidAcceptor(Direction side) {
    if (getLevel() == null) return false;
    return getLevel().getCapability(Capabilities.FluidHandler.BLOCK, getBlockPos().relative(side), side.getOpposite()) != null;
  }
  @Override protected @Nullable Transmitter<?, ?, ?> getTransmitter(ITransmitterProvider t) {
    if (t instanceof FluidDuctBlockEntity be) return be.transmitter;
    return null;
  }
}
