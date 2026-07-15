package com.hypothetic.ten4.api.blockentity.device;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.IEnumTranslatable;

public enum ComparatorMode implements IEnumTranslatable {
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
    return Ten4.lang("misc.comparator_mode");
  }
}
