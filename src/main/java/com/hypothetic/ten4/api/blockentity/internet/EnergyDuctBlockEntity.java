package com.hypothetic.ten4.api.blockentity.internet;

import com.hypothetic.ten4.api.capability.internet.ConnectionType;
import com.hypothetic.ten4.api.capability.internet.ITransmitterProvider;
import com.hypothetic.ten4.api.capability.internet.Transmitter;
import com.hypothetic.ten4.api.capability.internet.TransmitterNetworkRegistry;
import com.hypothetic.ten4.api.capability.internet.energy.EnergyNetwork;
import com.hypothetic.ten4.api.capability.internet.energy.EnergyTransmitter;
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

public class EnergyDuctBlockEntity extends BlockEntity implements ITransmitterProvider {
  public static final int CAPACITY = 100;
  public final EnergyTransmitter transmitter;
  private int tickCount;

  public EnergyDuctBlockEntity(BlockPos pos, BlockState state, int bufferCapacity) {
    super(ModBlockEntities.COPPER_ENERGY_DUCT.get(), pos, state);
    this.transmitter = new EnergyTransmitter(this, bufferCapacity);
  }

  public static <T extends BlockEntity> void tick(EnergyDuctBlockEntity be) {
    if (be.level instanceof ServerLevel) {
      TransmitterNetworkRegistry.onServerTick(be.level);

      if (be.tickCount++ % 10 == 0) {
        be.sendEnergyUpdate();
      }
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
      var t = transmitter;
      long buf = t.getNetwork() != null ? t.getNetwork().asStorage().getEnergyStored() : t.getBuffer();
      long cap = t.getNetwork() != null ? t.getNetwork().asStorage().getMaxEnergyStored() : t.getCapacity();
      PacketDistributor.sendToPlayersTrackingChunk(sl, sl.getChunkAt(worldPosition).getPos(),
          new DuctEnergyPayload(worldPosition, buf, cap));
    }
  }

  public @Nullable IEnergyStorage getEnergyStorage(@Nullable Direction side) {
    if (level == null) {
      return null;
    }
    EnergyNetwork net = transmitter.getNetwork();
    return net != null ? net.asStorage() : null;
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
      TransmitterNetworkRegistry.invalidateTransmitter(transmitter);
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
      TransmitterNetworkRegistry.invalidateTransmitter(transmitter);
    }
  }

  @Override
  public void onLoad() {
    super.onLoad();
    if (level != null && !level.isClientSide()) {
      transmitter.refreshConnections();
      TransmitterNetworkRegistry.registerOrphan(transmitter);
      sendEnergyUpdate();
    }
  }
}
