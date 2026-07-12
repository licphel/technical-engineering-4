package com.hypothetic.ten4.api.container.sync;

import net.minecraft.world.inventory.ContainerData;

import java.util.Map;

public class SyncedFieldReader {
  private final ContainerData data;
  private final Map<String, Integer> nameToIndex;

  public SyncedFieldReader(ContainerData data, Map<String, Integer> nameToIndex) {
    this.data = data;
    this.nameToIndex = nameToIndex;
  }

  public <T> T get(SyncedField<T> key) {
    int idx = nameToIndex.get(key.name());
    return key.codec().read(readBacking(), idx);
  }

  public int getInt(SyncedField<Integer> k) {
    return get(k);
  }

  public boolean getBool(SyncedField<Boolean> k) {
    return get(k);
  }

  public float getFloat(SyncedField<Float> k) {
    return get(k);
  }

  private int[] readBacking() {
    int n = data.getCount();
    int[] arr = new int[n];
    for (int i = 0; i < n; i++) {
      arr[i] = data.get(i);
    }
    return arr;
  }
}
