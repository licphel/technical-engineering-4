package com.hypothetic.ten4.lib.capability.internet;

import com.hypothetic.ten4.lib.blockentity.internet.EnergyCableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Global registry of all transmitter networks.
 * Handles network creation, merging, orphan resolution, and ticking.
 * Pattern: ported from Mekanism's TransmitterNetworkRegistry, simplified.
 */
public class TransmitterNetworkRegistry {

  private static final Set<DynamicNetwork<?, ?, ?>> networks = new HashSet<>();
  private static final Map<UUID, DynamicNetwork<?, ?, ?>> clientNetworks = new HashMap<>();
  private static final Map<GlobalPos, Transmitter<?, ?, ?>> orphans = new HashMap<>();
  private static final Set<Transmitter<?, ?, ?>> invalidTransmitters = new HashSet<>();
  private static final Set<DynamicNetwork<?, ?, ?>> networksToCommit = new HashSet<>();

  // --- Client network tracking ---

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

  // --- Server network tracking ---

  public static void registerNetwork(DynamicNetwork<?, ?, ?> net) { networks.add(net); }
  public static void removeNetwork(DynamicNetwork<?, ?, ?> net) {
    networks.remove(net);
    networksToCommit.remove(net);
  }

  // --- Orphan & invalid management ---

  public static void registerOrphan(Transmitter<?, ?, ?> t) {
    if (!invalidTransmitters.remove(t)) {
      orphans.put(GlobalPos.of(t.getLevel().dimension(), t.getBlockPos()), t);
    }
  }

  public static void invalidateTransmitter(Transmitter<?, ?, ?> t) {
    invalidTransmitters.add(t);
    orphans.remove(GlobalPos.of(t.getLevel().dimension(), t.getBlockPos()));
  }

  private static long lastTick = -1;

  // --- Tick (call from a server tick handler) ---

  public static void onServerTick() {
    // Only run once per tick regardless of how many cables call it
    long now = System.currentTimeMillis() / 50; // approximate tick counter
    if (now == lastTick) return;
    lastTick = now;

    removeInvalidTransmitters();
    assignOrphans();
    commitNetworks();
    for (DynamicNetwork<?, ?, ?> net : networks) {
      net.onUpdate();
    }
  }

  private static void removeInvalidTransmitters() {
    if (invalidTransmitters.isEmpty()) return;
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
        if (!t.isValid()) t.setNetwork(null, false);
      }
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static void assignOrphans() {
    if (orphans.isEmpty()) return;
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
    if (networksToCommit.isEmpty()) return;
    Set<DynamicNetwork<?, ?, ?>> toCommit = new HashSet<>(networksToCommit);
    networksToCommit.clear();
    for (DynamicNetwork<?, ?, ?> net : toCommit) {
      net.commit();
    }
  }

  // --- OrphanPathFinder ---

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static class OrphanFinder<ACCEPTOR, NETWORK extends DynamicNetwork<ACCEPTOR, NETWORK, TRANSMITTER>,
                                    TRANSMITTER extends Transmitter<ACCEPTOR, NETWORK, TRANSMITTER>> {

    private final TRANSMITTER start;
    private final Level world;
    private final Set<TRANSMITTER> connected = new HashSet<>();
    private final Set<NETWORK> foundNetworks = new HashSet<>();
    private final Set<BlockPos> visited = new HashSet<>();
    private final Deque<BlockPos> queue = new ArrayDeque<>();

    OrphanFinder(Transmitter<ACCEPTOR, NETWORK, TRANSMITTER> start) {
      this.start = (TRANSMITTER) start;
      this.world = start.getLevel();
    }

    NETWORK findNetwork(Map<GlobalPos, Transmitter<?, ?, ?>> orphanMap) {
      queue.add(start.getBlockPos());
      while (!queue.isEmpty()) {
        iterate(orphanMap, queue.pollFirst());
      }

      NETWORK network;
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
      if (!visited.add(pos)) return;
      GlobalPos gp = GlobalPos.of(world.dimension(), pos);
      Transmitter<?, ?, ?> found = orphanMap.get(gp);

      if (found != null) {
        if (found.isValid() && found.isOrphan() && start.supportsTransmission(found)) {
          connected.add((TRANSMITTER) found);
          found.setOrphan(false);
          for (Direction d : Direction.values()) {
            BlockPos next = pos.relative(d);
            if (!visited.contains(next)) {
              BlockEntity be = world.getBlockEntity(next);
              if (be instanceof EnergyCableBlockEntity cable
                  && start.supportsTransmission(cable.transmitter)) {
                queue.addLast(next);
              }
            }
          }
        }
      } else {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof EnergyCableBlockEntity cable) {
          NETWORK net = (NETWORK) cable.transmitter.getNetwork();
          if (net != null) foundNetworks.add(net);
        }
      }
    }
  }

  // --- Debug ---

  public static int networkCount() { return networks.size(); }
  public static int orphanCount() { return orphans.size(); }
}
