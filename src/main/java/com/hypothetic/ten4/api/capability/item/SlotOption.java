package com.hypothetic.ten4.api.capability.item;

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
