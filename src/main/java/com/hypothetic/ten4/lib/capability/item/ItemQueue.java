package com.hypothetic.ten4.lib.capability.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.Arrays;
import java.util.Queue;

public final class ItemQueue {
  private static final ThreadLocal<IItemHandler[]> CACHE = ThreadLocal.withInitial(() -> new IItemHandler[6]);

  private ItemQueue() {
  }

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

    int maxRate = host.getMaxItemExtract(cycle.peek());
    for (int slot = 0; slot < inv.getSlots(); slot++) {
      ItemStack stack = inv.extractItem(slot, maxRate, true);
      if (stack.isEmpty()) {
        continue;
      }
      int remaining = stack.getCount();
      int per = Math.max(remaining / available, 1), rem = remaining - per * available;
      for (Direction d : cycle) {
        IItemHandler h = cache[d.ordinal()];
        if (h == null) {
          continue;
        }
        int amt = per + (rem > 0 ? 1 : 0);
        if (rem > 0) {
          rem--;
        }
        amt = Math.min(remaining, Math.min(host.getMaxItemExtract(d), amt));
        if (amt <= 0) {
          continue;
        }
        ItemStack toPush = inv.extractItem(slot, amt, false);
        if (toPush.isEmpty()) {
          continue;
        }
        ItemStack leftover = h.insertItem(findEmpty(h), toPush, false);
        if (!leftover.isEmpty()) {
          inv.insertItem(slot, leftover, false);
        }
        remaining -= (amt - leftover.getCount());
        if (remaining <= 0) {
          break;
        }
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
      int maxPull = host.getMaxItemReceive(d);
      for (int srcSlot = 0; srcSlot < h.getSlots(); srcSlot++) {
        if (maxPull <= 0) {
          break;
        }
        ItemStack pulled = h.extractItem(srcSlot, maxPull, true);
        if (pulled.isEmpty()) {
          continue;
        }
        int dest = findEmpty(inv);
        if (dest < 0) {
          break;
        }
        ItemStack leftover = inv.insertItem(dest, pulled.copy(), true);
        int movable = pulled.getCount() - leftover.getCount();
        if (movable <= 0) {
          continue;
        }
        pulled = h.extractItem(srcSlot, movable, false);
        leftover = inv.insertItem(dest, pulled, false);
        if (!leftover.isEmpty()) {
          h.insertItem(srcSlot, leftover, false);
        }
        maxPull -= movable - leftover.getCount();
      }
    }
    cycle.offer(cycle.remove());
  }

  private static int findEmpty(IItemHandler inv) {
    for (int i = 0; i < inv.getSlots(); i++) {
      if (inv.getStackInSlot(i).isEmpty()) {
        return i;
      }
    }
    return 0;
  }
}
