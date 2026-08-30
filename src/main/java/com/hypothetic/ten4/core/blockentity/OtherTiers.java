package com.hypothetic.ten4.core.blockentity;

import com.hypothetic.ten4.api.blockentity.device.DeviceInfo;
import com.hypothetic.ten4.core.registry.config.CfgCommon;
import com.hypothetic.ten4.core.registry.config.ModConfigs;

import java.util.function.Supplier;

public final class OtherTiers {
  public static Supplier<DeviceInfo> LIQUID_UNIT;
  public static Supplier<DeviceInfo> ENERGY_UNIT;

  static {
    CfgCommon.Others o = ModConfigs.COMMON.others;
    LIQUID_UNIT = () -> new DeviceInfo()
        .enableFluid()
        .setFluidThroughput(o.liquidUnitFluidThroughput.get());
    ENERGY_UNIT = () -> new DeviceInfo()
        .enableEnergy()
        .setEnergyCapacity(o.energyUnitCapacity.get())
        .setEnergyThroughput(o.energyUnitThroughput.get());
  }

  private OtherTiers() {
  }
}
