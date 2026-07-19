package com.hypothetic.ten4.core.blockentity.duct;

import com.hypothetic.ten4.api.blockentity.duct.DuctInfo;
import com.hypothetic.ten4.core.registry.config.CfgCommon;
import com.hypothetic.ten4.core.registry.config.ModConfigs;

import java.util.function.Supplier;

public final class DuctTiers {
  public static Supplier<DuctInfo> COPPER_ENERGY;
  public static Supplier<DuctInfo> COPPER_FLUID;
  public static Supplier<DuctInfo> COPPER_ITEM;

  static {
    CfgCommon.Ducts d = ModConfigs.COMMON.ducts;
    COPPER_ENERGY = () -> new DuctInfo()
        .setBufferCapacity(d.copperEnergyBuffer.get())
        .setThroughput(d.copperEnergyThroughput.get());
    COPPER_FLUID = () -> new DuctInfo()
        .setBufferCapacity(d.copperFluidBuffer.get())
        .setThroughput(d.copperFluidThroughput.get());
    COPPER_ITEM = () -> new DuctInfo()
        .setBufferCapacity(d.copperItemBuffer.get())
        .setThroughput(d.copperItemThroughput.get());
  }

  private DuctTiers() {
  }
}
