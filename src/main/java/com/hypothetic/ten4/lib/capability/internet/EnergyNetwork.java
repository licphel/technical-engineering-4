package com.hypothetic.ten4.lib.capability.internet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.*;

/**
 * Energy network: distributes energy stored in per-cable buffers.
 * No shared buffer — each cable owns its energy. Merge/split is lossless.
 */
public class EnergyNetwork extends DynamicNetwork<IEnergyStorage, EnergyNetwork, UniversalCable> {

  public EnergyNetwork(UUID id) { super(id); }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public EnergyNetwork(Collection<EnergyNetwork> nets) {
    super(UUID.randomUUID());
    adoptAllAndRegister((Collection) nets);
  }

  // --- Per-cable buffer helpers ---

  private long totalBuffer() {
    long sum = 0;
    for (UniversalCable t : getTransmitters()) sum += t.getBuffer();
    return sum;
  }

  private long totalCapacity() {
    long sum = 0;
    for (UniversalCable t : getTransmitters()) sum += t.getCapacity();
    return sum;
  }

  // --- Tick: push to consumers, pull from producers using per-cable buffers ---

  @Override
  public void onUpdate() {
    if (positionedTransmitters.isEmpty()) return;

    record Edge(BlockPos pos, Direction side) {}
    List<Edge> consumers = new ArrayList<>(), producers = new ArrayList<>();
    Level level = null;

    for (var e : positionedTransmitters.entrySet()) {
      BlockPos pos = e.getKey();
      UniversalCable cable = e.getValue();
      if (level == null) level = cable.getLevel();
      if (level == null) continue;
      for (Direction d : Direction.values()) {
        BlockPos t = pos.relative(d);
        if (positionedTransmitters.containsKey(t)) continue;
        ConnectionType ct = cable.getConnectionTypeRaw(d);
        IEnergyStorage cap = level.getCapability(Capabilities.EnergyStorage.BLOCK, t, d.getOpposite());
        if (cap == null) continue;
        if (cap.canReceive() && ct.canSendTo()) consumers.add(new Edge(t, d.getOpposite()));
        if (cap.canExtract() && ct.canAccept()) producers.add(new Edge(t, d.getOpposite()));
      }
    }
    if (level == null) return;

    // Push to consumers: drain from cables with most energy first
    for (Edge c : consumers) {
      IEnergyStorage cap = level.getCapability(Capabilities.EnergyStorage.BLOCK, c.pos, c.side);
      if (cap == null || !cap.canReceive()) continue;
      for (UniversalCable cable : sortedByBuffer(true)) {
        if (cable.getBuffer() <= 0) break;
        int v = (int) Math.min(cable.getBuffer(), Integer.MAX_VALUE);
        int accepted = cap.receiveEnergy(v, false);
        cable.setBuffer(cable.getBuffer() - accepted);
      }
    }

    // Pull from producers: fill cables with most room first
    for (Edge p : producers) {
      IEnergyStorage cap = level.getCapability(Capabilities.EnergyStorage.BLOCK, p.pos, p.side);
      if (cap == null || !cap.canExtract()) continue;
      for (UniversalCable cable : sortedByBuffer(false)) {
        long space = cable.getCapacity() - cable.getBuffer();
        if (space <= 0) break;
        int v = (int) Math.min(space, Integer.MAX_VALUE);
        int extracted = cap.extractEnergy(v, false);
        cable.setBuffer(cable.getBuffer() + extracted);
      }
    }
  }

  private List<UniversalCable> sortedByBuffer(boolean descending) {
    List<UniversalCable> list = new ArrayList<>(getTransmitters());
    list.sort((a, b) -> descending ? Long.compare(b.getBuffer(), a.getBuffer()) : Long.compare(b.getBuffer(), a.getBuffer()));
    return list;
  }

  // --- IEnergyStorage view (aggregates all cable buffers) ---

  public IEnergyStorage asStorage() {
    return new IEnergyStorage() {
      @Override public int receiveEnergy(int max, boolean sim) {
        long remaining = max;
        for (UniversalCable t : getTransmitters()) {
          long space = t.getCapacity() - t.getBuffer();
          if (space <= 0) continue;
          int v = (int) Math.min(space, remaining);
          if (!sim) t.setBuffer(t.getBuffer() + v);
          remaining -= v;
          if (remaining <= 0) break;
        }
        return (int)(max - remaining);
      }
      @Override public int extractEnergy(int max, boolean sim) {
        long remaining = max;
        for (UniversalCable t : getTransmitters()) {
          if (t.getBuffer() <= 0) continue;
          int v = (int) Math.min(t.getBuffer(), remaining);
          if (!sim) t.setBuffer(t.getBuffer() - v);
          remaining -= v;
          if (remaining <= 0) break;
        }
        return (int)(max - remaining);
      }
      @Override public int getEnergyStored() { return (int) Math.min(totalBuffer(), Integer.MAX_VALUE); }
      @Override public int getMaxEnergyStored() { return (int) Math.min(totalCapacity(), Integer.MAX_VALUE); }
      @Override public boolean canExtract() { return totalBuffer() > 0; }
      @Override public boolean canReceive() { return totalBuffer() < totalCapacity(); }
    };
  }
}
