package com.hypothetic.ten4.core.item;

import net.minecraft.world.item.Item;

public final class ItemProperties {
  private ItemProperties() {
  }

  public static final Item.Properties NONSPECIAL = new Item.Properties();
  public static final Item.Properties WRENCH = new Item.Properties().stacksTo(1);
  public static final Item.Properties AUGMENT = new Item.Properties().stacksTo(4);
}
