package com.hypothetic.ten4.api.transmission;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;

public abstract class BufferedNetwork<AC, NET extends BufferedNetwork<AC, NET, BUF, T>,
    BUF, T extends BufferedTransmitter<AC, NET, BUF, T>>
    extends Network<AC, NET, T> {
  protected final Set<Long> chunks = new HashSet<>();
  public float currentScale;
  protected long capacity;
  protected boolean needsUpdate;
  @Nullable CompatibleTransmitterValidator<?, ?, ?> transmitterValidator;
  private long cachedThroughput = -1;

  protected BufferedNetwork(UUID id) {
    super(id);
  }

  protected abstract float computeContentScale();

  protected abstract void forceScaleUpdate();

  public abstract @Nullable BUF getBuffer();

  public abstract void absorbBuffer(T transmitter);

  public abstract void clampBuffer();

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

  public long getCapacity() {
    return capacity;
  }

  public long netThroughput() {
    if (cachedThroughput < 0) {
      long min = Long.MAX_VALUE;
      for (T t : getTransmitters()) {
        min = Math.min(min, t.getThroughput());
      }
      cachedThroughput = min;
    }
    return cachedThroughput;
  }

  protected void invalidateThroughput() {
    cachedThroughput = -1;
  }

  public @Nullable CompatibleTransmitterValidator<?, ?, ?> getValidator() {
    return transmitterValidator;
  }

  @Override
  protected void onTransmittersAdded(List<T> added) {
    clampBuffer();
    forceScaleUpdate();
    needsUpdate = true;
    invalidateThroughput();
  }

  @Override
  protected void addTransmitterFromCommit(T t) {
    super.addTransmitterFromCommit(t);
    chunks.add(ChunkPos.asLong(t.getBlockPos()));
    updateCapacity(t);
    absorbBuffer(t);
    invalidateThroughput();
  }

  @Override
  protected void removeInvalid(@Nullable T trigger) {
    super.removeInvalid(trigger);
    clampBuffer();
    invalidateThroughput();
  }

  @Override
  protected List<T> adoptFrom(NET other) {
    List<T> list = super.adoptFrom(other);
    chunks.addAll(other.chunks);
    updateCapacity();
    invalidateThroughput();
    return list;
  }

  @Override
  public void deregister() {
    super.deregister();
    chunks.clear();
  }

  @Override
  public void onUpdate() {
    super.onUpdate();
    float scale = computeContentScale();
    if (scale != currentScale) {
      currentScale = scale;
      needsUpdate = true;
    }
  }

  public void markDirty() {
    for (T t : getTransmitters()) {
      t.getTile().notifyChanges();
    }
  }

  protected <H extends AC> long emitToAcceptors(long amount, BiFunction<H, Long, Long> sendFn) {
    // collect unique acceptors
    List<H> acceptors = new ArrayList<>();
    Level level = null;
    for (Map.Entry<BlockPos, T> e : positionedTransmitters.entrySet()) {
      BlockPos pos = e.getKey();
      T cable = e.getValue();
      if (level == null) {
        level = cable.getLevel();
      }
      if (level == null) {
        continue;
      }
      for (Direction d : Direction.values()) {
        BlockPos t = pos.relative(d);
        if (positionedTransmitters.containsKey(t)) {
          continue;
        }
        if (level.getBlockEntity(t) instanceof ITransmitterProvider) {
          continue;
        }
        @SuppressWarnings("unchecked") H cap = (H) cable.getAcceptor(d, level, t);
        if (cap != null) {
          acceptors.add(cap);
        }
      }
    }
    if (acceptors.isEmpty() || amount <= 0) {
      return 0;
    }

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
        if (leftover <= 0) {
          break;
        }
      }
    }
    return totalSent;
  }
}
