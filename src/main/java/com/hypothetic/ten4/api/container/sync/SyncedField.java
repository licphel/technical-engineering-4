package com.hypothetic.ten4.api.container.sync;

import static com.hypothetic.ten4.api.container.sync.SyncedFieldCodec.*;

/**
 * Typed attribute key identified purely by name — no unstable index.
 */
public final class SyncedField<T> {
  private final String name;
  private final SyncedFieldCodec<T> codec;

  private SyncedField(String name, SyncedFieldCodec<T> codec) {
    this.name = name;
    this.codec = codec;
  }

  public static SyncedField<Integer> ofInt(String n) {
    return new SyncedField<>(n, INT);
  }

  public static SyncedField<Boolean> ofBool(String n) {
    return new SyncedField<>(n, BOOL);
  }

  public static SyncedField<Float> ofFloat(String n) {
    return new SyncedField<>(n, FLOAT);
  }

  public String name() {
    return name;
  }

  public int slots() {
    return codec.slots();
  }

  SyncedFieldCodec<T> codec() {
    return codec;
  }
}
