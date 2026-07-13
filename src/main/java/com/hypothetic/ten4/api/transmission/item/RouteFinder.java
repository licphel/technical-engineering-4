package com.hypothetic.ten4.api.transmission.item;

import com.hypothetic.ten4.api.transmission.ITransmitterProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.*;

final class RouteFinder {
  static byte @Nullable [] findRoute(ItemNetwork network, BlockPos start, ItemStack stack) {
    ItemTransmitter tr = network.findTransmitter(start);
    if (tr == null) {
      return null;
    }
    Level level = tr.getLevel();
    if (level == null) {
      return null;
    }

    List<byte[]> allRoutes = new ArrayList<>();
    Deque<Node> queue = new ArrayDeque<>();
    Set<BlockPos> visited = new HashSet<>();

    queue.add(new Node(start, null, null));
    visited.add(start);

    while (!queue.isEmpty()) {
      Node node = queue.pollFirst();
      ItemTransmitter curr = network.findTransmitter(node.pos);
      if (curr == null) {
        continue;
      }

      for (Direction d : Direction.values()) {
        if (!curr.getConnectionTypeRaw(d).canSendTo()) {
          continue;
        }
        BlockPos next = node.pos.relative(d);

        if (!network.hasTransmitter(next)) {
          if (!(level.getBlockEntity(next) instanceof ITransmitterProvider)) {
            IItemHandler cap = level.getCapability(Capabilities.ItemHandler.BLOCK, next, d.getOpposite());
            if (cap != null) {
              ItemStack leftover = ItemHandlerHelper.insertItem(cap, stack, true);
              if (leftover.getCount() < stack.getCount()) {
                allRoutes.add(buildRoute(node, d));
              }
            }
          }
          continue;
        }

        if (!visited.add(next)) {
          continue;
        }
        ItemTransmitter dst = network.findTransmitter(next);
        if (dst == null || !dst.getConnectionTypeRaw(d.getOpposite()).canAccept()) {
          continue;
        }
        queue.add(new Node(next, d, node));
      }
    }

    if (allRoutes.isEmpty()) {
      return null;
    }
    // Round-robin: pick next route
    int idx = network.nextRouteIndex.getAndIncrement() % allRoutes.size();
    return allRoutes.get(idx);
  }

  private static byte[] buildRoute(Node end, Direction finalDir) {
    List<Direction> dirs = new ArrayList<>();
    dirs.add(finalDir);
    Node n = end;
    while (n.prev != null && n.fromDir != null) {
      dirs.add(n.fromDir);
      n = n.prev;
    }
    Collections.reverse(dirs);
    byte[] result = new byte[dirs.size()];
    for (int i = 0; i < dirs.size(); i++) {
      result[i] = (byte) dirs.get(i).ordinal();
    }
    return result;
  }

  private record Node(BlockPos pos, @Nullable Direction fromDir, @Nullable Node prev) {
  }
}
