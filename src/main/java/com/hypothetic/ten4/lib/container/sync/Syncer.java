package com.hypothetic.ten4.lib.container.sync;

import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

import java.util.*;

public class Syncer {
  private final Set<SyncedField<?>> keys = new LinkedHashSet<>();
  private int[] data = new int[0];
  private SimpleContainerData containerData = new SimpleContainerData(0);
  private Map<String, Integer> nameToIndex = Map.of();
  private boolean sealed;

  public <T> void register(SyncedField<T> key) {
    if (sealed) {
      throw new IllegalStateException("Syncer already sealed");
    }
    keys.add(key);
  }

  public void seal() {
    if (sealed) {
      return;
    }
    sealed = true;

    List<SyncedField<?>> sorted = keys.stream().sorted(Comparator.comparing(SyncedField::name)).toList();
    nameToIndex = new HashMap<>();
    int idx = 0;
    for (SyncedField<?> key : sorted) {
      nameToIndex.put(key.name(), idx);
      idx += key.slots();
    }
    int size = idx;
    this.data = new int[size];
    this.containerData = new SimpleContainerData(size) {
      @Override
      public int get(int i) {
        return data[i];
      }

      @Override
      public void set(int i, int v) {
        data[i] = v;
      }
    };
  }

  public <T> void set(SyncedField<T> key, T value) {
    key.codec().write(data, nameToIndex.get(key.name()), value);
  }

  public <T> T get(SyncedField<T> key) {
    return key.codec().read(data, nameToIndex.get(key.name()));
  }

  public int getInt(SyncedField<Integer> k) {
    return k.codec().read(data, nameToIndex.get(k.name()));
  }

  public boolean getBool(SyncedField<Boolean> k) {
    return k.codec().read(data, nameToIndex.get(k.name()));
  }

  public ContainerData asContainerData() {
    return containerData;
  }

  public SyncedFieldReader createReader() {
    return new SyncedFieldReader(asContainerData(), nameToIndex);
  }
}
