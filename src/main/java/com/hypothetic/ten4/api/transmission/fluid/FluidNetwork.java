package com.hypothetic.ten4.api.transmission.fluid;

import com.hypothetic.ten4.api.transmission.DynamicBufferedNetwork;
import com.hypothetic.ten4.api.transmission.ITransmitterProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.*;

/**
 * Mekanism-style fluid network — single network-level FluidStack buffer, instant transfer.
 * Fluid conflict detection on merge.
 */
public class FluidNetwork extends DynamicBufferedNetwork<IFluidHandler, FluidNetwork, FluidStack, FluidTransmitter> {

  private FluidStack buffer = FluidStack.EMPTY;
  private int prevTransferAmount;

  public FluidNetwork(UUID id) { super(id); }
  public FluidNetwork(Collection<FluidNetwork> nets) {
    super(UUID.randomUUID());
    adoptAllAndRegister(nets);
  }

  // ---- Scale ----
  @Override protected void forceScaleUpdate() {
    currentScale = capacity > 0 ? (float) buffer.getAmount() / capacity : 0;
  }

  @Override protected float computeContentScale() {
    float scale = capacity > 0 ? (float) buffer.getAmount() / capacity : 0;
    float ret = Math.max(currentScale, scale);
    if (prevTransferAmount > 0 && ret < 1) ret = Math.min(1, ret + 0.02F);
    else if (prevTransferAmount <= 0 && ret > 0) ret = Math.max(scale, ret - 0.02F);
    return ret;
  }

  // ---- Buffer ----
  @Override public FluidStack getBuffer() { return buffer.copy(); }
  public FluidStack getFluid() { return buffer; }

  @Override public void absorbBuffer(FluidTransmitter t) {
    FluidStack share = t.releaseShare();
    if (!share.isEmpty()) {
      if (buffer.isEmpty()) buffer = share.copy();
      else if (FluidStack.isSameFluidSameComponents(buffer, share))
        buffer.setAmount(buffer.getAmount() + share.getAmount());
    }
  }

  @Override public void clampBuffer() {
    if (!buffer.isEmpty() && buffer.getAmount() > capacity)
      buffer.setAmount((int) capacity);
  }

  // ---- Tick ----
  @Override
  public void onUpdate() {
    super.onUpdate();

    Level level = null;
    // 0. PULL: collect producers first, then divide space fairly
    record Edge(BlockPos pos, Direction side) {}
    Set<Edge> pullSeen = new HashSet<>();
    List<IFluidHandler> producers = new ArrayList<>();
    for (var e : positionedTransmitters.entrySet()) {
      BlockPos pos = e.getKey();
      FluidTransmitter cable = e.getValue();
      if (level == null) level = cable.getLevel();
      if (level == null) continue;
      for (Direction d : Direction.values()) {
        if (cable.getConnectionTypeRaw(d) != com.hypothetic.ten4.api.transmission.ConnectionType.PULL) continue;
        BlockPos t = pos.relative(d);
        if (positionedTransmitters.containsKey(t)) continue;
        if (level.getBlockEntity(t) instanceof ITransmitterProvider) continue;
        Edge edge = new Edge(t, d.getOpposite());
        if (!pullSeen.add(edge)) continue;
        IFluidHandler src = level.getCapability(Capabilities.FluidHandler.BLOCK, t, d.getOpposite());
        if (src != null) producers.add(src);
      }
    }
    // Fair pull: each round divides remaining space evenly among producers
    long space = capacity - buffer.getAmount();
    if (space > 0 && !producers.isEmpty()) {
      long toPull = Math.min(space, capacity);
      int totalPulled = 0;
      List<IFluidHandler> srcs = new ArrayList<>(producers);
      while (!srcs.isEmpty() && toPull > 0) {
        int share = (int) Math.min(toPull / srcs.size(), Integer.MAX_VALUE);
        if (share == 0) share = (int) toPull;
        var it = srcs.iterator();
        while (it.hasNext()) {
          IFluidHandler src = it.next();
          FluidStack drained = src.drain(share, IFluidHandler.FluidAction.EXECUTE);
          if (!drained.isEmpty() && (buffer.isEmpty() || FluidStack.isSameFluidSameComponents(buffer, drained))) {
            if (buffer.isEmpty()) buffer = drained.copy();
            else buffer.grow(drained.getAmount());
            totalPulled += drained.getAmount();
            toPull -= drained.getAmount();
          }
          if (drained.getAmount() < share) it.remove(); // depleted
        }
      }
    }

    if (buffer.isEmpty()) { prevTransferAmount = 0; return; }

    // 1. PUSH to consumers
    Set<Edge> pushSeen = new HashSet<>();
    List<IFluidHandler> acceptors = new ArrayList<>();

    for (var e : positionedTransmitters.entrySet()) {
      BlockPos pos = e.getKey();
      FluidTransmitter cable = e.getValue();
      if (level == null) level = cable.getLevel();
      if (level == null) continue;
      for (Direction d : Direction.values()) {
        BlockPos t = pos.relative(d);
        if (positionedTransmitters.containsKey(t)) continue;
        if (level.getBlockEntity(t) instanceof ITransmitterProvider) continue; // skip cables from other networks
        if (!cable.getConnectionTypeRaw(d).canSendTo()) continue;
        Edge edge = new Edge(t, d.getOpposite());
        if (!pushSeen.add(edge)) continue;
        IFluidHandler cap = level.getCapability(Capabilities.FluidHandler.BLOCK, t, d.getOpposite());
        if (cap != null && cap.isFluidValid(0, buffer)) acceptors.add(cap);
      }
    }
    // Fair round-robin: each round divides remaining evenly among still-needy acceptors
    int toSend = buffer.getAmount();
    int totalSent = 0;
    List<IFluidHandler> needy = new ArrayList<>(acceptors);
    while (!needy.isEmpty() && toSend > 0) {
      int share = toSend / needy.size();
      if (share == 0) share = toSend;
      var it = needy.iterator();
      while (it.hasNext()) {
        IFluidHandler a = it.next();
        int filled = a.fill(buffer.copyWithAmount(share), IFluidHandler.FluidAction.EXECUTE);
        totalSent += filled;
        toSend -= filled;
        if (filled < share) it.remove(); // saturated
      }
    }
    if (totalSent > 0) buffer.shrink(totalSent);
    prevTransferAmount = totalSent;
  }

  // ---- Merge ----
  @Override protected List<FluidTransmitter> adoptFrom(FluidNetwork other) {
    float oldScale = currentScale;
    long oldCap = getCapacity();
    List<FluidTransmitter> list = super.adoptFrom(other);
    long cap = getCapacity();
    currentScale = cap == 0 ? 0 : Math.min(1, (currentScale * oldCap + other.currentScale * other.capacity) / cap);

    if (!other.buffer.isEmpty()) {
      if (buffer.isEmpty()) {
        buffer = other.buffer.copy();
      } else if (FluidStack.isSameFluidSameComponents(buffer, other.buffer)) {
        buffer.grow(other.buffer.getAmount());
      }
      other.buffer = FluidStack.EMPTY;
    }
    return list;
  }

  public boolean isCompatibleWith(FluidNetwork other) {
    return buffer.isEmpty() || other.buffer.isEmpty() ||
        FluidStack.isSameFluidSameComponents(buffer, other.buffer);
  }

  /** External capability: push fluid INTO the network. */
  public int receiveFluid(FluidStack stack, IFluidHandler.FluidAction action) {
    if (buffer.isEmpty() || FluidStack.isSameFluidSameComponents(buffer, stack)) {
      int space = (int) Math.min(Integer.MAX_VALUE, capacity - buffer.getAmount());
      int toAdd = Math.min(stack.getAmount(), space);
      if (toAdd > 0 && action.execute()) {
        if (buffer.isEmpty()) buffer = stack.copyWithAmount(toAdd);
        else buffer.grow(toAdd);
      }
      return toAdd;
    }
    return 0;
  }

  public int getPrevTransferAmount() { return prevTransferAmount; }
}
