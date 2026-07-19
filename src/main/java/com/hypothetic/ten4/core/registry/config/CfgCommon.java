package com.hypothetic.ten4.core.registry.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class CfgCommon {
  public final Devices devices;
  public final Ducts ducts;

  public CfgCommon(ModConfigSpec.Builder b) {
    devices = new Devices(b);
    ducts = new Ducts(b);
  }

  public static class Devices {
    public final ModConfigSpec.IntValue pulverizerPower;
    public final ModConfigSpec.IntValue pulverizerEnergyCapacity;
    public final ModConfigSpec.IntValue pulverizerEnergyThroughput;
    public final ModConfigSpec.IntValue pulverizerItemThroughput;

    public final ModConfigSpec.IntValue pressPower;
    public final ModConfigSpec.IntValue pressEnergyCapacity;
    public final ModConfigSpec.IntValue pressEnergyThroughput;
    public final ModConfigSpec.IntValue pressItemThroughput;

    public final ModConfigSpec.IntValue smelterPower;
    public final ModConfigSpec.IntValue smelterEnergyCapacity;
    public final ModConfigSpec.IntValue smelterEnergyThroughput;
    public final ModConfigSpec.IntValue smelterItemThroughput;

    public final ModConfigSpec.IntValue refinerPower;
    public final ModConfigSpec.IntValue refinerEnergyCapacity;
    public final ModConfigSpec.IntValue refinerEnergyThroughput;
    public final ModConfigSpec.IntValue refinerItemThroughput;
    public final ModConfigSpec.IntValue refinerFluidThroughput;

    public final ModConfigSpec.IntValue waterPumpPower;
    public final ModConfigSpec.IntValue waterPumpEnergyCapacity;
    public final ModConfigSpec.IntValue waterPumpEnergyThroughput;
    public final ModConfigSpec.IntValue waterPumpFluidThroughput;

    public final ModConfigSpec.IntValue heatGeneratorPower;
    public final ModConfigSpec.IntValue heatGeneratorEnergyCapacity;
    public final ModConfigSpec.IntValue heatGeneratorEnergyThroughput;
    public final ModConfigSpec.IntValue heatGeneratorItemThroughput;

    public final ModConfigSpec.DoubleValue overclockingMultiplier;
    public final ModConfigSpec.DoubleValue energyCoilMultiplier;
    public final ModConfigSpec.DoubleValue pistonMultiplier;
    public final ModConfigSpec.DoubleValue vacuumMultiplier;

    Devices(ModConfigSpec.Builder b) {
      b.push("devices");
      pulverizerPower = b.defineInRange("pulverizerPower", 15, 0, Integer.MAX_VALUE);
      pulverizerEnergyCapacity = b.defineInRange("pulverizerEnergyCapacity", 10000, 0, Integer.MAX_VALUE);
      pulverizerEnergyThroughput = b.defineInRange("pulverizerEnergyThroughput", 100, 0, Integer.MAX_VALUE);
      pulverizerItemThroughput = b.defineInRange("pulverizerItemThroughput", 1, 0, Integer.MAX_VALUE);

      pressPower = b.defineInRange("pressPower", 15, 0, Integer.MAX_VALUE);
      pressEnergyCapacity = b.defineInRange("pressEnergyCapacity", 10000, 0, Integer.MAX_VALUE);
      pressEnergyThroughput = b.defineInRange("pressEnergyThroughput", 100, 0, Integer.MAX_VALUE);
      pressItemThroughput = b.defineInRange("pressItemThroughput", 1, 0, Integer.MAX_VALUE);

      smelterPower = b.defineInRange("smelterPower", 10, 0, Integer.MAX_VALUE);
      smelterEnergyCapacity = b.defineInRange("smelterEnergyCapacity", 10000, 0, Integer.MAX_VALUE);
      smelterEnergyThroughput = b.defineInRange("smelterEnergyThroughput", 100, 0, Integer.MAX_VALUE);
      smelterItemThroughput = b.defineInRange("smelterItemThroughput", 1, 0, Integer.MAX_VALUE);

      refinerPower = b.defineInRange("refinerPower", 20, 0, Integer.MAX_VALUE);
      refinerEnergyCapacity = b.defineInRange("refinerEnergyCapacity", 10000, 0, Integer.MAX_VALUE);
      refinerEnergyThroughput = b.defineInRange("refinerEnergyThroughput", 100, 0, Integer.MAX_VALUE);
      refinerItemThroughput = b.defineInRange("refinerItemThroughput", 1, 0, Integer.MAX_VALUE);
      refinerFluidThroughput = b.defineInRange("refinerFluidThroughput", 100, 0, Integer.MAX_VALUE);

      waterPumpPower = b.defineInRange("waterPumpPower", 5, 0, Integer.MAX_VALUE);
      waterPumpEnergyCapacity = b.defineInRange("waterPumpEnergyCapacity", 10000, 0, Integer.MAX_VALUE);
      waterPumpEnergyThroughput = b.defineInRange("waterPumpEnergyThroughput", 100, 0, Integer.MAX_VALUE);
      waterPumpFluidThroughput = b.defineInRange("waterPumpFluidThroughput", 1000, 0, Integer.MAX_VALUE);

      heatGeneratorPower = b.defineInRange("heatGeneratorPower", 20, 0, Integer.MAX_VALUE);
      heatGeneratorEnergyCapacity = b.defineInRange("heatGeneratorEnergyCapacity", 10000, 0, Integer.MAX_VALUE);
      heatGeneratorEnergyThroughput = b.defineInRange("heatGeneratorEnergyThroughput", 100, 0, Integer.MAX_VALUE);
      heatGeneratorItemThroughput = b.defineInRange("heatGeneratorItemThroughput", 1, 0, Integer.MAX_VALUE);

      overclockingMultiplier = b.defineInRange("overclockingMultiplier", 1.25, 1.0, 100.0);
      energyCoilMultiplier = b.defineInRange("energyCoilMultiplier", 2.0, 1.0, 100.0);
      pistonMultiplier = b.defineInRange("pistonMultiplier", 2.0, 1.0, 100.0);
      vacuumMultiplier = b.defineInRange("vacuumMultiplier", 2.0, 1.0, 100.0);
      b.pop();
    }
  }

  public static class Ducts {
    public final ModConfigSpec.IntValue copperEnergyBuffer;
    public final ModConfigSpec.IntValue copperEnergyThroughput;
    public final ModConfigSpec.IntValue copperFluidBuffer;
    public final ModConfigSpec.IntValue copperFluidThroughput;
    public final ModConfigSpec.IntValue copperItemBuffer;
    public final ModConfigSpec.IntValue copperItemThroughput;

    Ducts(ModConfigSpec.Builder b) {
      b.push("ducts");
      copperEnergyBuffer = b.defineInRange("copperEnergyBuffer", 100, 0, Integer.MAX_VALUE);
      copperEnergyThroughput = b.defineInRange("copperEnergyThroughput", 100, 0, Integer.MAX_VALUE);
      copperFluidBuffer = b.defineInRange("copperFluidBuffer", 100, 0, Integer.MAX_VALUE);
      copperFluidThroughput = b.defineInRange("copperFluidThroughput", 100, 0, Integer.MAX_VALUE);
      copperItemBuffer = b.defineInRange("copperItemBuffer", 1, 0, Integer.MAX_VALUE);
      copperItemThroughput = b.defineInRange("copperItemThroughput", 20, 0, Integer.MAX_VALUE);
      b.pop();
    }
  }
}
