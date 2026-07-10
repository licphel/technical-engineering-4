package com.hypothetic.ten4.lib.capability.item;

public enum SlotOption {
  INPUT,
  OUTPUT,
  BOTH;

  public boolean canReceive() {
    return this == INPUT || this == BOTH;
  }

  public boolean canExtract() {
    return this == OUTPUT || this == BOTH;
  }
}
