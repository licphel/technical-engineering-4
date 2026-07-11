package com.hypothetic.ten4.lib.blockentity;

import com.hypothetic.ten4.Ten4;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum SignalMode {
  IGNORE,
  LOW,
  HIGH;

  @Override
  public String toString() {
    return switch (this) {
      case IGNORE -> "ignore";
      case LOW -> "low_level";
      case HIGH -> "high_level";
    };
  }

  public MutableComponent getComponent() {
    return Component.translatable(Ten4.getLangKey("misc.sigmode." + this));
  }

  public MutableComponent getDesc() {
    return Component.translatable(Ten4.getLangKey("misc.sigmode." + this + ".desc"));
  }

  public static SignalMode of(int v) {
    return switch (v) {
      case 1 -> LOW;
      case 2 -> HIGH;
      default -> IGNORE;
    };
  }
}
