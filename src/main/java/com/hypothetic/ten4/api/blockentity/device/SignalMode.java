package com.hypothetic.ten4.api.blockentity.device;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.ITranslatable;

public enum SignalMode implements ITranslatable {
  IGNORE,
  LOW_LEVEL,
  HIGH_LEVEL;

  public static SignalMode of(int v) {
    return values()[v % values().length];
  }

  @Override
  public String createGroupKey() {
    return Ten4.getLangKey("misc.sigmode");
  }
}
