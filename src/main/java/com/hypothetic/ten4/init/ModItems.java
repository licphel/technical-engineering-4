package com.hypothetic.ten4.init;

import com.hypothetic.ten4.Ten4;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class ModItems {
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Ten4.ID);

  public static final DeferredHolder<Item, Item> IRON_DUST = mat("iron_dust");
  public static final DeferredHolder<Item, Item> IRON_PLATE = mat("iron_plate");
  public static final DeferredHolder<Item, Item> COPPER_DUST = mat("copper_dust");
  public static final DeferredHolder<Item, Item> COPPER_PLATE = mat("copper_plate");
  public static final DeferredHolder<Item, Item> GOLD_DUST = mat("gold_dust");
  public static final DeferredHolder<Item, Item> GOLDEN_PLATE = mat("golden_plate");

  public static final DeferredHolder<Item, Item> TIN_INGOT = mat("tin_ingot");
  public static final DeferredHolder<Item, Item> TIN_DUST = mat("tin_dust");
  public static final DeferredHolder<Item, Item> TIN_PLATE = mat("tin_plate");

  public static final DeferredHolder<Item, Item> NICKEL_INGOT = mat("nickel_ingot");
  public static final DeferredHolder<Item, Item> NICKEL_DUST = mat("nickel_dust");
  public static final DeferredHolder<Item, Item> NICKEL_PLATE = mat("nickel_plate");

  public static final DeferredHolder<Item, Item> POWERED_TIN_INGOT = mat("powered_tin_ingot");
  public static final DeferredHolder<Item, Item> POWERED_TIN_DUST = mat("powered_tin_dust");
  public static final DeferredHolder<Item, Item> POWERED_TIN_PLATE = mat("powered_tin_plate");

  public static final DeferredHolder<Item, Item> RAW_TIN = mat("raw_tin");
  public static final DeferredHolder<Item, Item> RAW_NICKEL = mat("raw_nickel");

  private ModItems() {
  }

  private static DeferredHolder<Item, Item> mat(String name) {
    DeferredHolder<Item, Item> i = ITEMS.register(name, () -> new Item(new Item.Properties()));
    ModCreativeTabs.materialTab.add(i);
    return i;
  }

  public static void trigger() {
    // just trigger the class loader
  }
}
