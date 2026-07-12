package com.hypothetic.ten4.api.capability.fluid;

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
