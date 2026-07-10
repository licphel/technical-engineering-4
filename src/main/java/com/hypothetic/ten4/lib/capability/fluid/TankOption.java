package com.hypothetic.ten4.lib.capability.fluid;

public enum TankOption {
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
