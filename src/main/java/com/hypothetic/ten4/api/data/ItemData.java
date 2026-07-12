package com.hypothetic.ten4.api.data;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ItemData {
  final DeferredHolder<Item, ? extends Item> entry;
  String enName, zhName;
  String modelPath;

  ItemData(DeferredHolder<Item, ? extends Item> e) {
    this.entry = e;
  }

  public ItemData enName(String s) {
    enName = s;
    return this;
  }

  public ItemData zhName(String s) {
    zhName = s;
    return this;
  }

  public ItemData model(String path) {
    modelPath = path;
    return this;
  }
}
