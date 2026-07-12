package com.hypothetic.ten4.registry;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.core.item.PaintItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.Map;

@SuppressWarnings("unused")
public final class ModItems {
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Ten4.ID);

  public static final DeferredHolder<Item, Item> IRON_DUST = mat("iron_dust");
  public static final DeferredHolder<Item, Item> IRON_PLATE = mat("iron_plate");
  public static final DeferredHolder<Item, Item> COPPER_DUST = mat("copper_dust");
  public static final DeferredHolder<Item, Item> COPPER_PLATE = mat("copper_plate");
  public static final DeferredHolder<Item, Item> GOLD_DUST = mat("gold_dust");
  public static final DeferredHolder<Item, Item> GOLDEN_PLATE = mat("gold_plate");

  public static final DeferredHolder<Item, Item> SPONGE = ITEMS.register("sponge", () -> new PaintItem(null));
   static {
     ModCreativeTabs.materialTab.add(SPONGE);
   }
  public static final Map<DyeColor, DeferredHolder<Item, Item>> PAINTS = new EnumMap<>(DyeColor.class);
  static {
    for (DyeColor color : DyeColor.values()) {
      PAINTS.put(color, paint(color));
    }
  }

  private ModItems() {
  }

  private static DeferredHolder<Item, Item> mat(String name) {
    DeferredHolder<Item, Item> i = ITEMS.register(name, () -> new Item(new Item.Properties()));
    ModCreativeTabs.materialTab.add(i);
    return i;
  }

  private static DeferredHolder<Item, Item> paint(DyeColor color) {
    DeferredHolder<Item, Item> i = ITEMS.register(color.getName() + "_paint", () -> new PaintItem(color));
    ModCreativeTabs.materialTab.add(i);
    return i;
  }

  public static void trigger() {
    // just trigger the class loader
  }
}
