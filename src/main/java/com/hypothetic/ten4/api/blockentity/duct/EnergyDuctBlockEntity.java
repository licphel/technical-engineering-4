package com.hypothetic.ten4.api.blockentity.duct;

import com.hypothetic.ten4.api.blockentity.ITickable;
import com.hypothetic.ten4.core.client.renderer.RenderTransmitterBlock;
import com.hypothetic.ten4.api.network.PacketDist;
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
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class EnergyDuctBlockEntity extends DuctBlockEntity<EnergyTransmitter> implements ITickable {
  private final Map<Direction, TransmitterEnergyStorage> energyStorages = new EnumMap<>(Direction.class);

  public EnergyDuctBlockEntity(BlockPos pos, BlockState state, DuctInfo info) {
    super(pos, state, info);
    this.transmitter = new EnergyTransmitter(this, info.bufferCapacity, info.throughput);
    for (Direction dir : Direction.values()) {
      energyStorages.put(dir, new TransmitterEnergyStorage(transmitter, dir));
    }
  }

  @Override
  public void tick() {
    if (level == null || level.isClientSide()) {
      return;
    }

    if (level.getGameTime() % 5 == 0) {
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
    EnergyNetwork net = transmitter.getNetwork();
    if (net != null) {
      long total = net.getBuffer();
      tag.putLong("Buffer", total > 0 ? Math.max(1, total / net.size()) : 0);
    } else {
      tag.putLong("Buffer", transmitter.getBuffer());
    }
  }

  @Override
  protected void syncToClient() {
    if (level instanceof ServerLevel sl) {
      EnergyNetwork net = transmitter.getNetwork();
      float scale = net != null ? net.currentScale : (transmitter.getCapacity() > 0 ? (float) transmitter.getBuffer() / transmitter.getCapacity() : 0);
      PacketDist.sendToNearbyPlayers(sl, new DuctEnergyPayload(worldPosition, scale), getBlockPos(),
          RenderTransmitterBlock.LOD_DISTANCE.getAsInt());
    }
  }

  public @Nullable IEnergyStorage getEnergyStorage(@Nullable Direction side) {
    return side != null ? energyStorages.get(side) : null;
  }
}
