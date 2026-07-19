package com.hypothetic.ten4.api.transmission.item;

import com.hypothetic.ten4.api.transmission.ITransmitterProvider;
import com.hypothetic.ten4.api.transmission.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemNetwork extends Network<IItemHandler, ItemNetwork, ItemTransmitter> {
  final AtomicInteger nextRouteIndex = new AtomicInteger();

  public ItemNetwork(UUID id) {
    super(id);
  }

  public ItemNetwork(Collection<ItemNetwork> nets) {
    super(UUID.randomUUID());
    adoptAllAndRegister(nets);
  }

  @Override
  public void onUpdate() {
    Level level = null;
    // Snapshot all transits at tick start — prevent same-tick multi-hop
    Map<ItemTransmitter, ItemTransmitter.TransitEntry> snapshots = new HashMap<>();
    for (ItemTransmitter t : getTransmitters()) {
      snapshots.put(t, t.transitEntry);
      if (level == null) {
        level = t.getLevel();
      }
    }
    if (level == null) {
      return;
    }
    for (Map.Entry<ItemTransmitter, ItemTransmitter.TransitEntry> e : snapshots.entrySet()) {
      e.getKey().onUpdateServer(this, level, e.getValue());
    }
  }

  boolean canAnyAccept(ItemStack stack, ItemTransmitter asker) {
    for (Map.Entry<BlockPos, ItemTransmitter> e : positionedTransmitters.entrySet()) {
      ItemTransmitter tr = e.getValue();
      Level level = tr.getLevel();

      if (level == null) {
        continue;
      }

      for (Direction d : Direction.values()) {
        if (!tr.getConnectionTypeRaw(d).isPushOrNormal()) {
          continue;
        }
        BlockPos t = e.getKey().relative(d);
        if (positionedTransmitters.containsKey(t)) {
          continue;
        }
        if (level.getBlockEntity(t) instanceof ITransmitterProvider) {
          continue;
        }
        IItemHandler cap = level.getCapability(Capabilities.ItemHandler.BLOCK, t, d.getOpposite());
        if (cap == null) {
          continue;
        }
        ItemStack leftover = ItemHandlerHelper.insertItem(cap, stack, true);
        if (leftover.getCount() < stack.getCount()) {
          return true;
        }
      }
    }
    return false;
  }

  boolean hasTransmitter(BlockPos pos) {
    return positionedTransmitters.containsKey(pos);
  }

  @Nullable ItemTransmitter findTransmitter(BlockPos pos) {
    return positionedTransmitters.get(pos);
  }
}
