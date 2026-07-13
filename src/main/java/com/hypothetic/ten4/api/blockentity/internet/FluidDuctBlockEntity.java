package com.hypothetic.ten4.api.blockentity.internet;

import com.hypothetic.ten4.api.ITickable;
import com.hypothetic.ten4.api.transmission.ConnectionType;
import com.hypothetic.ten4.api.transmission.ITransmitterProvider;
import com.hypothetic.ten4.api.transmission.Transmitter;
import com.hypothetic.ten4.api.transmission.TransmitterNetworkRegistry;
import com.hypothetic.ten4.api.transmission.fluid.FluidNetwork;
import com.hypothetic.ten4.api.transmission.fluid.FluidTransmitter;
import com.hypothetic.ten4.api.network.duct.DuctConnectionPayload;
import com.hypothetic.ten4.api.network.duct.DuctFluidPayload;
import com.hypothetic.ten4.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class FluidDuctBlockEntity extends BlockEntity implements ITransmitterProvider, ITickable {
  public static final int CAP_COPPER = 1000;
  public static final int TPB_COPPER = 20;

  public final FluidTransmitter transmitter;
  private int tickCount;

  public FluidDuctBlockEntity(BlockPos pos, BlockState state, int capacity, int ticksPerBlock) {
    super(ModBlockEntities.COPPER_FLUID_DUCT.get(), pos, state);
    this.transmitter = new FluidTransmitter(this, capacity, ticksPerBlock);
  }

  @Override
  public void tick() {
    if (level == null || level.isClientSide()) return;
    if (tickCount++ % 5 == 0) sendFluidSync();
  }

  @Override
  public boolean isInvalid() {
    return isRemoved();
  }

  @Override
  public boolean isLoaded() {
    return level != null;
  }

  @Override
  public void sendUpdatePacket() {
    if (level instanceof ServerLevel sl) {
      setChanged();
      var t = transmitter;
      Direction[] dirs = Direction.values();
      ConnectionType[] types = new ConnectionType[6];
      for (int i = 0; i < 6; i++) {
        types[i] = t.getConnectionTypeRaw(dirs[i]);
      }
      DuctConnectionPayload pkt = new DuctConnectionPayload(worldPosition,
          t.getTransmitterConnections(), t.getAcceptorConnections(), types, t.getColor());
      PacketDistributor.sendToPlayersTrackingChunk(sl, sl.getChunkAt(worldPosition).getPos(), pkt);
    }
  }

  @Override
  public void notifyTileChange() {
    if (level != null) {
      setChanged();
      level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
    }
  }

  @Override
  public Transmitter<?, ?, ?> getTransmitter() {
    return transmitter;
  }

  @Override
  public void sendFluidSync() {
    if (level instanceof ServerLevel sl) {
      FluidNetwork net = transmitter.getNetwork();
      float scale = net != null ? net.currentScale
          : (transmitter.getCapacity() > 0 ? (float) transmitter.getBufferAmount() / transmitter.getCapacity() : 0);
      FluidStack fluid = net != null ? net.getFluid() : transmitter.getBuffer();
      PacketDistributor.sendToPlayersTrackingChunk(sl, sl.getChunkAt(worldPosition).getPos(),
          new DuctFluidPayload(worldPosition, scale, fluid));
    }
  }

  public @Nullable IFluidHandler getFluidHandler(@Nullable Direction side) {
    return new NetworkFluidHandler();
  }

  private class NetworkFluidHandler implements IFluidHandler {
    @Override public int getTanks() { return 1; }
    @Override public FluidStack getFluidInTank(int tank) {
      FluidNetwork net = transmitter.getNetwork();
      return net != null ? net.getFluid() : transmitter.getBuffer();
    }
    @Override public int getTankCapacity(int tank) {
      FluidNetwork net = transmitter.getNetwork();
      return net != null ? (int) Math.min(net.getCapacity(), Integer.MAX_VALUE) : (int) transmitter.getCapacity();
    }
    @Override public boolean isFluidValid(int tank, FluidStack stack) { return true; }
    @Override public int fill(FluidStack resource, FluidAction action) {
      FluidNetwork net = transmitter.getNetwork();
      if (net != null) return net.receiveFluid(resource, action);
      // Orphaned: store in local buffer
      FluidStack local = transmitter.getBuffer();
      if (!local.isEmpty() && !FluidStack.isSameFluidSameComponents(local, resource)) return 0;
      long cap = transmitter.getCapacity();
      int space = (int) Math.min(cap - local.getAmount(), Integer.MAX_VALUE);
      int toAdd = Math.min(resource.getAmount(), space);
      if (toAdd > 0 && action.execute()) {
        if (local.isEmpty()) transmitter.setBuffer(resource.copyWithAmount(toAdd));
        else local.grow(toAdd);
      }
      return toAdd;
    }
    @Override public FluidStack drain(FluidStack resource, FluidAction action) {
      FluidNetwork net = transmitter.getNetwork();
      FluidStack buf = net != null ? net.getFluid() : transmitter.getBuffer();
      if (buf.isEmpty() || !FluidStack.isSameFluidSameComponents(buf, resource)) return FluidStack.EMPTY;
      int toDrain = Math.min(resource.getAmount(), buf.getAmount());
      if (action.execute()) buf.shrink(toDrain);
      return resource.copyWithAmount(toDrain);
    }
    @Override public FluidStack drain(int maxDrain, FluidAction action) {
      FluidNetwork net = transmitter.getNetwork();
      FluidStack buf = net != null ? net.getFluid() : transmitter.getBuffer();
      if (buf.isEmpty()) return FluidStack.EMPTY;
      int toDrain = Math.min(maxDrain, buf.getAmount());
      if (action.execute()) buf.shrink(toDrain);
      return buf.copyWithAmount(toDrain);
    }
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.loadAdditional(tag, reg);
    transmitter.read(reg, tag.getCompound("Transmitter"));
    if (tag.contains("Buffer")) {
      transmitter.setBuffer(FluidStack.parseOptional(reg, tag.getCompound("Buffer")));
    }
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.saveAdditional(tag, reg);
    tag.put("Transmitter", transmitter.write(reg, new CompoundTag()));
    if (!transmitter.getBuffer().isEmpty()) {
      tag.put("Buffer", transmitter.getBuffer().save(reg, new CompoundTag()));
    }
  }

  @Override
  public BlockPos getBlockPos() {
    return worldPosition;
  }

  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider reg) {
    CompoundTag tag = transmitter.getReducedUpdateTag(reg, saveWithoutMetadata(reg));
    // Include fluid buffer in initial chunk data so client renders it on load
    FluidStack buf = transmitter.getBuffer();
    if (!buf.isEmpty()) {
      tag.put("Buf", buf.save(reg, new CompoundTag()));
    }
    return tag;
  }

  @Override
  public void setRemoved() {
    super.setRemoved();
    if (level != null && !level.isClientSide()) {
      transmitter.remove();
      TransmitterNetworkRegistry.onTransmitterRemoved(transmitter);
    }
  }

  @Override
  public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider reg) {
    super.handleUpdateTag(tag, reg);
    transmitter.handleUpdateTag(tag, reg);
    // Restore fluid buffer from initial chunk data so client renders it immediately
    FluidStack buf = tag.contains("Buf") ? FluidStack.parseOptional(reg, tag.getCompound("Buf")) : FluidStack.EMPTY;
    float scale = transmitter.getCapacity() > 0 ? (float) buf.getAmount() / transmitter.getCapacity() : 0;
    transmitter.applyFluidSync(scale, buf);
  }

  @Override
  public void onChunkUnloaded() {
    super.onChunkUnloaded();
    if (level != null && !level.isClientSide()) {
      transmitter.validateAndTakeShare();
      TransmitterNetworkRegistry.onTransmitterRemoved(transmitter);
    }
  }

  @Override
  public void onLoad() {
    super.onLoad();
    if (level != null && !level.isClientSide()) {
      transmitter.refreshConnections();
      TransmitterNetworkRegistry.joinNetwork(transmitter);
      sendFluidSync();
    }
  }
}
