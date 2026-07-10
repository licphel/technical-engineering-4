package com.hypothetic.ten4.lib.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class NetworkManager<T> {
  private final Map<BlockPos, Network<T>> posToNetwork = new HashMap<>();
  private final Predicate<BlockEntity> isCable;
  private final EdgeGetter<T> edgeGetter;

  public NetworkManager(Predicate<BlockEntity> tester, EdgeGetter<T> edgeGetter) {
    this.isCable = tester;
    this.edgeGetter = edgeGetter;
  }

  public void onNodeAdded(Level level, BlockPos pos) {
    if (posToNetwork.containsKey(pos)) {
      return;
    }

    // Collect all distinct networks this node connects to
    Set<Network<T>> neighbors = new HashSet<>();
    for (Direction d : Direction.values()) {
      Network<T> net = posToNetwork.get(pos.relative(d));
      if (net != null) {
        neighbors.add(net);
      }
    }

    Network<T> merged;
    if (!neighbors.isEmpty()) {
      if (neighbors.size() == 1) {
        // Extending an existing network — just add this node
        merged = neighbors.iterator().next();
        merged.nodes.add(pos);
        posToNetwork.put(pos, merged);
        scanEdges(level, pos, merged);
      } else {
        // Bridge between multiple networks — merge them all
        merged = neighbors.iterator().next();
        for (Network<T> other : neighbors) {
          if (other != merged) {
            merged.absorb(other);
            for (BlockPos node : other.nodes) {
              posToNetwork.put(node, merged);
            }
          }
        }
        merged.nodes.add(pos);
        posToNetwork.put(pos, merged);
      }
    }
  }

  public void onNodeRemoved(Level level, BlockPos pos) {
    Network<T> old = posToNetwork.remove(pos);
    if (old == null) {
      return;
    }
    old.nodes.remove(pos);
    if (old.nodes.isEmpty()) {
      return;
    }

    // The network might have split — BFS from each neighbor to rebuild subnetworks
    for (Direction d : Direction.values()) {
      BlockPos neighbor = pos.relative(d);
      if (!old.nodes.contains(neighbor)) {
        continue;
      }
      Network<T> existing = posToNetwork.get(neighbor);
      if (existing != null && existing != old) {
        continue; // already reassigned
      }

      Network<T> sub = bfsBuild(level, neighbor);
      if (sub != old) {
        // This neighbor formed a new, separate network
        for (BlockPos node : sub.nodes) {
          posToNetwork.put(node, sub);
        }
      }
    }
  }

  public @Nullable Network<T> getNetwork(BlockPos pos) {
    return posToNetwork.getOrDefault(pos, null);
  }

  public void rebuildAll(Level level, Iterable<BlockPos> allCablePositions) {
    posToNetwork.clear();
    for (BlockPos pos : allCablePositions) {
      if (!posToNetwork.containsKey(pos)) {
        Network<T> net = bfsBuild(level, pos);
        for (BlockPos node : net.nodes) {
          posToNetwork.put(node, net);
        }
      }
    }
  }

  private Network<T> bfsBuild(Level level, BlockPos start) {
    Network<T> net = new Network<>();
    Deque<BlockPos> queue = new ArrayDeque<>();
    Set<BlockPos> visited = new HashSet<>();

    queue.add(start);
    visited.add(start);

    while (!queue.isEmpty()) {
      BlockPos pos = queue.poll();
      BlockEntity be = level.getBlockEntity(pos);
      if (be == null || !isCable.test(be)) {
        continue;
      }

      net.nodes.add(pos);
      scanEdges(level, pos, net);

      for (Direction d : Direction.values()) {
        BlockPos next = pos.relative(d);
        if (visited.contains(next)) {
          continue;
        }
        visited.add(next);
        BlockEntity neighborBe = level.getBlockEntity(next);
        if (neighborBe != null && isCable.test(neighborBe)) {
          queue.add(next);
        }
      }
    }
    return net;
  }

  private void scanEdges(Level level, BlockPos pos, Network<T> net) {
    for (Direction d : Direction.values()) {
      BlockPos target = pos.relative(d);
      BlockEntity be = level.getBlockEntity(target);
      if (be != null && isCable.test(be)) {
        continue; // cable-to-cable, not an edge
      }
      T cap = edgeGetter.get(level, target, d.getOpposite());
      if (cap != null) {
        net.edges.add(new Network.Edge<>(pos, d, target, cap));
      }
    }
  }

  @FunctionalInterface
  public interface EdgeGetter<T> {
    @Nullable T get(Level level, BlockPos machinePos, Direction fromCableSide);
  }
}
