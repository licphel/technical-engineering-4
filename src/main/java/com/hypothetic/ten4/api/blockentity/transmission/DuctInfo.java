package com.hypothetic.ten4.api.blockentity.transmission;

public final class DuctInfo {
  int bufferCapacity; // in item ducts, this is SlotCapacity
  int throughput; // in item ducts, this is TicksPerBlock
  boolean opaque;

  public DuctInfo setBufferCapacity(int bufferCapacity) {
    this.bufferCapacity = bufferCapacity;
    return this;
  }

  public DuctInfo setThroughput(int throughput) {
    this.throughput = throughput;
    return this;
  }

  public DuctInfo setOpaque(boolean opaque) {
    this.opaque = opaque;
    return this;
  }

  public DuctInfo copy() {
    return new DuctInfo()
        .setBufferCapacity(bufferCapacity)
        .setThroughput(throughput)
        .setOpaque(opaque);
  }
}
