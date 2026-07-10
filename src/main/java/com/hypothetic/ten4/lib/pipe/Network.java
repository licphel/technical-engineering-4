package com.hypothetic.ten4.lib.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.*;

/**
 * A connected component of same-type cable/pipe blocks and their external connections.
 */
public class Network<T> {
  final Set<BlockPos> nodes = new HashSet<>();
  final List<Edge<T>> edges = new ArrayList<>();

  public Set<BlockPos> nodes() {
    return Collections.unmodifiableSet(nodes);
  }

  public List<Edge<T>> edges() {
    return Collections.unmodifiableList(edges);
  }

  public boolean isEmpty() {
    return nodes.isEmpty();
  }

  public int nodeCount() {
    return nodes.size();
  }

  /**
   * Absorbs all nodes and edges from {@code other} into this network.
   */
  void absorb(Network<T> other) {
    nodes.addAll(other.nodes);
    edges.addAll(other.edges);
  }

  /**
   * A connection from a network node to an external block.
   */
  public record Edge<T>(BlockPos cablePos, Direction side, BlockPos targetPos, T target) {
  }
}
