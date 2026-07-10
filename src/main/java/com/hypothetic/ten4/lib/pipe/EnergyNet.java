package com.hypothetic.ten4.lib.pipe;

import com.hypothetic.ten4.lib.capability.energy.DirectionalEnergyStorage;
import com.hypothetic.ten4.lib.capability.energy.IDirectionalEnergyProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnergyNet implements IEnergyStorage {
  private static final Map<Level, NetworkManager<IEnergyStorage>> MANAGERS = new HashMap<>();
  private static final Map<Level, Map<BlockPos, EnergyNet>> NETS = new HashMap<>();

  private final BlockPos rootPos;
  private final IDirectionalEnergyProvider host;
  private final NetworkManager<IEnergyStorage> graph;
  private final List<Slot> consumers = new ArrayList<>();
  private final List<Slot> producers = new ArrayList<>();
  private Level lastLevel;

  private EnergyNet(BlockPos rootPos, IDirectionalEnergyProvider host, NetworkManager<IEnergyStorage> graph) {
    this.rootPos = rootPos;
    this.host = host;
    this.graph = graph;
  }

  private static NetworkManager<IEnergyStorage> manager(Level level) {
    return MANAGERS.computeIfAbsent(level, l -> new NetworkManager<>(be -> be instanceof IEnergyNetNode, (lvl, p, side) -> lvl.getCapability(Capabilities.EnergyStorage.BLOCK, p, side)));
  }

  public static void add(Level level, BlockPos pos) {
    NetworkManager<IEnergyStorage> mgr = manager(level);
    mgr.onNodeAdded(level, pos);
    NETS.computeIfAbsent(level, k -> new HashMap<>()).remove(pos);
    Network<IEnergyStorage> net = mgr.getNetwork(pos);
    if (net != null) {
      NETS.get(level).remove(net.nodes().iterator().next());
    }
  }

  public static void remove(Level level, BlockPos pos) {
    NetworkManager<IEnergyStorage> mgr = manager(level);
    mgr.onNodeRemoved(level, pos);
    Map<BlockPos, EnergyNet> map = NETS.get(level);
    if (map != null) {
      map.remove(pos);
    }
  }

  public static @Nullable EnergyNet of(Level level, BlockPos pos, IDirectionalEnergyProvider host) {
    Network<IEnergyStorage> net = manager(level).getNetwork(pos);
    if (net == null || net.nodes().isEmpty()) {
      return null;
    }
    BlockPos root = net.nodes().iterator().next();
    if (!pos.equals(root)) {
      return null;
    }

    Map<BlockPos, EnergyNet> map = NETS.computeIfAbsent(level, k -> new HashMap<>());
    EnergyNet cached = map.get(root);
    if (cached != null && cached.host == host) {
      return cached;
    }

    EnergyNet en = new EnergyNet(root, host, manager(level));
    en.rescan(level);
    map.put(root, en);
    return en;
  }

  public static IEnergyStorage sideAccess(Level level, BlockPos pos, Direction side, IDirectionalEnergyProvider host) {
    EnergyNet net = of(level, pos, host);
    return net != null ? net : new DirectionalEnergyStorage(host, side);
  }

  public void tick() {
    if (consumers.isEmpty() && producers.isEmpty()) {
      return;
    }

    for (Slot c : consumers) {
      if (host.getEnergy() <= 0) {
        break;
      }
      IEnergyStorage fresh = lastLevel.getCapability(Capabilities.EnergyStorage.BLOCK, c.target, c.side);
      if (fresh == null || !fresh.canReceive()) {
        continue;
      }
      int v = Math.min(host.getEnergy(), host.getMaxEnergyExtract(c.side));
      if (v <= 0) {
        continue;
      }
      host.setEnergy(host.getEnergy() - v);
      fresh.receiveEnergy(v, false);
    }
    for (Slot p : producers) {
      if (host.getEnergy() >= host.getMaxEnergy()) {
        break;
      }
      IEnergyStorage fresh = lastLevel.getCapability(Capabilities.EnergyStorage.BLOCK, p.target, p.side);
      if (fresh == null || !fresh.canExtract()) {
        continue;
      }
      int v = Math.min(host.getMaxEnergy() - host.getEnergy(), host.getMaxEnergyReceive(p.side));
      if (v <= 0) {
        continue;
      }
      v = fresh.extractEnergy(v, false);
      if (v > 0) {
        host.setEnergy(host.getEnergy() + v);
      }
    }
  }

  @Override
  public int receiveEnergy(int max, boolean sim) {
    int v = Math.min(host.getMaxEnergy() - host.getEnergy(), max);
    if (!sim) {
      host.setEnergy(host.getEnergy() + v);
    }
    return v;
  }

  @Override
  public int extractEnergy(int max, boolean sim) {
    int v = Math.min(host.getEnergy(), max);
    if (!sim) {
      host.setEnergy(host.getEnergy() - v);
    }
    return v;
  }

  @Override
  public int getEnergyStored() {
    return host.getEnergy();
  }

  @Override
  public int getMaxEnergyStored() {
    return host.getMaxEnergy();
  }

  @Override
  public boolean canExtract() {
    return true;
  }

  @Override
  public boolean canReceive() {
    return true;
  }

  private void rescan(Level level) {
    lastLevel = level;
    consumers.clear();
    producers.clear();
    Network<IEnergyStorage> net = graph.getNetwork(rootPos);
    if (net == null) {
      return;
    }
    for (BlockPos node : net.nodes()) {
      for (Direction d : Direction.values()) {
        BlockPos target = node.relative(d);
        if (level.getBlockEntity(target) instanceof IEnergyNetNode) {
          continue;
        }
        IEnergyStorage cap = level.getCapability(Capabilities.EnergyStorage.BLOCK, target, d.getOpposite());
        if (cap == null) {
          continue;
        }
        if (cap.canReceive()) {
          consumers.add(new Slot(node, d, target, cap));
        }
        if (cap.canExtract()) {
          producers.add(new Slot(node, d, target, cap));
        }
      }
    }
  }

  private record Slot(BlockPos pos, Direction side, BlockPos target, IEnergyStorage cap) {
  }
}
