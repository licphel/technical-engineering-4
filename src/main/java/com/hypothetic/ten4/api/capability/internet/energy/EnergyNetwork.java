package com.hypothetic.ten4.api.capability.internet.energy;

import com.hypothetic.ten4.api.capability.internet.ConnectionType;
import com.hypothetic.ten4.api.capability.internet.DynamicNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Energy network: distributes energy stored in per-cable buffers.
 * No shared buffer — each cable owns its energy. Merge/split is lossless.
 */
public class EnergyNetwork extends DynamicNetwork<IEnergyStorage, EnergyNetwork, EnergyTransmitter> {
  public EnergyNetwork(UUID id) {
    super(id);
  }

  public EnergyNetwork(Collection<EnergyNetwork> nets) {
    super(UUID.randomUUID());
    adoptAllAndRegister(nets);
  }

  private long totalBuffer() {
    long sum = 0;
    for (EnergyTransmitter t : getTransmitters()) {
      sum += t.getBuffer();
    }
    return sum;
  }

  private long totalCapacity() {
    long sum = 0;
    for (EnergyTransmitter t : getTransmitters()) {
      sum += t.getCapacity();
    }
    return sum;
  }

  @Override
  public void onUpdate() {
    if (positionedTransmitters.isEmpty()) {
      return;
    }

    record Edge(BlockPos pos, Direction side) {
    }
    List<Edge> consumers = new ArrayList<>(), producers = new ArrayList<>();
    Level level = null;

    for (var e : positionedTransmitters.entrySet()) {
      BlockPos pos = e.getKey();
      EnergyTransmitter cable = e.getValue();
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
        ConnectionType ct = cable.getConnectionTypeRaw(d);
        IEnergyStorage cap = level.getCapability(Capabilities.EnergyStorage.BLOCK, t, d.getOpposite());
        if (cap == null) {
          continue;
        }
        if (cap.canReceive() && ct.canSendTo()) {
          consumers.add(new Edge(t, d.getOpposite()));
        }
        if (cap.canExtract() && ct.canAccept()) {
          producers.add(new Edge(t, d.getOpposite()));
        }
      }
    }
    if (level == null) {
      return;
    }

    for (EnergyTransmitter cable : sortedByBuffer(true)) {
      if (cable.getBuffer() <= 0) {
        break;
      }
      long remaining = cable.getBuffer();
      for (Edge c : consumers) {
        if (remaining <= 0) {
          break;
        }
        IEnergyStorage cap = level.getCapability(Capabilities.EnergyStorage.BLOCK, c.pos, c.side);
        if (cap == null || !cap.canReceive()) {
          continue;
        }
        int toSend = (int) Math.min(remaining, Integer.MAX_VALUE);
        int accepted = cap.receiveEnergy(toSend, false);
        remaining -= accepted;
      }
      cable.setBuffer(remaining);
    }

    for (EnergyTransmitter cable : sortedByBuffer(false)) {
      long space = cable.getCapacity() - cable.getBuffer();
      if (space <= 0) {
        break;
      }
      long remaining = space;
      for (Edge p : producers) {
        if (remaining <= 0) {
          break;
        }
        IEnergyStorage cap = level.getCapability(Capabilities.EnergyStorage.BLOCK, p.pos, p.side);
        if (cap == null || !cap.canExtract()) {
          continue;
        }
        int toPull = (int) Math.min(remaining, Integer.MAX_VALUE);
        int extracted = cap.extractEnergy(toPull, false);
        remaining -= extracted;
      }
      cable.setBuffer(cable.getCapacity() - remaining);
    }
  }

  private List<EnergyTransmitter> sortedByBuffer(boolean descending) {
    List<EnergyTransmitter> list = new ArrayList<>(getTransmitters());
    list.sort((a, b) -> descending ? Long.compare(b.getBuffer(), a.getBuffer()) : Long.compare(a.getBuffer(), b.getBuffer()));
    return list;
  }

  public IEnergyStorage asStorage() {
    return new IEnergyStorage() {
      @Override
      public int receiveEnergy(int max, boolean sim) {
        long remaining = max;
        for (EnergyTransmitter t : getTransmitters()) {
          long space = t.getCapacity() - t.getBuffer();
          if (space <= 0) {
            continue;
          }
          int v = (int) Math.min(space, remaining);
          if (!sim) {
            t.setBuffer(t.getBuffer() + v);
          }
          remaining -= v;
          if (remaining <= 0) {
            break;
          }
        }
        return (int) (max - remaining);
      }

      @Override
      public int extractEnergy(int max, boolean sim) {
        long remaining = max;
        for (EnergyTransmitter t : getTransmitters()) {
          if (t.getBuffer() <= 0) {
            continue;
          }
          int v = (int) Math.min(t.getBuffer(), remaining);
          if (!sim) {
            t.setBuffer(t.getBuffer() - v);
          }
          remaining -= v;
          if (remaining <= 0) {
            break;
          }
        }
        return (int) (max - remaining);
      }

      @Override
      public int getEnergyStored() {
        return (int) Math.min(totalBuffer(), Integer.MAX_VALUE);
      }

      @Override
      public int getMaxEnergyStored() {
        return (int) Math.min(totalCapacity(), Integer.MAX_VALUE);
      }

      @Override
      public boolean canExtract() {
        return totalBuffer() > 0;
      }

      @Override
      public boolean canReceive() {
        return totalBuffer() < totalCapacity();
      }
    };
  }
}
