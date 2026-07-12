package com.hypothetic.ten4.api.container;

import java.util.ArrayList;
import java.util.List;

public class ContainerMenuLayout {
  private final List<int[]> slots = new ArrayList<>();

  public ContainerMenuLayout() {
  }

  public ContainerMenuLayout add(int id, int x, int y) {
    slots.add(new int[] {id, x, y});
    return this;
  }

  public List<int[]> getSlots() {
    return slots;
  }
}
