package com.hypothetic.ten4.api.transmission.item;

import com.hypothetic.ten4.api.blockentity.internet.ItemDuctBlockEntity;
import com.hypothetic.ten4.api.network.duct.ItemExtractedPayload;
import com.hypothetic.ten4.api.transmission.ConnectionType;
import com.hypothetic.ten4.api.transmission.ITransmitterProvider;
import com.hypothetic.ten4.api.transmission.Transmitter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ItemTransmitter extends Transmitter<IItemHandler, ItemNetwork, ItemTransmitter> {
  public static final int DUCT_LENGTH = 128;
  final int speed, slots, slotCapacity;

  final Map<Integer, TransitEntry> transit = new LinkedHashMap<>();
  final List<TransitEntry> clientTransit = new ArrayList<>();
  private int nextId;

  public ItemTransmitter(ITransmitterProvider tile, int ticksPerBlock, int slots, int slotCapacity) {
    super(tile);
    this.speed = Math.max(1, DUCT_LENGTH / Math.max(1, ticksPerBlock));
    this.slots = slots;
    this.slotCapacity = slotCapacity;
  }

  public int getSpeed() { return speed; }
  public Map<Integer, TransitEntry> getTransitMap() { return transit; }
  public Collection<TransitEntry> getTransit() { return transit.values(); }
  public List<TransitEntry> getClientTransit() { return clientTransit; }
  public void addToClientTransit(TransitEntry e) { clientTransit.add(e); }

  private static TransitEntry copyForSync(TransitEntry src) {
    TransitEntry e = new TransitEntry();
    e.id = src.id;
    e.stack = src.stack.copy();
    e.entryDir = src.entryDir;
    e.direction = src.direction;
    e.route = src.route;
    e.routeIdx = src.routeIdx;
    return e;
  }

  // ---- Client-side autonomous transit: pathfind + move locally, server sync is just a periodic correction ----
  public void onUpdateClient(net.minecraft.world.level.Level level) {
    List<TransitEntry> movedOut = new ArrayList<>();
    for (TransitEntry e : clientTransit) {
      e.progress += speed;
      if (e.progress < DUCT_LENGTH) continue;
      e.progress -= DUCT_LENGTH;

      // Need a route? Run BFS on client-side network
      if (e.route == null || e.routeIdx >= e.route.length) {
        ItemNetwork net = getNetwork();
        if (net != null) {
          byte[] newRoute = RouteFinder.findRoute(net, getBlockPos(), e.stack);
          if (newRoute != null) {
            e.route = newRoute;
            e.routeIdx = 0;
            e.direction = newRoute[0];
          }
        }
        if (e.route == null || e.routeIdx >= e.route.length) {
          e.progress = 0; // stuck, retry next tick
          continue;
        }
      }

      Direction nextDir = Direction.values()[e.route[e.routeIdx]];
      BlockPos nextPos = getBlockPos().relative(nextDir);

      if (e.routeIdx == e.route.length - 1) {
        // Destination — simulate insertion locally, remove entry
        movedOut.add(e);
      } else {
        // Move to next transmitter in route
        if (level.getBlockEntity(nextPos) instanceof ItemDuctBlockEntity nextDuct) {
          e.entryDir = (byte) nextDir.getOpposite().ordinal();
          e.routeIdx++;
          e.direction = e.route[e.routeIdx];
          nextDuct.transmitter.clientTransit.add(e);
          movedOut.add(e);
        } else {
          // Next pipe not loaded — retry
          e.route = null;
          e.progress = 0;
        }
      }
    }
    clientTransit.removeAll(movedOut);
  }


  @Override
  public ItemNetwork createEmptyNetwork(UUID id) { return new ItemNetwork(id); }
  @Override public ItemNetwork createNetworkByMerging(Collection<ItemNetwork> nets) { return new ItemNetwork(nets); }
  @Override public boolean supportsTransmission(Transmitter<?, ?, ?> other) { return other instanceof ItemTransmitter; }

  @Override protected boolean isValidAcceptor(Direction side) {
    if (getLevel() == null) return false;
    return getLevel().getCapability(Capabilities.ItemHandler.BLOCK, getBlockPos().relative(side), side.getOpposite()) != null;
  }
  @Override protected @Nullable Transmitter<?, ?, ?> getTransmitter(ITransmitterProvider t) {
    if (t instanceof ItemDuctBlockEntity be) return be.transmitter;
    return null;
  }
  @Override public void takeShare() {}

  // ---- Server tick (unchanged logic, just without sync calls) ----
  public void onUpdateServer(ItemNetwork network, Level level) {
    BlockPos myPos = getBlockPos();
    boolean canPull = transit.size() < slots;

    if (canPull) {
      pull:
      for (Direction side : Direction.values()) {
      if (getConnectionTypeRaw(side) != ConnectionType.PULL) continue;
      IItemHandler inv = level.getCapability(Capabilities.ItemHandler.BLOCK, myPos.relative(side), side.getOpposite());
      if (inv == null) continue;
      for (int slot = 0; slot < inv.getSlots(); slot++) {
        ItemStack sim = inv.extractItem(slot, slotCapacity, true);
        if (sim.isEmpty()) continue;
        byte[] route = RouteFinder.findRoute(network, myPos, sim);
        if (route == null) continue;
        TransitEntry entry = new TransitEntry();
        entry.stack = inv.extractItem(slot, Math.min(slotCapacity, sim.getCount()), false);
        entry.entryDir = (byte) side.ordinal();
        entry.direction = route[0];
        entry.route = route;
        entry.routeIdx = 0;
        entry.id = nextId;
        transit.put(nextId++, entry);
        // Tell client: item entered the network. Client handles everything else locally.
        if (level instanceof ServerLevel sl) {
          PacketDistributor.sendToPlayersTrackingChunk(sl, sl.getChunkAt(myPos).getPos(),
              new ItemExtractedPayload(myPos, copyForSync(entry)));
        }
        break pull; // one extraction per tick total
      }
    }
    }

    // Advance items in transit
    List<Integer> toRemove = new ArrayList<>();
    for (var e : transit.entrySet()) {
      int id = e.getKey();
      TransitEntry entry = e.getValue();
      entry.progress += speed;
      if (entry.progress < DUCT_LENGTH) continue;
      entry.progress -= DUCT_LENGTH;

      if (entry.routeIdx < entry.route.length) {
        Direction nextDir = Direction.values()[entry.route[entry.routeIdx]];

        if (entry.routeIdx == entry.route.length - 1) {
          BlockPos destPos = myPos.relative(nextDir);
          IItemHandler dest = level.getCapability(Capabilities.ItemHandler.BLOCK, destPos, nextDir.getOpposite());
          if (dest != null) {
            ItemStack leftover = ItemHandlerHelper.insertItem(dest, entry.stack.copy(), false);
            if (leftover.isEmpty()) { toRemove.add(id); continue; }
            if (leftover.getCount() < entry.stack.getCount()) {
              entry.stack = leftover;
              entry.progress = 0;
              continue;
            }
          }
          byte[] newRoute = RouteFinder.findRoute(network, myPos, entry.stack);
          if (newRoute == null) { entry.progress = DUCT_LENGTH - speed; continue; }
          entry.route = newRoute;
          entry.routeIdx = 0;
          entry.direction = newRoute[0];
          continue;
        }

        BlockPos nextPos = myPos.relative(nextDir);
        ItemTransmitter dst = network.findTransmitter(nextPos);
        if (dst != null && dst.transit.size() < slots
            && getConnectionTypeRaw(nextDir).canSendTo()
            && dst.getConnectionTypeRaw(nextDir.getOpposite()).canAccept()) {
          entry.entryDir = (byte) nextDir.getOpposite().ordinal();
          entry.direction = entry.route[++entry.routeIdx];
          dst.transit.put(dst.nextId++, entry);
          toRemove.add(id);
          continue;
        }

        byte[] newRoute = RouteFinder.findRoute(network, myPos, entry.stack);
        if (newRoute == null) { entry.progress = DUCT_LENGTH - speed; continue; }
        entry.route = newRoute;
        entry.routeIdx = 0;
        entry.direction = newRoute[0];
      }
    }
    toRemove.forEach(transit::remove);
  }

  public static class TransitEntry {
    public int id;
    public ItemStack stack = ItemStack.EMPTY;
    public int progress;       // 0 ~ DUCT_LENGTH (server authoritative, client advances independently)
    public byte direction;     // exit direction
    public byte entryDir;      // entry direction
    public byte[] route;
    public int routeIdx;
  }
}
