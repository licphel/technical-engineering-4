package com.hypothetic.ten4.api.blockentity.device;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.IEnumTranslatable;

public enum FaceMode implements IEnumTranslatable {
  NONE,
  INPUT,
  OUTPUT,
  BIPASS;

  public static FaceMode of(int v) {
    return values()[v % values().length];
  }

  public boolean canExtract() {
    return this == OUTPUT || this == BIPASS;
  }

  public boolean canReceive() {
    return this == INPUT || this == BIPASS;
  }

  @Override
  public String createGroupKey() {
    return Ten4.lang("misc.facemode");
  }
}
