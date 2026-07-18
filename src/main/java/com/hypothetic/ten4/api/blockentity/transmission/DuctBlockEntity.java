package com.hypothetic.ten4.api.blockentity.transmission;

import com.hypothetic.ten4.api.blockentity.RedstoneAwareBlockEntity;
import com.hypothetic.ten4.api.network.duct.DuctConnectionPayload;
import com.hypothetic.ten4.api.transmission.ConnectionType;
import com.hypothetic.ten4.api.transmission.ITransmitterProvider;
import com.hypothetic.ten4.api.transmission.Transmitter;
import com.hypothetic.ten4.api.transmission.TransmitterNetworkRegistry;
import com.hypothetic.ten4.core.block.duct.DuctBlock;
import com.hypothetic.ten4.core.block.duct.DuctInteractions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

public abstract class DuctBlockEntity<T extends Transmitter<?, ?, ?>> extends RedstoneAwareBlockEntity implements ITransmitterProvider {
  protected final DuctInfo info;
  public T transmitter;

  public DuctBlockEntity(BlockPos pos, BlockState blockState, DuctInfo info) {
    super(pos, blockState);
    this.info = info;
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
      T t = transmitter;
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
  public void notifyChanges() {
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
  public CompoundTag getUpdateTag(HolderLookup.Provider reg) {
    return transmitter.getReducedUpdateTag(reg, saveWithoutMetadata(reg));
  }

  @Override
  public void setRemoved() {
    super.setRemoved();
    if (level != null && !level.isClientSide()) {
      TransmitterNetworkRegistry.remove(transmitter);
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
      TransmitterNetworkRegistry.remove(transmitter);
    }
  }

  @Override
  public void onLoad() {
    super.onLoad();
    if (level != null && !level.isClientSide()) {
      transmitter.refreshConnections();
      TransmitterNetworkRegistry.join(transmitter);
      syncToClient();
      // Update block state CONNECTIONS to match the transmitter's discovered connections
      syncBlockStateConnections();
    }
  }

  private void syncBlockStateConnections() {
    if (level == null) return;
    BlockState state = getBlockState();
    if (state.getBlock() instanceof DuctBlock ductBlock) {
      DuctInteractions.updateConnections(level, worldPosition, ductBlock);
    }
  }

  protected abstract void syncToClient();
}
