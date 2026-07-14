package com.hypothetic.ten4.core.blockentity.duct;

import com.hypothetic.ten4.api.blockentity.transmission.DuctInfo;

public final class DuctTiers {
  private DuctTiers() {
  }

  public static final DuctInfo COPPER_ENERGY = new DuctInfo().setBufferCapacity(100).setThroughput(100);
  public static final DuctInfo COPPER_FLUID = new DuctInfo().setBufferCapacity(100).setThroughput(100);
  public static final DuctInfo COPPER_ITEM = new DuctInfo().setBufferCapacity(1).setThroughput(20);
}
