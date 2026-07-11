package com.hypothetic.ten4.lib.blockentity;

import com.hypothetic.ten4.Ten4;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public enum FaceMode {
  OFF,
  ACTIVE_INPUT,
  ACTIVE_OUTPUT,
  PASSIVE_INPUT,
  PASSIVE_OUTPUT,
  PASSIVE_BIPASS;

  public static final List<FaceMode> EXTRACTABLE = List.of(ACTIVE_OUTPUT, PASSIVE_OUTPUT, PASSIVE_BIPASS);
  public static final List<FaceMode> RECEIVABLE = List.of(ACTIVE_INPUT, PASSIVE_INPUT, PASSIVE_BIPASS);

  public String toString() {
    return switch (this) {
      case OFF -> "off";
      case ACTIVE_INPUT -> "active_input";
      case ACTIVE_OUTPUT -> "active_output";
      case PASSIVE_INPUT -> "passive_input";
      case PASSIVE_OUTPUT -> "passive_output";
      case PASSIVE_BIPASS -> "passive_bipass";
    };
  }

  public boolean isPassive() {
    return this == OFF || this == PASSIVE_BIPASS || this == PASSIVE_INPUT || this == PASSIVE_OUTPUT;
  }

  public boolean isIn() {
    return this == ACTIVE_INPUT || this == PASSIVE_INPUT;
  }

  public boolean isOut() {
    return this == ACTIVE_OUTPUT || this == PASSIVE_OUTPUT;
  }

  public boolean canExtract() {
    return this == ACTIVE_OUTPUT || this == PASSIVE_BIPASS || this == PASSIVE_OUTPUT;
  }

  public boolean canReceive() {
    return this == ACTIVE_INPUT || this == PASSIVE_BIPASS || this == PASSIVE_INPUT;
  }

  public MutableComponent getComponent() {
    return Component.translatable(Ten4.getLangKey("misc.facemode." + this));
  }

  public MutableComponent getDesc() {
    return Component.translatable(Ten4.getLangKey("misc.facemode." + this + ".desc"));
  }

  public static FaceMode of(int v) {
    return switch (v) {
      case 1 -> ACTIVE_INPUT;
      case 2 -> ACTIVE_OUTPUT;
      case 3 -> PASSIVE_INPUT;
      case 4 -> PASSIVE_OUTPUT;
      case 5 -> PASSIVE_BIPASS;
      default -> OFF;
    };
  }
}
