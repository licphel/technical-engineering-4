package com.hypothetic.ten4.lib.blockentity.internet;

import com.hypothetic.ten4.init.ModBlockEntities;
import com.hypothetic.ten4.lib.capability.internet.*;
import com.hypothetic.ten4.lib.network.CableSyncPayload;
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

public class EnergyCableBlockEntity extends BlockEntity implements ITransmitterTile {

  public static final int CAPACITY = 100;
  public final UniversalCable transmitter;

  public EnergyCableBlockEntity(BlockPos pos, BlockState state, int bufferCapacity) {
    super(ModBlockEntities.GLASS_ENERGY_CABLE.get(), pos, state);
    this.transmitter = new UniversalCable(this, bufferCapacity);
  }

  // --- ITransmitterTile ---

  @Override public BlockPos getBlockPos() { return worldPosition; }
  @Override public boolean isInvalid() { return isRemoved(); }
  @Override public boolean isLoaded() { return level != null; }
  public void sendUpdatePacket() {
    if (level instanceof ServerLevel sl) {
      setChanged();
      var t = transmitter;
      Direction[] dirs = Direction.values(); // DOWN, UP, NORTH, SOUTH, WEST, EAST
      ConnectionType[] types = new ConnectionType[6];
      for (int i = 0; i < 6; i++) types[i] = t.getConnectionTypeRaw(dirs[i]);
      long buf = t.getNetwork() != null ? t.getNetwork().asStorage().getEnergyStored() : t.getBuffer();
      long cap = t.getNetwork() != null ? t.getNetwork().asStorage().getMaxEnergyStored() : t.getCapacity();
      CableSyncPayload pkt = new CableSyncPayload(worldPosition,
          t.getTransmitterConnections(), t.getAcceptorConnections(), types, buf, cap);
      PacketDistributor.sendToPlayersTrackingChunk(sl, sl.getChunkAt(worldPosition).getPos(), pkt);
    }
  }
  public void notifyTileChange() {
    if (level != null) {
      setChanged();
      level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
    }
  }

  // --- Lifecycle ---

  @Override
  public void onLoad() {
    super.onLoad();
    if (level != null && !level.isClientSide()) {
      transmitter.refreshConnections();
      TransmitterNetworkRegistry.registerOrphan(transmitter);
      sendUpdatePacket();
    }
  }

  @Override
  public void setRemoved() {
    super.setRemoved();
    if (level != null && !level.isClientSide()) {
      transmitter.remove();
      TransmitterNetworkRegistry.invalidateTransmitter(transmitter);
    }
  }

  @Override
  public void onChunkUnloaded() {
    super.onChunkUnloaded();
    if (level != null && !level.isClientSide()) {
      transmitter.validateAndTakeShare();
      TransmitterNetworkRegistry.invalidateTransmitter(transmitter);
    }
  }

  // --- Capability ---

  public @Nullable IEnergyStorage getCap(@Nullable Direction side) {
    if (level == null) return null;
    EnergyNetwork net = transmitter.getNetwork();
    return net != null ? net.asStorage() : null;
  }

  // --- Tick ---

  private int tickCount;

  public static <T extends BlockEntity> void tick(EnergyCableBlockEntity be) {
    if (be.level instanceof ServerLevel sl) {
      TransmitterNetworkRegistry.onServerTick();

      if (++be.tickCount % 10 == 0) {
        be.sendUpdatePacket();
      }
    }
  }

  // --- Persistence ---

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.loadAdditional(tag, reg);
    transmitter.read(reg, tag.getCompound("transmitter"));
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.saveAdditional(tag, reg);
    tag.put("transmitter", transmitter.write(reg, new CompoundTag()));
  }

  // Initial chunk sync: server → client via vanilla ClientboundBlockEntityDataPacket
  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider reg) {
    return transmitter.getReducedUpdateTag(reg, saveWithoutMetadata(reg));
  }

  @Override
  public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider reg) {
    super.handleUpdateTag(tag, reg);
    transmitter.handleUpdateTag(tag, reg);
  }

}
