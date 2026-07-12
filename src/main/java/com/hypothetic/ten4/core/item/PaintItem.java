package com.hypothetic.ten4.core.item;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

public class PaintItem extends Item {
  private final @Nullable DyeColor color;

  public PaintItem(@Nullable DyeColor color) {
    super(new Properties().stacksTo(64));
    this.color = color;
  }

  public @Nullable DyeColor getColor() {
    return color;
  }
}
