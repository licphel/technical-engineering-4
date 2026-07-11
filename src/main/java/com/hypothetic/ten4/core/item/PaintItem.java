package com.hypothetic.ten4.core.item;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

public class PaintItem extends Item {
  private final DyeColor color;

  public PaintItem(DyeColor color) {
    super(new Properties().stacksTo(64));
    this.color = color;
  }

  public DyeColor getColor() {
    return color;
  }
}
