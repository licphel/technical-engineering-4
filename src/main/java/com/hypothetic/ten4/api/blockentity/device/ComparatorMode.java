package com.hypothetic.ten4.api.blockentity.device;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.ITranslatable;

public enum ComparatorMode implements ITranslatable {
  OFF,
  ENERGY,
  OUTPUT_ITEMS,
  OUTPUT_FLUID,
  ACTIVE;

  public static ComparatorMode of(int v) {
    return values()[v % values().length];
  }

  @Override
  public String createGroupKey() {
    return Ten4.getLangKey("misc.comparator_mode");
  }
}
