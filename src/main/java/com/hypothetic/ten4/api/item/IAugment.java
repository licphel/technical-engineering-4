package com.hypothetic.ten4.api.item;

import com.hypothetic.ten4.api.blockentity.device.AugmentableDeviceBlockEntity;

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
    ENERGY_THROUGHPUT,
    MAX_ENERGY_RECEIVE,
    ENERGY_CAPACITY,
    ITEM_THROUGHPUT,
    MAX_ITEM_RECEIVE,
    FLUID_THROUGHPUT,
    MAX_FLUID_RECEIVE
  }
}
