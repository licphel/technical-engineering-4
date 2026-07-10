package com.hypothetic.ten4.lib.blockentity;

import java.util.List;

public enum FaceMode {
  OFF,
  ACTIVE_RECEIVE,
  ACTIVE_EXTRACT,
  PASSIVE_RECEIVE,
  PASSIVE_EXTRACT,
  PASSIVE_BOTH;

  public static final List<FaceMode> EXTRACTABLE = List.of(ACTIVE_EXTRACT, PASSIVE_EXTRACT, PASSIVE_BOTH);
  public static final List<FaceMode> RECEIVABLE = List.of(ACTIVE_RECEIVE, PASSIVE_RECEIVE, PASSIVE_BOTH);

  public String toString() {
    return switch (this) {
      case OFF -> "off";
      case ACTIVE_RECEIVE -> "active_receive";
      case ACTIVE_EXTRACT -> "active_extract";
      case PASSIVE_RECEIVE -> "passive_receive";
      case PASSIVE_EXTRACT -> "passive_extract";
      case PASSIVE_BOTH -> "passive";
    };
  }

  public boolean isPassive() {
    return this == OFF || this == PASSIVE_BOTH || this == PASSIVE_RECEIVE || this == PASSIVE_EXTRACT;
  }

  public boolean isIn() {
    return this == ACTIVE_RECEIVE || this == PASSIVE_RECEIVE;
  }

  public boolean isOut() {
    return this == ACTIVE_EXTRACT || this == PASSIVE_EXTRACT;
  }

  public boolean canExtract() {
    return this == ACTIVE_EXTRACT || this == PASSIVE_BOTH || this == PASSIVE_EXTRACT;
  }

  public boolean canReceive() {
    return this == ACTIVE_RECEIVE || this == PASSIVE_BOTH || this == PASSIVE_RECEIVE;
  }
}
