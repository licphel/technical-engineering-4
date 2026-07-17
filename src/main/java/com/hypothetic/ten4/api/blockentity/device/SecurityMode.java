package com.hypothetic.ten4.api.blockentity.device;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.IEnumTranslatable;

public enum SecurityMode implements IEnumTranslatable {
  DISABLED,
  PRIVATE;

  public static SecurityMode of(int v) {
    return values()[v % values().length];
  }

  @Override
  public String createGroupKey() {
    return Ten4.lang("misc.security_mode");
  }
}
