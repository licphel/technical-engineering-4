package com.hypothetic.ten4.api.transmission;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Mekanism-style buffered network base.
 * Holds a single network-level buffer (energy/fluid) and a smooth {@link #currentScale}
 * for client rendering. Distribution is "instant" — no per-transmitter progress.
 */
public abstract class DynamicBufferedNetwork<AC, NET extends DynamicBufferedNetwork<AC, NET, BUF, T>,
    BUF, T extends BufferedTransmitter<AC, NET, BUF, T>>
    extends DynamicNetwork<AC, NET, T> {

  protected final Set<Long> chunks = new HashSet<>();
  protected long capacity;
  protected boolean needsUpdate;
  public float currentScale;
  @Nullable CompatibleTransmitterValidator<?, ?, ?> transmitterValidator;

  protected DynamicBufferedNetwork(UUID id) { super(id); }

  // ---- Scale (client rendering) ----

  /** Called each tick; should return 0..1 target fill ratio. */
  protected abstract float computeContentScale();

  protected abstract void forceScaleUpdate();

  @Override
  public void onUpdate() {
    super.onUpdate();
    float scale = computeContentScale();
    if (scale != currentScale) {
      currentScale = scale;
      needsUpdate = true;
    }
  }

  // ---- Buffer ----

  @Nullable public abstract BUF getBuffer();

  /** Absorb a transmitter's saved share when it joins the network. */
  public abstract void absorbBuffer(T transmitter);

  /** Clamp buffer to capacity after a transmitter is removed. */
  public abstract void clampBuffer();

  // ---- Capacity ----

  protected void updateCapacity(T transmitter) {
    long c = transmitter.getCapacity();
    capacity = capacity > Long.MAX_VALUE - c ? Long.MAX_VALUE : capacity + c;
  }

  protected void updateCapacity() {
    long sum = 0;
    for (T t : getTransmitters()) {
      long c = t.getCapacity();
      sum = sum > Long.MAX_VALUE - c ? Long.MAX_VALUE : sum + c;
    }
    capacity = sum;
  }

  public long getCapacity() { return capacity; }
  public @Nullable CompatibleTransmitterValidator<?, ?, ?> getValidator() { return transmitterValidator; }

  // ---- Transmitter add/remove ----

  @Override
  protected void addTransmitterFromCommit(T t) {
    super.addTransmitterFromCommit(t);
    chunks.add(ChunkPos.asLong(t.getBlockPos()));
    updateCapacity(t);
    absorbBuffer(t);
  }

  @Override
  protected void removeInvalid(@Nullable T trigger) {
    super.removeInvalid(trigger);
    clampBuffer();
  }

  @Override
  protected void onTransmittersAdded(List<T> added) {
    clampBuffer();
    forceScaleUpdate();
    needsUpdate = true;
  }

  @Override
  public void deregister() {
    super.deregister();
    chunks.clear();
  }

  // ---- Network merge ----

  @Override
  protected List<T> adoptFrom(NET other) {
    List<T> list = super.adoptFrom(other);
    chunks.addAll(other.chunks);
    updateCapacity();
    return list;
  }

  // ---- Mark dirty ----

  public void markDirty() {
    for (T t : getTransmitters()) {
      t.getTile().setChanged();
    }
  }

  // Helpers for subclasses
  protected <H extends AC> long emitToAcceptors(long amount, java.util.function.BiFunction</*capability*/ H, Long, Long> sendFn) {
    // collect unique acceptors
    List<H> acceptors = new ArrayList<>();
    Level level = null;
    for (var e : positionedTransmitters.entrySet()) {
      BlockPos pos = e.getKey();
      T cable = e.getValue();
      if (level == null) level = cable.getLevel();
      if (level == null) continue;
      for (net.minecraft.core.Direction d : net.minecraft.core.Direction.values()) {
        BlockPos t = pos.relative(d);
        if (positionedTransmitters.containsKey(t)) continue;
        if (level.getBlockEntity(t) instanceof ITransmitterProvider) continue;
        H cap = (H) cable.getAcceptor(d, level, t);
        if (cap != null) acceptors.add(cap);
      }
    }
    if (acceptors.isEmpty() || amount <= 0) return 0;

    int n = acceptors.size();
    long share = amount / n;
    long totalSent = 0;

    // first pass: each takes up to share
    for (H a : acceptors) {
      long sent = sendFn.apply(a, share);
      totalSent += sent;
    }

    // second pass: distribute remainder to whoever can take it
    long leftover = amount - totalSent;
    if (leftover > 0) {
      for (H a : acceptors) {
        long sent = sendFn.apply(a, leftover);
        totalSent += sent;
        leftover -= sent;
        if (leftover <= 0) break;
      }
    }
    return totalSent;
  }
}
