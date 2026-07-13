package com.hypothetic.ten4.api.capability.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;

public final class ItemQueue {
  private static final ThreadLocal<IItemHandler[]> CACHE = ThreadLocal.withInitial(() -> new IItemHandler[6]);

  private ItemQueue() {
  }

  // ---- push: host → neighbors ----

  public static void push(Level level, BlockPos pos, IDirectionalItemProvider host) {
    if (host.getItemPushingCycle().isEmpty()) {
      return;
    }

    IItemHandler inv = host.getInventory();
    IItemHandler[] cache = CACHE.get();
    Arrays.fill(cache, null);
    Queue<Direction> cycle = host.getItemPushingCycle();
    int available = 0;

    for (Direction d : cycle) {
      if (!host.canExtractItem(d)) {
        continue;
      }
      IItemHandler h = level.getCapability(Capabilities.ItemHandler.BLOCK, pos.relative(d), d.getOpposite());
      if (h != null) {
        cache[d.ordinal()] = h;
        available++;
      }
    }
    if (available == 0) {
      cycle.offer(cycle.remove());
      return;
    }

    int maxRate = host.getItemThroughput();
    for (int slot = 0; slot < inv.getSlots(); slot++) {
      // 1. Simulate extract
      ItemStack simExtracted = inv.extractItem(slot, maxRate, true);
      if (simExtracted.isEmpty()) {
        continue;
      }

      // 2. Per-target sim-insertAll to find how much can actually fit
      Map<Direction, Integer> planned = new LinkedHashMap<>();
      int totalMovable = 0;
      ItemStack remaining = simExtracted.copy();
      for (Direction d : cycle) {
        IItemHandler h = cache[d.ordinal()];
        if (h == null || remaining.isEmpty()) {
          continue;
        }
        ItemStack leftover = insertAll(h, remaining.copy(), true);
        int moved = remaining.getCount() - leftover.getCount();
        if (moved > 0) {
          planned.put(d, moved);
          totalMovable += moved;
          remaining.shrink(moved);
        }
      }
      if (totalMovable <= 0) {
        continue;
      }

      // 3. Real extract only what all targets can accept
      ItemStack realExtracted = inv.extractItem(slot, totalMovable, false);
      if (realExtracted.isEmpty()) {
        continue;
      }

      // 4. Distribute to targets matching planned amounts
      for (var e : planned.entrySet()) {
        IItemHandler h = cache[e.getKey().ordinal()];
        int amt = Math.min(e.getValue(), realExtracted.getCount());
        if (amt <= 0) {
          continue;
        }
        ItemStack portion = realExtracted.split(amt);
        ItemStack leftover = insertAll(h, portion, false);
        if (!leftover.isEmpty()) {
          realExtracted.grow(leftover.getCount());
        }
      }
      // Return any undistributed remainder
      if (!realExtracted.isEmpty()) {
        inv.insertItem(slot, realExtracted, false);
      }
    }
    cycle.offer(cycle.remove());
  }

  public static void pull(Level level, BlockPos pos, IDirectionalItemProvider host) {
    if (host.getItemPullingCycle().isEmpty()) {
      return;
    }

    IItemHandler inv = host.getInventory();
    IItemHandler[] cache = CACHE.get();
    Arrays.fill(cache, null);
    Queue<Direction> cycle = host.getItemPullingCycle();
    int available = 0;

    for (Direction d : cycle) {
      if (!host.canReceiveItem(d)) {
        continue;
      }
      IItemHandler h = level.getCapability(Capabilities.ItemHandler.BLOCK, pos.relative(d), d.getOpposite());
      if (h != null) {
        cache[d.ordinal()] = h;
        available++;
      }
    }
    if (available == 0) {
      cycle.offer(cycle.remove());
      return;
    }

    for (Direction d : cycle) {
      IItemHandler h = cache[d.ordinal()];
      if (h == null) {
        continue;
      }
      int maxPull = host.getItemThroughput();
      for (int srcSlot = 0; srcSlot < h.getSlots() && maxPull > 0; srcSlot++) {
        // 1. Simulate extract from neighbor
        ItemStack simPulled = h.extractItem(srcSlot, maxPull, true);
        if (simPulled.isEmpty()) {
          continue;
        }

        // 2. Simulate insertAll into host to find actual fit
        ItemStack simLeftover = insertAll(inv, simPulled.copy(), true);
        int movable = simPulled.getCount() - simLeftover.getCount();
        if (movable <= 0) {
          continue;
        }

        // 3. Real extract + insert
        ItemStack pulled = h.extractItem(srcSlot, movable, false);
        ItemStack leftover = insertAll(inv, pulled, false);
        if (!leftover.isEmpty()) {
          h.insertItem(srcSlot, leftover, false);
        }

        maxPull -= movable - leftover.getCount();
      }
    }
    cycle.offer(cycle.remove());
  }

  private static ItemStack insertAll(IItemHandler inv, ItemStack stack, boolean sim) {
    ItemStack remaining = stack;
    for (int i = 0; i < inv.getSlots() && !remaining.isEmpty(); i++) {
      remaining = inv.insertItem(i, remaining, sim);
    }
    return remaining;
  }
}
