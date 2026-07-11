package com.hypothetic.ten4.lib.blockentity;

import com.hypothetic.ten4.Ten4;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum ComparatorMode {
  OFF,
  ENERGY,
  OUTPUT_ITEMS,
  OUTPUT_FLUID,
  ACTIVE;

  public static ComparatorMode of(int v) {
    return switch (v) {
      case 1 -> ENERGY;
      case 2 -> OUTPUT_ITEMS;
      case 3 -> OUTPUT_FLUID;
      case 4 -> ACTIVE;
      default -> OFF;
    };
  }

  @Override
  public String toString() {
    return switch (this) {
      case OFF -> "off";
      case ENERGY -> "energy";
      case OUTPUT_ITEMS -> "output_items";
      case OUTPUT_FLUID -> "output_fluid";
      case ACTIVE -> "active";
    };
  }

  public MutableComponent getComponent() {
    return Component.translatable(Ten4.getLangKey("misc.comparator_mode." + this));
  }

  public MutableComponent getDesc() {
    return Component.translatable(Ten4.getLangKey("misc.comparator_mode." + this + ".desc"));
  }
}
