package com.hypothetic.ten4.lib.container.sync;

public final class BuiltinSyncedFields {
    private BuiltinSyncedFields() {}
  
    public static final SyncedField<Integer> ENERGY = SyncedField.ofInt("energy");
    public static final SyncedField<Integer> MAX_ENERGY = SyncedField.ofInt("max_energy");
    public static final SyncedField<Integer> PROGRESS = SyncedField.ofInt("progress");
    public static final SyncedField<Integer> MAX_PROGRESS = SyncedField.ofInt("max_progress");
    public static final SyncedField<Integer> FUEL = SyncedField.ofInt("fuel");
    public static final SyncedField<Integer> MAX_FUEL = SyncedField.ofInt("max_fuel");
    public static final SyncedField<Integer> EFFICIENCY = SyncedField.ofInt("efficiency");
    public static final SyncedField<Boolean> ACTIVE = SyncedField.ofBool("active");
    public static final SyncedField<Integer> SIG_MODE = SyncedField.ofInt("signal_mode");
    // 6 sides × 3 bits per side (FaceMode ordinal 0-5)
    public static final SyncedField<Integer> ENERGY_FACES = SyncedField.ofInt("energy_faces");
    public static final SyncedField<Integer> ITEM_FACES = SyncedField.ofInt("item_faces");
    public static final SyncedField<Integer> FLUID_FACES = SyncedField.ofInt("fluid_faces");
}
