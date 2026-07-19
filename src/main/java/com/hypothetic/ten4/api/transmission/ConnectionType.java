package com.hypothetic.ten4.api.transmission;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.IEnumTranslatable;
import net.minecraft.util.StringRepresentable;

public enum ConnectionType implements StringRepresentable, IEnumTranslatable {
  NONE,
  NORMAL,
  PUSH,
  PULL;

  public static ConnectionType of(int colorOrd) {
    return values()[colorOrd % values().length];
  }

  @Override
  public String createGroupKey() {
    return Ten4.lang("misc.connection_type");
  }

  public ConnectionType next() {
    return ConnectionType.of(ordinal() + 1);
  }

  public boolean isPullOrNormal() {
    return this == NORMAL || this == PULL;
  }

  public boolean isPushOrNormal() {
    return this == NORMAL || this == PUSH;
  }
}
