package com.hypothetic.ten4.lib.container.sync;

public sealed interface SyncedFieldCodec<T> permits SyncedFieldCodec.IntC, SyncedFieldCodec.BoolC, SyncedFieldCodec.FloatC {
  SyncedFieldCodec<Integer> INT = new IntC();
  SyncedFieldCodec<Boolean> BOOL = new BoolC();
  SyncedFieldCodec<Float> FLOAT = new FloatC();

  int slots();

  void write(int[] data, int idx, T val);

  T read(int[] data, int idx);

  record IntC() implements SyncedFieldCodec<Integer> {
    public int slots() {
      return 1;
    }

    public void write(int[] d, int i, Integer v) {
      d[i] = v;
    }

    public Integer read(int[] d, int i) {
      return d[i];
    }
  }

  record BoolC() implements SyncedFieldCodec<Boolean> {
    public int slots() {
      return 1;
    }

    public void write(int[] d, int i, Boolean v) {
      d[i] = v ? 1 : 0;
    }

    public Boolean read(int[] d, int i) {
      return d[i] != 0;
    }
  }

  record FloatC() implements SyncedFieldCodec<Float> {
    public int slots() {
      return 1;
    }

    public void write(int[] d, int i, Float v) {
      d[i] = Float.floatToIntBits(v);
    }

    public Float read(int[] d, int i) {
      return Float.intBitsToFloat(d[i]);
    }
  }
}
