package com.hypothetic.ten4.core.registry.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Objects;

public final class CfgClient {
  public final Render render;

  public CfgClient(ModConfigSpec.Builder render) {
    this.render = new Render(render);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    var that = (CfgClient) obj;
    return Objects.equals(this.render, that.render);
  }

  @Override
  public int hashCode() {
    return Objects.hash(render);
  }

  @Override
  public String toString() {
    return "CfgClient[" +
        "render=" + render + ']';
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
