package com.hypothetic.ten4.lib.capability.internet;

import com.hypothetic.ten4.Ten4;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum ConnectionType implements StringRepresentable {
  NONE,
  NORMAL,
  PUSH,
  PULL;

  public static final ConnectionType[] VALUES = values();

  @Override
  public String toString() {
    return name().toLowerCase();
  }

  @Override
  public @NotNull String getSerializedName() {
    return toString();
  }

  public ConnectionType next() {
    return VALUES[(ordinal() + 1) % VALUES.length];
  }

  public boolean canAccept() {
    return this == NORMAL || this == PULL;
  }

  public boolean canSendTo() {
    return this == NORMAL || this == PUSH;
  }

  public MutableComponent getComponent() {
    return Component.translatable(Ten4.getLangKey("misc.connection_type." + this));
  }
}
