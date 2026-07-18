package com.hypothetic.ten4.core.blockentity.device;

import com.hypothetic.ten4.api.blockentity.device.DeviceInfo;
import com.hypothetic.ten4.core.registry.config.CfgCommon;
import com.hypothetic.ten4.core.registry.config.ModConfigs;

import java.util.function.Supplier;

public final class DeviceTiers {
  public static Supplier<DeviceInfo> PULVERIZER;
  public static Supplier<DeviceInfo> PRESS;
  public static Supplier<DeviceInfo> SMELTER;
  public static Supplier<DeviceInfo> WATER_PUMP;
  public static Supplier<DeviceInfo> HEAT_GENERATOR;

  private DeviceTiers() {
  }

  static {
    CfgCommon.Devices d = ModConfigs.COMMON.devices;
    PULVERIZER = () -> new DeviceInfo()
        .enableEnergy()
        .enableItem()
        .setPower(d.pulverizerPower.get())
        .setEnergyCapacity(d.pulverizerEnergyCapacity.get())
        .setEnergyThroughput(d.pulverizerEnergyThroughput.get())
        .setItemThroughput(d.pulverizerItemThroughput.get());
    PRESS = () -> new DeviceInfo()
        .enableEnergy()
        .enableItem()
        .setPower(d.pressPower.get())
        .setEnergyCapacity(d.pressEnergyCapacity.get())
        .setEnergyThroughput(d.pressEnergyThroughput.get())
        .setItemThroughput(d.pressItemThroughput.get());
    SMELTER = () -> new DeviceInfo()
        .enableEnergy()
        .enableItem()
        .setPower(d.smelterPower.get())
        .setEnergyCapacity(d.smelterEnergyCapacity.get())
        .setEnergyThroughput(d.smelterEnergyThroughput.get())
        .setItemThroughput(d.smelterItemThroughput.get());
    WATER_PUMP = () -> new DeviceInfo()
        .enableEnergy()
        .enableFluid()
        .setPower(d.waterPumpPower.get())
        .setEnergyCapacity(d.waterPumpEnergyCapacity.get())
        .setEnergyThroughput(d.waterPumpEnergyThroughput.get())
        .setFluidThroughput(d.waterPumpFluidThroughput.get());
    HEAT_GENERATOR = () -> new DeviceInfo().
        enableEnergy()
        .enableItem()
        .setPower(d.heatGeneratorPower.get())
        .setEnergyCapacity(d.heatGeneratorEnergyCapacity.get())
        .setEnergyThroughput(d.heatGeneratorEnergyThroughput.get())
        .setItemThroughput(d.heatGeneratorItemThroughput.get());
  }
}
