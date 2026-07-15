package com.hypothetic.ten4.api.transmission;

import com.hypothetic.ten4.Ten4;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@EventBusSubscriber(modid = Ten4.ID)
public final class TransmitterNetworkRegistry {
  private static final Set<Network<?, ?, ?>> networks = new HashSet<>();
  private static final Map<UUID, Network<?, ?, ?>> clientNetworks = new HashMap<>();
  private static final Set<Transmitter<?, ?, ?>> pendingJoins = new HashSet<>();
  private static final Set<Transmitter<?, ?, ?>> pendingRemovals = new HashSet<>();
  private static int pruneCounter;

  private TransmitterNetworkRegistry() {
  }

  @SubscribeEvent
  public static void onLevelTick(LevelTickEvent.Post e) {
    Level level = e.getLevel();
    if (level.isClientSide()) {
      return;
    }

    // Process pending removals first (deferred from setRemoved/setPlacedBy/paint)
    processPendingRemovals();
    // Then process pending joins (deferred from onLoad/onPlace/paint/removals)
    processPendingJoins();
    // Network update
    for (Network<?, ?, ?> net : networks) {
      net.onUpdate();
    }
    // Prune
    if (++pruneCounter >= 100) {
      pruneCounter = 0;
      networks.removeIf(Network::isEmpty);
    }
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private static void processPendingJoins() {
    if (pendingJoins.isEmpty()) {
      return;
    }
    Set<Transmitter<?, ?, ?>> batch = new HashSet<>(pendingJoins);
    pendingJoins.clear();

    for (Transmitter start : batch) {
      if (!start.isValid() || start.isRemote()) {
        continue;
      }
      Level world = start.getLevel();
      if (world == null) {
        continue;
      }

      CompatibleTransmitterValidator validator = start.getNewOrphanValidator();
      Set<Transmitter> connected = new HashSet<>();
      Set<Network> foundNetworks = new HashSet<>();
      Set<BlockPos> visited = new HashSet<>();
      Deque<BlockPos> queue = new ArrayDeque<>();

      queue.add(start.getBlockPos());
      while (!queue.isEmpty()) {
        BlockPos pos = queue.pollFirst();
        if (!visited.add(pos)) {
          continue;
        }
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof ITransmitterProvider tb)) {
          continue;
        }
        Transmitter t = tb.getTransmitter();
        if (!t.isValid() || !start.supportsTransmission(t)) {
          continue;
        }

        Network net = t.getNetwork();
        if (net != null) {
          if (validator.isNetworkCompatible(net)) {
            foundNetworks.add(net);
          } else {
            destroyFluidConflict(world, pos);
          }
          continue;
        }

        if (!validator.isTransmitterCompatible(t)) {
          destroyFluidConflict(world, pos);
          continue;
        }
        connected.add(t);

        for (Direction d : Direction.values()) {
          BlockPos next = pos.relative(d);
          if (!visited.contains(next)) {
            BlockEntity neighborBe = world.getBlockEntity(next);
            if (neighborBe instanceof ITransmitterProvider ntb) {
              Transmitter<?, ?, ?> neighbor = ntb.getTransmitter();
              if (t.isValidTransmitterBasic(ntb, d)) {
                queue.addLast(next);
              }
            }
          }
        }
      }

      Network network;
      if (foundNetworks.isEmpty()) {
        network = start.createEmptyNetwork(UUID.randomUUID());
        network.register();
      } else if (foundNetworks.size() == 1) {
        network = foundNetworks.iterator().next();
      } else {
        network = start.createNetworkByMerging(foundNetworks);
      }

      for (Transmitter t : connected) {
        Network oldNet = t.getNetwork();
        if (oldNet != null && oldNet != network) {
          oldNet.removeTransmitter(t);
          t.setNetwork(null, false);
        }
      }

      network.addNewTransmitters(connected, validator);
      network.commit();
    }
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private static void processPendingRemovals() {
    if (pendingRemovals.isEmpty()) {
      return;
    }
    Set<Transmitter<?, ?, ?>> batch = new HashSet<>(pendingRemovals);
    pendingRemovals.clear();

    for (Transmitter removed : batch) {
      if (removed.isRemote()) {
        continue;
      }
      Level world = removed.getLevel();
      if (world == null) {
        continue;
      }

      Network oldNet = removed.getNetwork();
      if (oldNet != null) {
        for (Object o : new ArrayList<>(oldNet.getTransmitters())) {
          Transmitter t = (Transmitter) o;
          t.takeShare();
          t.setNetwork(null, false);
        }
        oldNet.deregister();
      }
      removed.setNetwork(null, false);

      // Queue neighbors for re-join
      for (Direction d : Direction.values()) {
        BlockPos neighborPos = removed.getBlockPos().relative(d);
        BlockEntity be = world.getBlockEntity(neighborPos);
        if (be instanceof ITransmitterProvider tb && removed.isValidTransmitterBasic(tb, d)) {
          Transmitter t = tb.getTransmitter();
          if (t.isValid()) {
            pendingJoins.add(t);
          }
        }
      }
    }
  }

  public static void addClientNetwork(UUID id, Network<?, ?, ?> net) {
    clientNetworks.putIfAbsent(id, net);
  }

  public static @Nullable Network<?, ?, ?> getClientNetwork(UUID id) {
    return clientNetworks.getOrDefault(id, null);
  }

  public static void removeClientNetwork(Network<?, ?, ?> net) {
    clientNetworks.remove(net.getUUID());
  }

  public static void registerNetwork(Network<?, ?, ?> net) {
    networks.add(net);
  }

  public static void removeNetwork(Network<?, ?, ?> net) {
    networks.remove(net);
  }

  public static void join(Transmitter<?, ?, ?> t) {
    if (!t.isRemote()) {
      pendingJoins.add(t);
    }
  }

  public static void remove(Transmitter<?, ?, ?> removed) {
    if (!removed.isRemote()) {
      pendingRemovals.add(removed);
    }
  }

  static void destroyFluidConflict(Level level, BlockPos pos) {
    if (level instanceof ServerLevel sl) {
      sl.sendParticles(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20, 0.3, 0.3, 0.3, 0.05);
      sl.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 1.5f);
      sl.destroyBlock(pos, true);
    }
  }

  public static int networkCount() {
    return networks.size();
  }
}
