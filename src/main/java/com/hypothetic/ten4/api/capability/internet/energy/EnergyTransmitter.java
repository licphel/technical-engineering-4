package com.hypothetic.ten4.api.capability.internet.energy;

import com.hypothetic.ten4.api.blockentity.internet.EnergyDuctBlockEntity;
import com.hypothetic.ten4.api.capability.internet.ITransmitterProvider;
import com.hypothetic.ten4.api.capability.internet.Transmitter;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public class EnergyTransmitter extends Transmitter<IEnergyStorage, EnergyNetwork, EnergyTransmitter> {
  private final long capacity;
  private long buffer;

  public EnergyTransmitter(ITransmitterProvider tile, long capacity) {
    super(tile);
    this.capacity = capacity;
  }

  public long getBuffer() {
    return buffer;
  }

  public void setBuffer(long v) {
    buffer = Math.min(v, capacity);
  }

  public long getCapacity() {
    return capacity;
  }

  @Override
  public EnergyNetwork createEmptyNetwork(UUID id) {
    return new EnergyNetwork(id);
  }

  @Override
  public EnergyNetwork createNetworkByMerging(Collection<EnergyNetwork> nets) {
    return new EnergyNetwork(nets);
  }

  @Override
  public boolean supportsTransmission(Transmitter<?, ?, ?> other) {
    return other instanceof EnergyTransmitter;
  }

  @Override
  protected boolean isValidAcceptor(Direction side) {
    if (getLevel() == null) {
      return false;
    }
    return getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, getBlockPos().relative(side), side.getOpposite()) != null;
  }

  @Override
  protected @Nullable Transmitter<?, ?, ?> getTransmitter(ITransmitterProvider t) {
    if (t instanceof EnergyDuctBlockEntity be) {
      return be.transmitter;
    }
    return null;
  }

  @Override
  public void takeShare() {
  }
}
