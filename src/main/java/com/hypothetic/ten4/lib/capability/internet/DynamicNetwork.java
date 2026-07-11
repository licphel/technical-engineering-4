package com.hypothetic.ten4.lib.capability.internet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.neoforged.fml.util.thread.EffectiveSide;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A connected component of transmitters. ACCEPTOR = capability type on external blocks.
 * Pattern: ported from Mekanism's DynamicNetwork.
 */
public abstract class DynamicNetwork<ACCEPTOR,
                                     NETWORK extends DynamicNetwork<ACCEPTOR, NETWORK, TRANSMITTER>,
                                     TRANSMITTER extends Transmitter<ACCEPTOR, NETWORK, TRANSMITTER>> {

  final Map<BlockPos, TRANSMITTER> positionedTransmitters = new HashMap<>();
  final Set<TRANSMITTER> transmittersToAdd = new HashSet<>();
  private final UUID uuid;

  protected DynamicNetwork(UUID id) { this.uuid = id; }
  public UUID getUUID() { return uuid; }

  @SuppressWarnings("unchecked")
  NETWORK self() { return (NETWORK) this; }

  // --- Commit ---

  public void commit() {
    if (!transmittersToAdd.isEmpty()) {
      List<TRANSMITTER> toUpdate = new ArrayList<>();
      for (TRANSMITTER t : transmittersToAdd) {
        if (t != null && t.isValid()) {
          for (Direction d : Direction.values()) acceptorChanged(t, d);
          if (t.setNetwork(self(), false)) toUpdate.add(t);
          addTransmitterFromCommit(t);
        }
      }
      transmittersToAdd.clear();
      if (!toUpdate.isEmpty()) { onTransmittersAdded(toUpdate); toUpdate.forEach(Transmitter::requestsUpdate); }
    }
  }

  protected void onTransmittersAdded(List<TRANSMITTER> added) {}
  void addNewTransmitters(Collection<TRANSMITTER> ts) { transmittersToAdd.addAll(ts); }
  void addTransmitterFromCommit(TRANSMITTER t) { positionedTransmitters.put(t.getBlockPos(), t); }

  @Nullable public TRANSMITTER getTransmitter(BlockPos pos) { return positionedTransmitters.get(pos); }
  public Collection<TRANSMITTER> getTransmitters() { return positionedTransmitters.values(); }
  public int size() { return positionedTransmitters.size(); }
  public boolean isEmpty() { return positionedTransmitters.isEmpty(); }
  public void addTransmitter(TRANSMITTER t) { positionedTransmitters.put(t.getBlockPos(), t); }
  public void removeTransmitter(TRANSMITTER t) { if (getTransmitter(t.getBlockPos()) == t) positionedTransmitters.remove(t.getBlockPos()); if (isEmpty()) deregister(); }

  // --- Invalidation ---

  public void invalidate(@Nullable TRANSMITTER trigger) {
    if (size() == 1 && trigger != null && !trigger.isValid()) onLastTransmitterRemoved(trigger);
    removeInvalid(trigger);
    if (!isRemote()) for (TRANSMITTER t : getTransmitters()) if (t.isValid()) { t.validateAndTakeShare(); t.setNetwork(null, false); TransmitterNetworkRegistry.registerOrphan(t); }
    deregister();
  }

  protected void onLastTransmitterRemoved(TRANSMITTER trigger) {}
  protected void removeInvalid(@Nullable TRANSMITTER trigger) { getTransmitters().removeIf(t -> !t.isValid()); }

  // --- Merge ---

  List<TRANSMITTER> adoptFrom(NETWORK other) {
    List<TRANSMITTER> toUpdate = new ArrayList<>();
    for (var e : other.positionedTransmitters.entrySet()) { TRANSMITTER t = e.getValue(); positionedTransmitters.put(e.getKey(), t); if (t.setNetwork(self(), false)) toUpdate.add(t); }
    transmittersToAdd.addAll(other.transmittersToAdd);
    return toUpdate;
  }

  @SuppressWarnings("unchecked")
  protected void adoptAllAndRegister(Collection<NETWORK> nets) {
    List<TRANSMITTER> toUpdate = new ArrayList<>();
    for (NETWORK n : nets) { if (n != null && n != this) { toUpdate.addAll(adoptFrom(n)); n.deregister(); } }
    register();
    toUpdate.forEach(Transmitter::requestsUpdate);
  }

  // --- Registration ---

  public void register() {
    if (isRemote()) TransmitterNetworkRegistry.addClientNetwork(uuid, this);
    else TransmitterNetworkRegistry.registerNetwork(this);
  }

  public void deregister() {
    positionedTransmitters.clear(); transmittersToAdd.clear();
    if (isRemote()) TransmitterNetworkRegistry.removeClientNetwork(this);
    else TransmitterNetworkRegistry.removeNetwork(this);
  }

  // --- Acceptor cache (override in subclasses) ---

  public void acceptorChanged(TRANSMITTER t, Direction side) {}

  boolean isRemote() { return EffectiveSide.get().isClient(); }

  /** Called every server tick. */
  public void onUpdate() {}

  @Override public boolean equals(Object o) { return o instanceof DynamicNetwork<?,?,?> other && uuid.equals(other.uuid); }
  @Override public int hashCode() { return uuid.hashCode(); }
}
