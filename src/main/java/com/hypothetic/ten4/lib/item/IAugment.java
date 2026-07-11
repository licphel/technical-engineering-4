package com.hypothetic.ten4.lib.item;

import com.hypothetic.ten4.lib.blockentity.device.AugmentableDeviceBlockEntity;

public interface IAugment<T extends AugmentableDeviceBlockEntity> {
  default boolean isValidFor(T entity) {
    return true;
  }

  default void apply(T entity) {
  }

  default int modifier(ModifiableEntry entry, int value) {
    return value;
  }

  enum ModifiableEntry {
    EFFICIENCY,
    MAX_ENERGY_EXTRACT,
    MAX_ENERGY_RECEIVE,
    ENERGY_CAPACITY,
    MAX_ITEM_EXTRACT,
    MAX_ITEM_RECEIVE,
    MAX_FLUID_EXTRACT,
    MAX_FLUID_RECEIVE
  }
}
