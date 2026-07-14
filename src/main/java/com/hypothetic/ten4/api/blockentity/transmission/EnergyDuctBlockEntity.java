package com.hypothetic.ten4.api.blockentity.transmission;

import com.hypothetic.ten4.api.blockentity.ITickable;
import com.hypothetic.ten4.api.network.duct.DuctEnergyPayload;
import com.hypothetic.ten4.api.transmission.energy.EnergyNetwork;
import com.hypothetic.ten4.api.transmission.energy.EnergyTransmitter;
import com.hypothetic.ten4.api.transmission.energy.TransmitterEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public abstract class EnergyDuctBlockEntity extends DuctBlockEntity<EnergyTransmitter> implements ITickable {
  private final IEnergyStorage energyStorage;
  private int tickCount;

  public EnergyDuctBlockEntity(BlockPos pos, BlockState state) {
    super(pos, state);
    this.transmitter = new EnergyTransmitter(this, info.bufferCapacity, info.throughput);
    this.energyStorage = new TransmitterEnergyStorage(transmitter);
  }

  @Override
  public void tick() {
    if (level == null || level.isClientSide()) {
      return;
    }

    if (tickCount++ % 10 == 0) {
      syncToClient();
    }
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.loadAdditional(tag, reg);

    transmitter.setBuffer(tag.getLong("Buffer"));
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.saveAdditional(tag, reg);

    tag.putLong("Buffer", transmitter.getBuffer());
  }

  @Override
  protected void syncToClient() {
    if (level instanceof ServerLevel sl) {
      EnergyNetwork net = transmitter.getNetwork();
      float scale = net != null ? net.currentScale : (transmitter.getCapacity() > 0 ? (float) transmitter.getBuffer() / transmitter.getCapacity() : 0);
      PacketDistributor.sendToPlayersTrackingChunk(sl, sl.getChunkAt(worldPosition).getPos(),
          new DuctEnergyPayload(worldPosition, scale));
    }
  }

  public @Nullable IEnergyStorage getEnergyStorage(@Nullable Direction side) {
    return energyStorage;
  }
}
