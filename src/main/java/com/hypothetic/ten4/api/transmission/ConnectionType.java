package com.hypothetic.ten4.api.transmission;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.ITranslatable;
import net.minecraft.util.StringRepresentable;

public enum ConnectionType implements StringRepresentable, ITranslatable {
  NONE,
  NORMAL,
  PUSH,
  PULL;

  public static ConnectionType of(int colorOrd) {
    return values()[colorOrd % values().length];
  }

  @Override
  public String createGroupKey() {
    return Ten4.getLangKey("misc.connection_type");
  }

  public ConnectionType next() {
    return ConnectionType.of(ordinal() + 1);
  }

  public boolean canAccept() {
    return this == NORMAL || this == PULL;
  }

  public boolean canBorrow() {
    return this == NORMAL || this == PUSH;
  }
}
