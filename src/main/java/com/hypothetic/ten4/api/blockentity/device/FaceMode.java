package com.hypothetic.ten4.api.blockentity.device;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.ITranslatable;

public enum FaceMode implements ITranslatable {
  OFF,
  ACTIVE_INPUT,
  ACTIVE_OUTPUT,
  PASSIVE_INPUT,
  PASSIVE_OUTPUT,
  PASSIVE_BIPASS;

  public static FaceMode of(int v) {
    return values()[v % values().length];
  }

  public boolean canExtract() {
    return this == ACTIVE_OUTPUT || this == PASSIVE_BIPASS || this == PASSIVE_OUTPUT;
  }

  public boolean canReceive() {
    return this == ACTIVE_INPUT || this == PASSIVE_BIPASS || this == PASSIVE_INPUT;
  }

  @Override
  public String createGroupKey() {
    return Ten4.lang("misc.facemode");
  }
}
