package com.hypothetic.ten4.api.transmission.item;

import com.hypothetic.ten4.api.blockentity.transmission.ItemDuctBlockEntity;
import com.hypothetic.ten4.api.network.duct.ItemExtractedPayload;
import com.hypothetic.ten4.api.transmission.ConnectionType;
import com.hypothetic.ten4.api.transmission.ITransmitterProvider;
import com.hypothetic.ten4.api.transmission.Transmitter;
import com.hypothetic.ten4.api.transmission.TransmitterFilter;
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
  public static final int DUCT_LENGTH = 64;
  final int speed, slotCapacity;

  public @Nullable TransitEntry transitEntry;
  public @Nullable TransitEntry clientEntry;
  private int nextId;

  public ItemTransmitter(ITransmitterProvider tile, int ticksPerBlock, int slotCapacity) {
    super(tile);
    this.speed = Math.max(1, DUCT_LENGTH / Math.max(1, ticksPerBlock));
    this.slotCapacity = slotCapacity;
  }

  private static TransitEntry copyForSync(TransitEntry src) {
    TransitEntry e = new TransitEntry();
    e.id = src.id;
    e.stack = src.stack.copy();
    e.entrySide = src.entrySide;
    e.exitSide = src.exitSide;
    e.route = src.route;
    e.index = src.index;
    return e;
  }

  public int getSpeed() {
    return speed;
  }

  public int getSlotCapacity() {
    return slotCapacity;
  }

  public Collection<TransitEntry> getTransit() {
    return transitEntry == null ? Collections.emptyList() : Collections.singletonList(transitEntry);
  }

  public List<TransitEntry> getClientTransit() {
    return clientEntry == null ? Collections.emptyList() : Collections.singletonList(clientEntry);
  }

  public void addToClientTransit(TransitEntry e) {
    clientEntry = e;
  }

  public Map<Integer, TransitEntry> getTransitMap() {
    return transitEntry == null ? Collections.emptyMap() : Collections.singletonMap(transitEntry.id, transitEntry);
  }

  public void loadTransitFromMap(Map<Integer, TransitEntry> map) {
    transitEntry = map.isEmpty() ? null : map.values().iterator().next();
  }

  public void onUpdateClient(Level level) {
    if (clientEntry == null) {
      return;
    }
    TransitEntry e = clientEntry;
    e.progress += speed;
    if (e.progress < DUCT_LENGTH) {
      return;
    }
    e.progress -= DUCT_LENGTH;

    if (e.route == null || e.index >= e.route.length) {
      ItemNetwork net = getNetwork();
      if (net != null) {
        byte[] newRoute = RouteFinder.findRoute(net, getBlockPos(), e.stack);
        if (newRoute != null) {
          e.route = newRoute;
          e.index = 0;
          e.exitSide = newRoute[0];
        }
      }
      if (e.route == null || e.index >= e.route.length) {
        e.progress = 0;
        return;
      }
    }

    Direction nextDir = Direction.values()[e.route[e.index]];
    BlockPos nextPos = getBlockPos().relative(nextDir);

    if (e.index == e.route.length - 1) {
      clientEntry = null; // reached destination
    } else if (level.getBlockEntity(nextPos) instanceof ItemDuctBlockEntity nextDuct) {
      e.entrySide = (byte) nextDir.getOpposite().ordinal();
      e.index++;
      e.exitSide = e.route[e.index];
      nextDuct.transmitter.clientEntry = e;
      clientEntry = null;
    } else {
      e.route = null;
      e.progress = 0;
    }
  }

  void onUpdateServer(ItemNetwork network, Level level, @Nullable TransitEntry snapshot) {
    BlockPos myPos = getBlockPos();

    // PULL
    if (transitEntry == null) {
      pull:
      for (Direction side : Direction.values()) {
        if (getConnectionTypeRaw(side) != ConnectionType.PULL) {
          continue;
        }
        IItemHandler inv = level.getCapability(Capabilities.ItemHandler.BLOCK, myPos.relative(side), side.getOpposite());
        if (inv == null) {
          continue;
        }
        for (int slot = 0; slot < inv.getSlots(); slot++) {
          ItemStack sim = inv.extractItem(slot, slotCapacity, true);
          if (sim.isEmpty()) {
            continue;
          }
          byte[] route = RouteFinder.findRoute(network, myPos, sim);
          if (route == null) {
            continue;
          }
          TransitEntry entry = new TransitEntry();
          entry.stack = inv.extractItem(slot, Math.min(slotCapacity, sim.getCount()), false);
          entry.entrySide = (byte) side.ordinal();
          entry.exitSide = route[0];
          entry.route = route;
          entry.index = 0;
          entry.id = nextId++;
          transitEntry = entry;
          if (level instanceof ServerLevel sl) {
            PacketDistributor.sendToPlayersTrackingChunk(sl, sl.getChunkAt(myPos).getPos(),
                new ItemExtractedPayload(myPos, copyForSync(entry)));
          }
          break pull;
        }
      }
    }

    // Advance (only if in snapshot — prevents same-tick multi-hop)
    if (snapshot == null) {
      return;
    }
    snapshot.progress += speed;
    if (snapshot.progress < DUCT_LENGTH) {
      return;
    }
    snapshot.progress -= DUCT_LENGTH;

    if (snapshot.index < snapshot.route.length) {
      Direction nextDir = Direction.values()[snapshot.route[snapshot.index]];

      if (snapshot.index == snapshot.route.length - 1) {
        BlockPos target = myPos.relative(nextDir);
        IItemHandler targetHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, target, nextDir.getOpposite());
        if (targetHandler != null) {
          ItemStack leftover = ItemHandlerHelper.insertItem(targetHandler, snapshot.stack.copy(), false);
          if (leftover.isEmpty()) {
            transitEntry = null;
            return;
          }
          if (leftover.getCount() < snapshot.stack.getCount()) {
            snapshot.stack = leftover;
            snapshot.progress = 0;
            return;
          }
        }
        byte[] newRoute = RouteFinder.findRoute(network, myPos, snapshot.stack);
        if (newRoute == null) {
          snapshot.progress = DUCT_LENGTH - speed;
          return;
        }
        snapshot.route = newRoute;
        snapshot.index = 0;
        snapshot.exitSide = newRoute[0];
        return;
      }

      BlockPos nextPos = myPos.relative(nextDir);
      ItemTransmitter dst = network.findTransmitter(nextPos);
      if (dst != null && dst.transitEntry == null
          && getConnectionTypeRaw(nextDir).canBorrow()
          && dst.getConnectionTypeRaw(nextDir.getOpposite()).canAccept()) {
        // Filter gate: check if source side allows this item to pass
        TransmitterFilter<ItemStack> f = getFilter(nextDir);
        if (f != null) {
          ItemStack filtered = f.getFiltered(snapshot.stack);
          if (filtered.isEmpty()) {
            snapshot.progress = 0;
            return;
          }
          snapshot.stack = filtered;
        }
        snapshot.entrySide = (byte) nextDir.getOpposite().ordinal();
        snapshot.index++;
        snapshot.exitSide = snapshot.route[snapshot.index];
        dst.transitEntry = snapshot;
        transitEntry = null;
        return;
      }

      byte[] newRoute = RouteFinder.findRoute(network, myPos, snapshot.stack);
      if (newRoute == null) {
        snapshot.progress = DUCT_LENGTH - speed;
        return;
      }
      snapshot.route = newRoute;
      snapshot.index = 0;
      snapshot.exitSide = newRoute[0];
    }
  }

  @Override
  public ItemNetwork createEmptyNetwork(UUID id) {
    return new ItemNetwork(id);
  }

  @Override
  public ItemNetwork createNetworkByMerging(Collection<ItemNetwork> nets) {
    return new ItemNetwork(nets);
  }

  @Override
  public boolean supportsTransmission(Transmitter<?, ?, ?> other) {
    return other instanceof ItemTransmitter;
  }

  @Override
  protected boolean isValidAcceptor(Direction side) {
    if (getLevel() == null) {
      return false;
    }
    return getLevel().getCapability(Capabilities.ItemHandler.BLOCK, getBlockPos().relative(side), side.getOpposite()) != null;
  }

  @Override
  public void takeShare() {
  }

  public static class TransitEntry {
    public int id;
    public ItemStack stack = ItemStack.EMPTY;
    public int progress;
    public byte exitSide;
    public byte entrySide;
    public byte[] route;
    public int index;
  }
}
