package com.hypothetic.ten4.core.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public final class ItemProperties {
  public static final Item.Properties NONSPECIAL = new Item.Properties();
  public static final Item.Properties SINGLE_STACKED = new Item.Properties().stacksTo(1);
  public static final Item.Properties BUCKET = new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET);
  public static final Item.Properties WRENCH = new Item.Properties().stacksTo(1);
  public static final Item.Properties AUGMENT = new Item.Properties().stacksTo(4);

  private ItemProperties() {
  }
}
