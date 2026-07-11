package com.hypothetic.ten4.lib.capability.internet;

import com.hypothetic.ten4.lib.blockentity.internet.EnergyCableBlockEntity;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public class UniversalCable extends Transmitter<IEnergyStorage, EnergyNetwork, UniversalCable> {

  private long buffer;
  private final long capacity;

  public UniversalCable(ITransmitterTile tile, long capacity) {
    super(tile);
    this.capacity = capacity;
  }

  public long getBuffer() { return buffer; }
  public void setBuffer(long v) { buffer = Math.min(v, capacity); }
  public long getCapacity() { return capacity; }

  @Override
  public EnergyNetwork createEmptyNetwork(UUID id) { return new EnergyNetwork(id); }

  @Override @SuppressWarnings({"unchecked", "rawtypes"})
  public EnergyNetwork createNetworkByMerging(Collection<EnergyNetwork> nets) { return new EnergyNetwork((Collection) nets); }

  @Override
  public boolean supportsTransmission(Transmitter<?, ?, ?> other) {
    return other instanceof UniversalCable;
  }

  @Override
  protected boolean isValidAcceptor(Direction side) {
    return getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, getBlockPos().relative(side), side.getOpposite()) != null;
  }

  @Override
  protected @Nullable Transmitter<?, ?, ?> getTransmitter(ITransmitterTile t) {
    if (t instanceof EnergyCableBlockEntity be) return be.transmitter;
    return null;
  }

  @Override public void takeShare() {} // per-node buffer, nothing to split
  @Override public void remove() {}
}
