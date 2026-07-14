package com.hypothetic.ten4.api.transmission.fluid;

import com.hypothetic.ten4.api.transmission.BufferedNetwork;
import com.hypothetic.ten4.api.transmission.ConnectionType;
import com.hypothetic.ten4.api.transmission.ITransmitterProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.*;

public class FluidNetwork extends BufferedNetwork<IFluidHandler, FluidNetwork, FluidStack, FluidTransmitter> {
  private FluidStack buffer = FluidStack.EMPTY;
  private int prevTransferAmount;

  public FluidNetwork(UUID id) {
    super(id);
  }

  public FluidNetwork(Collection<FluidNetwork> nets) {
    super(UUID.randomUUID());
    adoptAllAndRegister(nets);
  }

  @Override
  protected float computeContentScale() {
    float scale = capacity > 0 ? (float) buffer.getAmount() / capacity : 0;
    float ret = Math.max(currentScale, scale);
    if (prevTransferAmount > 0 && ret < 1) {
      ret = Math.min(1, ret + 0.02F);
    } else if (prevTransferAmount <= 0 && ret > 0) {
      ret = Math.max(scale, ret - 0.02F);
    }
    return ret;
  }

  @Override
  protected void forceScaleUpdate() {
    currentScale = capacity > 0 ? (float) buffer.getAmount() / capacity : 0;
  }

  @Override
  public FluidStack getBuffer() {
    return buffer.copy();
  }

  @Override
  public void absorbBuffer(FluidTransmitter t) {
    FluidStack share = t.releaseShare();
    if (!share.isEmpty()) {
      if (buffer.isEmpty()) {
        buffer = share.copy();
      } else if (FluidStack.isSameFluidSameComponents(buffer, share)) {
        buffer.setAmount(buffer.getAmount() + share.getAmount());
      }
    }
  }

  @Override
  public void clampBuffer() {
    if (!buffer.isEmpty() && buffer.getAmount() > capacity) {
      buffer.setAmount((int) capacity);
    }
  }

  @Override
  protected List<FluidTransmitter> adoptFrom(FluidNetwork other) {
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

  @Override
  public void onUpdate() {
    super.onUpdate();
    long throughput = netThroughput();
    if (throughput <= 0) {
      return;
    }
    Level level = null;

    // 0. PULL
    record Edge(BlockPos pos, Direction side) {
    }
    Set<Edge> pullSeen = new HashSet<>();
    List<IFluidHandler> producers = new ArrayList<>();
    for (var e : positionedTransmitters.entrySet()) {
      BlockPos pos = e.getKey();
      FluidTransmitter tr = e.getValue();
      if (level == null) {
        level = tr.getLevel();
      }
      if (level == null) {
        continue;
      }
      for (Direction d : Direction.values()) {
        if (tr.getConnectionTypeRaw(d) != ConnectionType.PULL) {
          continue;
        }
        BlockPos t = pos.relative(d);
        if (positionedTransmitters.containsKey(t)) {
          continue;
        }
        if (level.getBlockEntity(t) instanceof ITransmitterProvider) {
          continue;
        }
        Edge edge = new Edge(t, d.getOpposite());
        if (!pullSeen.add(edge)) {
          continue;
        }
        IFluidHandler src = level.getCapability(Capabilities.FluidHandler.BLOCK, t, d.getOpposite());
        if (src != null) {
          producers.add(src);
        }
      }
    }

    long space = Math.min(capacity - buffer.getAmount(), throughput);
    if (space > 0 && !producers.isEmpty()) {
      long toPull = space;
      int totalPulled = 0;
      List<IFluidHandler> srcs = new ArrayList<>(producers);
      while (!srcs.isEmpty() && toPull > 0) {
        int share = (int) Math.min(toPull / srcs.size(), Integer.MAX_VALUE);
        if (share == 0) {
          share = (int) toPull;
        }
        var it = srcs.iterator();
        while (it.hasNext()) {
          IFluidHandler src = it.next();
          FluidStack sim = src.drain(share, IFluidHandler.FluidAction.SIMULATE);
          if (!sim.isEmpty() && (buffer.isEmpty() || FluidStack.isSameFluidSameComponents(buffer, sim))) {
            FluidStack drained = src.drain(sim.getAmount(), IFluidHandler.FluidAction.EXECUTE);
            if (buffer.isEmpty()) {
              buffer = drained.copy();
            } else {
              buffer.grow(drained.getAmount());
            }
            totalPulled += drained.getAmount();
            toPull -= drained.getAmount();
          }
          if (sim.getAmount() < share) {
            it.remove();
          }
        }
      }
    }

    if (buffer.isEmpty()) {
      prevTransferAmount = 0;
      return;
    }

    // 1. PUSH
    Set<Edge> pushSeen = new HashSet<>();
    List<IFluidHandler> acceptors = new ArrayList<>();
    for (var e : positionedTransmitters.entrySet()) {
      BlockPos pos = e.getKey();
      FluidTransmitter tr = e.getValue();
      if (level == null) {
        level = tr.getLevel();
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
        if (!tr.getConnectionTypeRaw(d).canBorrow()) {
          continue;
        }
        Edge edge = new Edge(t, d.getOpposite());
        if (!pushSeen.add(edge)) {
          continue;
        }
        IFluidHandler cap = level.getCapability(Capabilities.FluidHandler.BLOCK, t, d.getOpposite());
        if (cap != null && cap.isFluidValid(0, buffer)) {
          acceptors.add(cap);
        }
      }
    }

    int toSend = (int) Math.min(buffer.getAmount(), throughput);
    int totalSent = 0;
    List<IFluidHandler> needy = new ArrayList<>(acceptors);
    while (!needy.isEmpty() && toSend > 0) {
      int share = toSend / needy.size();
      if (share == 0) {
        share = toSend;
      }
      var it = needy.iterator();
      while (it.hasNext()) {
        IFluidHandler a = it.next();
        int filled = a.fill(buffer.copyWithAmount(share), IFluidHandler.FluidAction.EXECUTE);
        totalSent += filled;
        toSend -= filled;
        if (filled < share) {
          it.remove();
        }
      }
    }
    if (totalSent > 0) {
      buffer.shrink(totalSent);
    }
    prevTransferAmount = totalSent;
  }

  public FluidStack getFluid() {
    return buffer;
  }

  public boolean isCompatibleWith(FluidNetwork other) {
    return buffer.isEmpty() || other.buffer.isEmpty() ||
        FluidStack.isSameFluidSameComponents(buffer, other.buffer);
  }

  public int receiveFluid(FluidStack stack, IFluidHandler.FluidAction action) {
    if (buffer.isEmpty() || FluidStack.isSameFluidSameComponents(buffer, stack)) {
      int space = (int) Math.min(Integer.MAX_VALUE, capacity - buffer.getAmount());
      int toAdd = Math.min(stack.getAmount(), space);
      if (toAdd > 0 && action.execute()) {
        if (buffer.isEmpty()) {
          buffer = stack.copyWithAmount(toAdd);
        } else {
          buffer.grow(toAdd);
        }
      }
      return toAdd;
    }
    return 0;
  }

  public int getPrevTransferAmount() {
    return prevTransferAmount;
  }
}
