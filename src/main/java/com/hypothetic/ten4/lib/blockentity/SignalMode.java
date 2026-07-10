package com.hypothetic.ten4.lib.blockentity;

public enum SignalMode {
  IGNORE,
  LOW,
  HIGH;

  @Override
  public String toString() {
    return switch (this) {
      case IGNORE -> "ignore";
      case LOW -> "low";
      case HIGH -> "high";
    };
  }
}
