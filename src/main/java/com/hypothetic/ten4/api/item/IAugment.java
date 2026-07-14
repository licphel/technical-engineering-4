package com.hypothetic.ten4.api.item;

import com.hypothetic.ten4.api.blockentity.device.AugmentableDeviceBlockEntity;

public interface IAugment<T extends AugmentableDeviceBlockEntity> {
  default boolean isValidFor(T entity) {
    return true;
  }

  default void apply(T entity) {
  }

  default int modifier(AugmentableField field, int value) {
    return value;
  }

  enum AugmentableField {
    EFFICIENCY,
    ENERGY_THROUGHPUT,
    ENERGY_CAPACITY,
    ITEM_THROUGHPUT,
    FLUID_THROUGHPUT
  }
}
