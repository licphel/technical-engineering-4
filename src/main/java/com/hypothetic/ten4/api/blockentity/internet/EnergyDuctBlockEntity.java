package com.hypothetic.ten4.api.blockentity.internet;

import com.hypothetic.ten4.api.ITickable;
import com.hypothetic.ten4.api.transmission.ConnectionType;
import com.hypothetic.ten4.api.transmission.ITransmitterProvider;
import com.hypothetic.ten4.api.transmission.Transmitter;
import com.hypothetic.ten4.api.transmission.TransmitterNetworkRegistry;
import com.hypothetic.ten4.api.transmission.energy.EnergyNetwork;
import com.hypothetic.ten4.api.transmission.energy.EnergyTransmitter;
import com.hypothetic.ten4.api.network.duct.DuctConnectionPayload;
import com.hypothetic.ten4.api.network.duct.DuctEnergyPayload;
import com.hypothetic.ten4.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class EnergyDuctBlockEntity extends BlockEntity implements ITransmitterProvider, ITickable {
  public static final int CAP_COPPER = 20;

  public final EnergyTransmitter transmitter;
  private int tickCount;

  public EnergyDuctBlockEntity(BlockPos pos, BlockState state, int bufferCapacity) {
    super(ModBlockEntities.COPPER_ENERGY_DUCT.get(), pos, state);
    this.transmitter = new EnergyTransmitter(this, bufferCapacity);
  }

  @Override
  public void tick() {
    if (level == null || level.isClientSide()) {
      return;
    }

    if (tickCount++ % 10 == 0) {
      sendEnergyUpdate();
    }
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

  private void sendEnergyUpdate() {
    if (level instanceof ServerLevel sl) {
      EnergyNetwork net = transmitter.getNetwork();
      float scale = net != null ? net.currentScale : (transmitter.getCapacity() > 0 ? (float) transmitter.getBuffer() / transmitter.getCapacity() : 0);
      PacketDistributor.sendToPlayersTrackingChunk(sl, sl.getChunkAt(worldPosition).getPos(),
          new DuctEnergyPayload(worldPosition, scale));
    }
  }

  public @Nullable IEnergyStorage getEnergyStorage(@Nullable Direction side) {
    if (level == null) return null;
    return new NetworkEnergyStorage();
  }

  private class NetworkEnergyStorage implements IEnergyStorage {
    private EnergyNetwork net() { return transmitter.getNetwork(); }
    private long buf()  { EnergyNetwork n = net(); return n != null ? n.getBuffer() : transmitter.getBuffer(); }
    private long cap()  { EnergyNetwork n = net(); return n != null ? n.getCapacity() : transmitter.getCapacity(); }

    @Override public int receiveEnergy(int max, boolean sim) {
      long space = cap() - buf();
      int toAdd = (int) Math.min(Math.min(space, max), Integer.MAX_VALUE);
      if (toAdd > 0 && !sim) {
        EnergyNetwork n = net();
        if (n != null) n.setBuffer(n.getBuffer() + toAdd);
        else transmitter.setBuffer(transmitter.getBuffer() + toAdd);
      }
      return toAdd;
    }
    @Override public int extractEnergy(int max, boolean sim) {
      long cur = buf();
      int toExtract = (int) Math.min(Math.min(cur, max), Integer.MAX_VALUE);
      if (toExtract > 0 && !sim) {
        EnergyNetwork n = net();
        if (n != null) n.setBuffer(n.getBuffer() - toExtract);
        else transmitter.setBuffer(transmitter.getBuffer() - toExtract);
      }
      return toExtract;
    }
    @Override public int getEnergyStored() { return (int) Math.min(buf(), Integer.MAX_VALUE); }
    @Override public int getMaxEnergyStored() { return (int) Math.min(cap(), Integer.MAX_VALUE); }
    @Override public boolean canExtract() { return buf() > 0; }
    @Override public boolean canReceive() { return buf() < cap(); }
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.loadAdditional(tag, reg);
    transmitter.read(reg, tag.getCompound("Transmitter"));
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.saveAdditional(tag, reg);
    tag.put("Transmitter", transmitter.write(reg, new CompoundTag()));
  }

  @Override
  public BlockPos getBlockPos() {
    return worldPosition;
  }

  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider reg) {
    return transmitter.getReducedUpdateTag(reg, saveWithoutMetadata(reg));
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
      sendEnergyUpdate();
    }
  }
}
