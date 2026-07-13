package com.hypothetic.ten4.api.transmission.energy;

import com.hypothetic.ten4.api.transmission.DynamicBufferedNetwork;
import com.hypothetic.ten4.api.transmission.ITransmitterProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.*;

/**
 * Mekanism-style energy network — single network-level buffer, instant transfer,
 * fair distribution via emitToAcceptors.
 */
public class EnergyNetwork extends DynamicBufferedNetwork<IEnergyStorage, EnergyNetwork, Long, EnergyTransmitter> {

  private long buffer; // network-level energy

  public EnergyNetwork(UUID id) { super(id); }
  public EnergyNetwork(Collection<EnergyNetwork> nets) {
    super(UUID.randomUUID());
    adoptAllAndRegister(nets);
  }

  // ---- Scale ----
  @Override protected void forceScaleUpdate() {
    currentScale = capacity > 0 ? (float) buffer / capacity : 0;
  }

  @Override protected float computeContentScale() {
    float scale = capacity > 0 ? (float) buffer / capacity : 0;
    float ret = Math.max(currentScale, scale);
    if (ret < 1 && scale > 0) ret = Math.min(1, ret + 0.02F);
    else if (scale <= 0 && ret > 0) ret = Math.max(scale, ret - 0.02F);
    return ret;
  }

  // ---- Buffer ----
  @Override public Long getBuffer() { return buffer; }
  public void setBuffer(long v) { buffer = v; }

  @Override public void absorbBuffer(EnergyTransmitter t) {
    long share = t.releaseShare();
    if (share > 0) buffer = Math.min(buffer + share, capacity);
  }

  @Override public void clampBuffer() {
    if (buffer > capacity) buffer = capacity;
  }

  // ---- Tick ----
  @Override
  public void onUpdate() {
    super.onUpdate();

    Level level = null;
    // 0. PULL: collect all producers first, then divide space fairly
    record Edge(BlockPos pos, Direction side) {}
    Set<Edge> pullSeen = new HashSet<>();
    List<IEnergyStorage> producers = new ArrayList<>();
    for (var e : positionedTransmitters.entrySet()) {
      BlockPos pos = e.getKey();
      EnergyTransmitter cable = e.getValue();
      if (level == null) level = cable.getLevel();
      if (level == null) continue;
      for (Direction d : Direction.values()) {
        if (cable.getConnectionTypeRaw(d) != com.hypothetic.ten4.api.transmission.ConnectionType.PULL) continue;
        BlockPos t = pos.relative(d);
        if (positionedTransmitters.containsKey(t)) continue;
        if (level.getBlockEntity(t) instanceof ITransmitterProvider) continue;
        Edge edge = new Edge(t, d.getOpposite());
        if (!pullSeen.add(edge)) continue;
        IEnergyStorage src = level.getCapability(Capabilities.EnergyStorage.BLOCK, t, d.getOpposite());
        if (src != null && src.canExtract()) producers.add(src);
      }
    }
    // Fair pull: each round divides remaining space evenly among producers
    long space = capacity - buffer;
    if (space > 0 && !producers.isEmpty()) {
      long toPull = Math.min(space, capacity);
      long totalPulled = 0;
      List<IEnergyStorage> srcs = new ArrayList<>(producers);
      while (!srcs.isEmpty() && toPull > 0) {
        long share = toPull / srcs.size();
        if (share == 0) share = toPull;
        var it = srcs.iterator();
        while (it.hasNext()) {
          IEnergyStorage src = it.next();
          int pulled = src.extractEnergy((int) Math.min(share, Integer.MAX_VALUE), false);
          totalPulled += pulled;
          toPull -= pulled;
          if (pulled < share) it.remove(); // depleted
        }
      }
      buffer += totalPulled;
    }
    if (buffer <= 0) return;

    // 1. PUSH to consumers (fair: collect all, divide buffer equally)
    Set<Edge> pushSeen = new HashSet<>();
    List<IEnergyStorage> acceptors = new ArrayList<>();

    for (var e : positionedTransmitters.entrySet()) {
      BlockPos pos = e.getKey();
      EnergyTransmitter cable = e.getValue();
      if (level == null) level = cable.getLevel();
      if (level == null) continue;
      for (Direction d : Direction.values()) {
        BlockPos t = pos.relative(d);
        if (positionedTransmitters.containsKey(t)) continue;
        if (level.getBlockEntity(t) instanceof ITransmitterProvider) continue; // skip cables from other networks
        if (!cable.getConnectionTypeRaw(d).canSendTo()) continue;
        Edge edge = new Edge(t, d.getOpposite());
        if (!pushSeen.add(edge)) continue;
        IEnergyStorage cap = level.getCapability(Capabilities.EnergyStorage.BLOCK, t, d.getOpposite());
        if (cap != null && cap.canReceive() && cap.getEnergyStored() < cap.getMaxEnergyStored()) {
          acceptors.add(cap);
        }
      }
    }
    // Fair round-robin: each round divides remaining evenly among still-needy acceptors
    long toSend = buffer;
    long totalSent = 0;
    List<IEnergyStorage> needy = new ArrayList<>(acceptors);
    while (!needy.isEmpty() && toSend > 0) {
      long share = toSend / needy.size();
      if (share == 0) share = toSend;
      var it = needy.iterator();
      while (it.hasNext()) {
        IEnergyStorage a = it.next();
        int sent = a.receiveEnergy((int) Math.min(share, Integer.MAX_VALUE), false);
        totalSent += sent;
        toSend -= sent;
        if (sent < share || a.getEnergyStored() >= a.getMaxEnergyStored()) {
          it.remove(); // saturated
        }
      }
    }
    buffer -= totalSent;
  }

  // ---- Merge ----
  @Override protected List<EnergyTransmitter> adoptFrom(EnergyNetwork other) {
    long oldCap = getCapacity();
    List<EnergyTransmitter> list = super.adoptFrom(other);
    // merge buffers
    long ourScale = (long)(currentScale * oldCap);
    long theirScale = (long)(other.currentScale * other.capacity);
    long cap = getCapacity();
    currentScale = cap == 0 ? 0 : Math.min(1, (ourScale + theirScale) / (float)cap);
    if (!other.bufferWasZero()) {
      buffer += other.buffer;
      other.buffer = 0;
    }
    return list;
  }

  private boolean bufferWasZero() { return buffer == 0; }
}
