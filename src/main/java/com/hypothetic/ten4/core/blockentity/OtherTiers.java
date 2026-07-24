package com.hypothetic.ten4.core.blockentity;

import com.hypothetic.ten4.api.blockentity.device.DeviceInfo;
import com.hypothetic.ten4.core.registry.config.CfgCommon;
import com.hypothetic.ten4.core.registry.config.ModConfigs;

import java.util.function.Supplier;

public final class OtherTiers {
  public static Supplier<DeviceInfo> TANK;

  static {
    CfgCommon.Others o = ModConfigs.COMMON.others;
    TANK = () -> new DeviceInfo()
        .enableFluid()
        .setFluidThroughput(o.tankFluidThroughput.get());
  }

  private OtherTiers() {
  }
}
