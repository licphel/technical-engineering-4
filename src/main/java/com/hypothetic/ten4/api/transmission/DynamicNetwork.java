package com.hypothetic.ten4.api.transmission;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.neoforged.fml.util.thread.EffectiveSide;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class DynamicNetwork<AC, NET extends DynamicNetwork<AC, NET, T>, T extends Transmitter<AC, NET, T>> {
  public final Map<BlockPos, T> positionedTransmitters = new HashMap<>();
  public final Set<T> transmittersToAdd = new HashSet<>();
  private final UUID uuid;

  protected DynamicNetwork(UUID id) {
    this.uuid = id;
  }

  public UUID getUUID() {
    return uuid;
  }

  @SuppressWarnings("unchecked")
  NET self() {
    return (NET) this;
  }

  public void commit() {
    if (!transmittersToAdd.isEmpty()) {
      List<T> toUpdate = new ArrayList<>();
      for (T t : transmittersToAdd) {
        if (t != null && t.isValid()) {
          for (Direction d : Direction.values()) {
            acceptorChanged(t, d);
          }
          if (t.setNetwork(self(), false)) {
            toUpdate.add(t);
          }
          addTransmitterFromCommit(t);
        }
      }
      transmittersToAdd.clear();
      if (!toUpdate.isEmpty()) {
        onTransmittersAdded(toUpdate);
        toUpdate.forEach(Transmitter::requestsUpdate);
      }
    }
  }

  protected void onTransmittersAdded(List<T> added) {
  }

  void addNewTransmitters(Collection<T> ts) {
    transmittersToAdd.addAll(ts);
  }

  void addNewTransmitters(Collection<T> ts, CompatibleTransmitterValidator<?, ?, ?> validator) {
    transmittersToAdd.addAll(ts);
    if (this instanceof DynamicBufferedNetwork<?, ?, ?, ?> bn) {
      bn.transmitterValidator = validator;
    }
  }

  protected void addTransmitterFromCommit(T t) {
    positionedTransmitters.put(t.getBlockPos(), t);
  }

  @Nullable
  public T getTransmitter(BlockPos pos) {
    return positionedTransmitters.get(pos);
  }

  public Collection<T> getTransmitters() {
    return positionedTransmitters.values();
  }

  public int size() {
    return positionedTransmitters.size();
  }

  public boolean isEmpty() {
    return positionedTransmitters.isEmpty();
  }

  public void addTransmitter(T t) {
    positionedTransmitters.put(t.getBlockPos(), t);
  }

  public void removeTransmitter(T t) {
    if (getTransmitter(t.getBlockPos()) == t) {
      positionedTransmitters.remove(t.getBlockPos());
    }
    if (isEmpty()) {
      deregister();
    }
  }

  public void invalidate(@Nullable T trigger) {
    if (size() == 1 && trigger != null && !trigger.isValid()) {
      onLastTransmitterRemoved(trigger);
    }
    removeInvalid(trigger);
    if (!isRemote()) {
      for (T t : getTransmitters()) {
        if (t.isValid()) {
          t.validateAndTakeShare();
          t.setNetwork(null, false);
          TransmitterNetworkRegistry.joinNetwork(t);
        }
      }
    }
    deregister();
  }

  protected void onLastTransmitterRemoved(T trigger) {
  }

  protected void removeInvalid(@Nullable T trigger) {
    getTransmitters().removeIf(t -> !t.isValid());
  }

  protected List<T> adoptFrom(NET other) {
    List<T> toUpdate = new ArrayList<>();
    for (var e : other.positionedTransmitters.entrySet()) {
      T t = e.getValue();
      positionedTransmitters.put(e.getKey(), t);
      if (t.setNetwork(self(), false)) {
        toUpdate.add(t);
      }
    }
    transmittersToAdd.addAll(other.transmittersToAdd);
    return toUpdate;
  }

  protected void adoptAllAndRegister(Collection<NET> nets) {
    List<T> toUpdate = new ArrayList<>();
    for (NET n : nets) {
      if (n != null && n != this) {
        toUpdate.addAll(adoptFrom(n));
        n.deregister();
      }
    }
    register();
    toUpdate.forEach(Transmitter::requestsUpdate);
  }

  public void register() {
    if (isRemote()) {
      TransmitterNetworkRegistry.addClientNetwork(uuid, this);
    } else {
      TransmitterNetworkRegistry.registerNetwork(this);
    }
  }

  public void deregister() {
    positionedTransmitters.clear();
    transmittersToAdd.clear();
    if (isRemote()) {
      TransmitterNetworkRegistry.removeClientNetwork(this);
    } else {
      TransmitterNetworkRegistry.removeNetwork(this);
    }
  }

  public void acceptorChanged(T t, Direction side) {
  }

  boolean isRemote() {
    return EffectiveSide.get().isClient();
  }

  public void onUpdate() {
  }

  @Override
  public int hashCode() {
    return uuid.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof DynamicNetwork<?, ?, ?> other && uuid.equals(other.uuid);
  }
}
