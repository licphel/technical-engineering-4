package com.hypothetic.ten4.api.blockentity.transmission;

import com.hypothetic.ten4.api.capability.fluid.FluidTank;
import com.hypothetic.ten4.api.capability.item.ItemSlot;

import java.util.ArrayList;
import java.util.List;

public final class DuctInfo {
  int bufferCapacity; // in item ducts, this is SlotCapacity
  int throughput; // in item ducts, this is TicksPerBlock

  public DuctInfo setBufferCapacity(int bufferCapacity) {
    this.bufferCapacity = bufferCapacity;
    return this;
  }

  public DuctInfo setThroughput(int throughput) {
    this.throughput = throughput;
    return this;
  }
}
