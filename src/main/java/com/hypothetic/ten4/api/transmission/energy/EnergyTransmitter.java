package com.hypothetic.ten4.api.transmission.energy;

import com.hypothetic.ten4.api.blockentity.internet.EnergyDuctBlockEntity;
import com.hypothetic.ten4.api.transmission.BufferedTransmitter;
import com.hypothetic.ten4.api.transmission.ITransmitterProvider;
import com.hypothetic.ten4.api.transmission.Transmitter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public class EnergyTransmitter extends BufferedTransmitter<IEnergyStorage, EnergyNetwork, Long, EnergyTransmitter> {
  private final long capacity;
  private long buffer; // local buffer when orphaned; network buffer when connected

  public EnergyTransmitter(ITransmitterProvider tile, long capacity) {
    super(tile);
    this.capacity = capacity;
  }

  // ---- BufferedTransmitter ----
  @Override public long getCapacity() { return capacity; }

  @Override public Long releaseShare() {
    long share = buffer;
    buffer = 0;
    return share;
  }

  @Override public void takeShare() {
    EnergyNetwork net = getNetwork();
    if (net != null && net.getBuffer() != null) {
      int size = net.getTransmitters().size();
      if (size > 0) {
        long share = net.getBuffer() / size;
        net.setBuffer(net.getBuffer() - share);
        buffer = share;
      }
    }
  }

  /** Local buffer for orphaned state or BE access. */
  public long getBuffer() { return buffer; }
  public void setBuffer(long v) { buffer = v; }

  /** Network-level energy (if connected). Called by external capability. */
  public long getNetworkEnergy() {
    EnergyNetwork net = getNetwork();
    return net != null ? net.getBuffer() : 0;
  }

  @Override
  @Nullable public IEnergyStorage getAcceptor(Direction side, Level level, BlockPos targetPos) {
    return level.getCapability(Capabilities.EnergyStorage.BLOCK, targetPos, side.getOpposite());
  }

  // ---- Transmitter boilerplate ----
  @Override public EnergyNetwork createEmptyNetwork(UUID id) { return new EnergyNetwork(id); }
  @Override public EnergyNetwork createNetworkByMerging(Collection<EnergyNetwork> nets) { return new EnergyNetwork(nets); }
  @Override public boolean supportsTransmission(Transmitter<?, ?, ?> other) { return other instanceof EnergyTransmitter; }
  @Override protected boolean isValidAcceptor(Direction side) {
    if (getLevel() == null) return false;
    return getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, getBlockPos().relative(side), side.getOpposite()) != null;
  }
  @Override protected @Nullable Transmitter<?, ?, ?> getTransmitter(ITransmitterProvider t) {
    if (t instanceof EnergyDuctBlockEntity be) return be.transmitter;
    return null;
  }
}
