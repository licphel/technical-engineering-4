package com.hypothetic.ten4.api.capability.internet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TransmitterNetworkRegistry {
  private static final Set<DynamicNetwork<?, ?, ?>> networks = new HashSet<>();
  private static final Map<UUID, DynamicNetwork<?, ?, ?>> clientNetworks = new HashMap<>();
  private static final Map<GlobalPos, Transmitter<?, ?, ?>> orphans = new HashMap<>();
  private static final Set<Transmitter<?, ?, ?>> invalidTransmitters = new HashSet<>();
  private static final Set<DynamicNetwork<?, ?, ?>> networksToCommit = new HashSet<>();

  private static long lastGameTime = -1;
  private static int pruneCounter;

  public static void addClientNetwork(UUID id, DynamicNetwork<?, ?, ?> net) {
    clientNetworks.putIfAbsent(id, net);
  }

  @Nullable
  public static DynamicNetwork<?, ?, ?> getClientNetwork(UUID id) {
    return clientNetworks.get(id);
  }

  public static void removeClientNetwork(DynamicNetwork<?, ?, ?> net) {
    clientNetworks.remove(net.getUUID());
  }

  public static void registerNetwork(DynamicNetwork<?, ?, ?> net) {
    networks.add(net);
  }

  public static void removeNetwork(DynamicNetwork<?, ?, ?> net) {
    networks.remove(net);
    networksToCommit.remove(net);
  }

  public static void registerOrphan(Transmitter<?, ?, ?> t) {
    if (!invalidTransmitters.remove(t)) {
      Level level = t.getLevel();
      if (level != null) {
        orphans.put(GlobalPos.of(level.dimension(), t.getBlockPos()), t);
      }
    }
  }

  public static void invalidateTransmitter(Transmitter<?, ?, ?> t) {
    invalidTransmitters.add(t);
    Level level = t.getLevel();
    if (level != null) {
      orphans.remove(GlobalPos.of(level.dimension(), t.getBlockPos()));
    }
  }

  public static void onServerTick(Level level) {
    long gt = level.getGameTime();
    if (gt == lastGameTime) {
      return;
    }
    lastGameTime = gt;

    removeInvalidTransmitters();
    assignOrphans();
    commitNetworks();
    for (DynamicNetwork<?, ?, ?> net : networks) {
      net.onUpdate();
    }

    // Periodic empty-network cleanup (every 100 ticks)
    if (++pruneCounter >= 100) {
      pruneCounter = 0;
      networks.removeIf(DynamicNetwork::isEmpty);
    }
  }

  private static void removeInvalidTransmitters() {
    if (invalidTransmitters.isEmpty()) {
      return;
    }
    Set<Transmitter<?, ?, ?>> toInvalidate = new HashSet<>(invalidTransmitters);
    invalidTransmitters.clear();
    for (Transmitter<?, ?, ?> t : toInvalidate) {
      removeInvalidTransmitter(t);
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static void removeInvalidTransmitter(Transmitter<?, ?, ?> t) {
    if (!t.isOrphan() || !t.isValid()) {
      DynamicNetwork net = t.getNetwork();
      if (net != null) {
        net.invalidate(t);
        if (!t.isValid()) {
          t.setNetwork(null, false);
        }
      }
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static void assignOrphans() {
    if (orphans.isEmpty()) {
      return;
    }
    Map<GlobalPos, Transmitter<?, ?, ?>> current = new HashMap<>(orphans);
    orphans.clear();

    for (Transmitter<?, ?, ?> orphan : current.values()) {
      if (orphan.isValid() && orphan.isOrphan()) {
        OrphanFinder finder = new OrphanFinder(orphan);
        networksToCommit.add(finder.findNetwork(current));
      }
    }
  }

  private static void commitNetworks() {
    if (networksToCommit.isEmpty()) {
      return;
    }
    Set<DynamicNetwork<?, ?, ?>> toCommit = new HashSet<>(networksToCommit);
    networksToCommit.clear();
    for (DynamicNetwork<?, ?, ?> net : toCommit) {
      net.commit();
    }
  }

  public static int networkCount() {
    return networks.size();
  }

  public static int orphanCount() {
    return orphans.size();
  }

  @SuppressWarnings("unchecked")
  private static class OrphanFinder<AC, NET extends DynamicNetwork<AC, NET, T>, T extends Transmitter<AC, NET, T>> {
    private final T start;
    private final Level world;
    private final Set<T> connected = new HashSet<>();
    private final Set<NET> foundNetworks = new HashSet<>();
    private final Set<BlockPos> visited = new HashSet<>();
    private final Deque<BlockPos> queue = new ArrayDeque<>();

    OrphanFinder(Transmitter<AC, NET, T> start) {
      this.start = (T) start;
      this.world = start.getLevel();
    }

    NET findNetwork(Map<GlobalPos, Transmitter<?, ?, ?>> orphanMap) {
      queue.add(start.getBlockPos());
      while (!queue.isEmpty()) {
        iterate(orphanMap, queue.pollFirst());
      }

      NET network;
      if (foundNetworks.size() == 1) {
        network = foundNetworks.iterator().next();
      } else {
        network = start.createNetworkByMerging(foundNetworks);
      }
      network.addNewTransmitters(connected);
      return network;
    }

    @SuppressWarnings("unchecked")
    private void iterate(Map<GlobalPos, Transmitter<?, ?, ?>> orphanMap, BlockPos pos) {
      if (!visited.add(pos)) {
        return;
      }
      GlobalPos gp = GlobalPos.of(world.dimension(), pos);
      Transmitter<?, ?, ?> found = orphanMap.get(gp);

      if (found != null) {
        if (found.isValid() && found.isOrphan()
            && start.supportsTransmission(found) && start.isColorCompatible(found)) {
          connected.add((T) found);
          found.setOrphan(false);
          for (Direction d : Direction.values()) {
            BlockPos next = pos.relative(d);
            if (!visited.contains(next)) {
              BlockEntity be = world.getBlockEntity(next);
              if (be instanceof ITransmitterProvider tb) {
                Transmitter<?, ?, ?> t = tb.getTransmitter();
                if (t != null && start.supportsTransmission(t) && start.isColorCompatible(t)) {
                  queue.addLast(next);
                }
              }
            }
          }
        }
      } else {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof ITransmitterProvider tb) {
          Transmitter<?, ?, ?> t = tb.getTransmitter();
          if (t != null) {
            NET net = (NET) t.getNetwork();
            if (net != null) {
              foundNetworks.add(net);
            }
          }
        }
      }
    }
  }
}
