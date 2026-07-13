package com.hypothetic.ten4.api.transmission;

import com.hypothetic.ten4.Ten4;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.*;

@EventBusSubscriber(modid = Ten4.ID)
public final class TransmitterNetworkRegistry {
  private static final Set<DynamicNetwork<?, ?, ?>> networks = new HashSet<>();
  private static final Map<UUID, DynamicNetwork<?, ?, ?>> clientNetworks = new HashMap<>();
  private static int pruneCounter;

  private TransmitterNetworkRegistry() {}

  @SubscribeEvent
  public static void onLevelTick(LevelTickEvent.Post e) {
    Level level = e.getLevel();
    if (level.isClientSide()) return;

    for (DynamicNetwork<?, ?, ?> net : networks) {
      net.onUpdate();
    }

    if (++pruneCounter >= 100) {
      pruneCounter = 0;
      networks.removeIf(DynamicNetwork::isEmpty);
    }
  }

  // ---- Client networks ----
  public static void addClientNetwork(UUID id, DynamicNetwork<?, ?, ?> net) { clientNetworks.putIfAbsent(id, net); }
  public static DynamicNetwork<?, ?, ?> getClientNetwork(UUID id) { return clientNetworks.get(id); }
  public static void removeClientNetwork(DynamicNetwork<?, ?, ?> net) { clientNetworks.remove(net.getUUID()); }

  // ---- Server networks ----
  public static void registerNetwork(DynamicNetwork<?, ?, ?> net) { networks.add(net); }
  public static void removeNetwork(DynamicNetwork<?, ?, ?> net) { networks.remove(net); }

  // ---- Synchronous network join: called from onLoad/onPlace ----
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static void joinNetwork(Transmitter<?, ?, ?> start) {
    if (start.isRemote()) return;
    Level world = start.getLevel();
    if (world == null) return;

    CompatibleTransmitterValidator validator = start.getNewOrphanValidator();
    Set<Transmitter> connected = new HashSet<>();
    Set<DynamicNetwork> foundNetworks = new HashSet<>();
    Set<BlockPos> visited = new HashSet<>();
    Deque<BlockPos> queue = new ArrayDeque<>();

    // BFS: find all physically+logically connected transmitters
    queue.add(start.getBlockPos());
    while (!queue.isEmpty()) {
      BlockPos pos = queue.pollFirst();
      if (!visited.add(pos)) continue;

      BlockEntity be = world.getBlockEntity(pos);
      if (!(be instanceof ITransmitterProvider tb)) continue;
      Transmitter t = tb.getTransmitter();
      if (t == null || !t.isValid() || !start.supportsTransmission(t)) continue;

      DynamicNetwork net = t.getNetwork();
      if (net != null) {
        if (validator.isNetworkCompatible(net)) {
          foundNetworks.add(net);
        }
        continue; // already networked, don't add to connected
      }

      // Orphan or new transmitter — check compatibility
      if (!validator.isTransmitterCompatible(t)) continue;
      connected.add(t);

      // Expand to neighbors via isValidTransmitterBasic (Mekanism pattern)
      for (Direction d : Direction.values()) {
        BlockPos next = pos.relative(d);
        if (!visited.contains(next)) {
          BlockEntity neighborBe = world.getBlockEntity(next);
          if (neighborBe instanceof ITransmitterProvider ntb) {
            Transmitter<?, ?, ?> neighbor = ntb.getTransmitter();
            if (neighbor != null && t.isValidTransmitterBasic(ntb, d)) {
              queue.addLast(next);
            }
          }
        }
      }
    }

    // Create or merge network
    DynamicNetwork network;
    if (foundNetworks.isEmpty()) {
      network = start.createEmptyNetwork(UUID.randomUUID());
      network.register();
    } else if (foundNetworks.size() == 1) {
      network = foundNetworks.iterator().next();
    } else {
      network = start.createNetworkByMerging((Collection) foundNetworks);
    }

    // Remove all connected transmitters from their old networks first
    for (Transmitter t : connected) {
      DynamicNetwork oldNet = t.getNetwork();
      if (oldNet != null && oldNet != network) {
        oldNet.removeTransmitter(t);
        t.setNetwork(null, false);
      }
    }

    network.addNewTransmitters(connected, validator);
    network.commit();
  }

  /** Called when a transmitter is removed — destroy old network, rebuild from each neighbor. */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static void onTransmitterRemoved(Transmitter<?, ?, ?> removed) {
    if (removed.isRemote()) return;
    Level world = removed.getLevel();
    if (world == null) return;

    DynamicNetwork oldNet = removed.getNetwork();

    // Destroy old network: orphan all members
    if (oldNet != null) {
      for (Object o : new ArrayList<>(oldNet.getTransmitters())) {
        Transmitter t = (Transmitter) o;
        t.takeShare();
        t.setNetwork(null, false);
      }
      oldNet.deregister();
    }
    removed.setNetwork(null, false);

    // Rebuild from each valid neighbor
    for (Direction d : Direction.values()) {
      BlockPos neighborPos = removed.getBlockPos().relative(d);
      BlockEntity be = world.getBlockEntity(neighborPos);
      if (be instanceof ITransmitterProvider tb && removed.isValidTransmitterBasic(tb, d)) {
        Transmitter t = tb.getTransmitter();
        if (t != null && t.isValid()) {
          joinNetwork(t);
        }
      }
    }
  }

  // ---- Fluid conflict ----
  static void destroyFluidConflict(Level level, BlockPos pos) {
    if (level instanceof ServerLevel sl) {
      sl.sendParticles(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
          20, 0.3, 0.3, 0.3, 0.05);
      sl.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 1.5f);
      sl.destroyBlock(pos, true);
    }
  }

  public static int networkCount() { return networks.size(); }
}
