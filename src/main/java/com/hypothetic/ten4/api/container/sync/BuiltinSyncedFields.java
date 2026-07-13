package com.hypothetic.ten4.api.container.sync;

public final class BuiltinSyncedFields {
  public static final SyncedField<Integer> ENERGY = SyncedField.ofInt("energy");
  public static final SyncedField<Integer> MAX_ENERGY = SyncedField.ofInt("max_energy");
  public static final SyncedField<Integer> PROGRESS = SyncedField.ofInt("progress");
  public static final SyncedField<Integer> MAX_PROGRESS = SyncedField.ofInt("max_progress");
  public static final SyncedField<Integer> FUEL = SyncedField.ofInt("fuel");
  public static final SyncedField<Integer> MAX_FUEL = SyncedField.ofInt("max_fuel");
  public static final SyncedField<Integer> POWER = SyncedField.ofInt("power");
  public static final SyncedField<Integer> ENERGY_THROUGHPUT = SyncedField.ofInt("energy_throughput");
  public static final SyncedField<Integer> ITEM_THROUGHPUT = SyncedField.ofInt("item_throughput");
  public static final SyncedField<Integer> FLUID_THROUGHPUT = SyncedField.ofInt("fluid_throughput");
  public static final SyncedField<Boolean> ACTIVE = SyncedField.ofBool("active");
  public static final SyncedField<Integer> SIG_MODE = SyncedField.ofInt("signal_mode");
  public static final SyncedField<Integer> ENERGY_FACES = SyncedField.ofInt("energy_faces");
  public static final SyncedField<Integer> ITEM_FACES = SyncedField.ofInt("item_faces");
  public static final SyncedField<Integer> FLUID_FACES = SyncedField.ofInt("fluid_faces");
  public static final SyncedField<Boolean> STRICT_INPUT = SyncedField.ofBool("strict_input");
  public static final SyncedField<Integer> COMPARATOR_MODE = SyncedField.ofInt("comparator_mode");
  public static final SyncedField<Integer> REQUEST_INTERVAL = SyncedField.ofInt("request_interval");

  private BuiltinSyncedFields() {
  }
}
