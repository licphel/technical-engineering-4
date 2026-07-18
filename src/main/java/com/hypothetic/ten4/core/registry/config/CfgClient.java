package com.hypothetic.ten4.core.registry.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class CfgClient {
  public final Render render;

  public CfgClient(ModConfigSpec.Builder b) {
    render = new Render(b);
  }

  public static class Render {
    public final ModConfigSpec.IntValue ductLODDistance;

    Render(ModConfigSpec.Builder b) {
      b.push("render");
      ductLODDistance = b.defineInRange("ductLodDistance", 32, 0, Integer.MAX_VALUE);
      b.pop();
    }
  }
}
