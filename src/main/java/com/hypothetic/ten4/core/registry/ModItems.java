package com.hypothetic.ten4.core.registry;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.item.IAugment;
import com.hypothetic.ten4.core.item.GeneralAugmentItem;
import com.hypothetic.ten4.core.item.ItemProperties;
import com.hypothetic.ten4.core.item.WrenchItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class ModItems {
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Ten4.ID);

  public static final DeferredHolder<Item, Item> WRENCH = ITEMS.register("wrench", () -> new WrenchItem(ItemProperties.WRENCH));

  // DeviceTiers
  public static final DeferredHolder<Item, BlockItem> DEVICE_CASING = bridgeBlockItem(ModBlocks.DEVICE_CASING);
  public static final DeferredHolder<Item, BlockItem> PULVERIZER = bridgeBlockItem(ModBlocks.PULVERIZER);
  public static final DeferredHolder<Item, BlockItem> PRESS = bridgeBlockItem(ModBlocks.PRESS);
  public static final DeferredHolder<Item, BlockItem> SMELTER = bridgeBlockItem(ModBlocks.SMELTER);
  public static final DeferredHolder<Item, BlockItem> REFINER = bridgeBlockItem(ModBlocks.REFINER);
  public static final DeferredHolder<Item, BlockItem> WATER_PUMP = bridgeBlockItem(ModBlocks.WATER_PUMP);

  // Generators
  public static final DeferredHolder<Item, BlockItem> HEAT_GENERATOR = bridgeBlockItem(ModBlocks.HEAT_GENERATOR);

  // DuctTiers
  public static final DeferredHolder<Item, BlockItem> COPPER_ENERGY_DUCT = bridgeBlockItem(ModBlocks.COPPER_ENERGY_DUCT);
  public static final DeferredHolder<Item, BlockItem> OPAQUE_COPPER_ENERGY_DUCT = bridgeBlockItem(ModBlocks.OPAQUE_COPPER_ENERGY_DUCT);
  public static final DeferredHolder<Item, BlockItem> COPPER_CONTROLLER_ENERGY_DUCT = bridgeBlockItem(ModBlocks.COPPER_CONTROLLER_ENERGY_DUCT);
  public static final DeferredHolder<Item, BlockItem> COPPER_ITEM_DUCT = bridgeBlockItem(ModBlocks.COPPER_ITEM_DUCT);
  public static final DeferredHolder<Item, BlockItem> OPAQUE_COPPER_ITEM_DUCT = bridgeBlockItem(ModBlocks.OPAQUE_COPPER_ITEM_DUCT);
  public static final DeferredHolder<Item, BlockItem> COPPER_CONTROLLER_ITEM_DUCT = bridgeBlockItem(ModBlocks.COPPER_CONTROLLER_ITEM_DUCT);
  public static final DeferredHolder<Item, BlockItem> COPPER_FLUID_DUCT = bridgeBlockItem(ModBlocks.COPPER_FLUID_DUCT);
  public static final DeferredHolder<Item, BlockItem> OPAQUE_COPPER_FLUID_DUCT = bridgeBlockItem(ModBlocks.OPAQUE_COPPER_FLUID_DUCT);
  public static final DeferredHolder<Item, BlockItem> COPPER_CONTROLLER_FLUID_DUCT = bridgeBlockItem(ModBlocks.COPPER_CONTROLLER_FLUID_DUCT);

  // Other Blocks
  public static final DeferredHolder<Item, BlockItem> ASPHALT = bridgeBlockItem(ModBlocks.ASPHALT);

  // Ore & Storage BlockItems
  public static final DeferredHolder<Item, BlockItem> TIN_ORE = bridgeBlockItem(ModBlocks.TIN_ORE);
  public static final DeferredHolder<Item, BlockItem> DEEPSLATE_TIN_ORE = bridgeBlockItem(ModBlocks.DEEPSLATE_TIN_ORE);
  public static final DeferredHolder<Item, BlockItem> TIN_BLOCK = bridgeBlockItem(ModBlocks.TIN_BLOCK);
  public static final DeferredHolder<Item, BlockItem> RAW_TIN_BLOCK = bridgeBlockItem(ModBlocks.RAW_TIN_BLOCK);
  public static final DeferredHolder<Item, BlockItem> TITANIUM_ORE = bridgeBlockItem(ModBlocks.TITANIUM_ORE);
  public static final DeferredHolder<Item, BlockItem> DEEPSLATE_TITANIUM_ORE = bridgeBlockItem(ModBlocks.DEEPSLATE_TITANIUM_ORE);
  public static final DeferredHolder<Item, BlockItem> TITANIUM_BLOCK = bridgeBlockItem(ModBlocks.TITANIUM_BLOCK);
  public static final DeferredHolder<Item, BlockItem> RAW_TITANIUM_BLOCK = bridgeBlockItem(ModBlocks.RAW_TITANIUM_BLOCK);
  public static final DeferredHolder<Item, BlockItem> MONAZITE_ORE = bridgeBlockItem(ModBlocks.MONAZITE_ORE);
  public static final DeferredHolder<Item, BlockItem> SULFUR_ORE = bridgeBlockItem(ModBlocks.SULFUR_ORE);
  public static final DeferredHolder<Item, BlockItem> OIL_SAND = bridgeBlockItem(ModBlocks.OIL_SAND);
  public static final DeferredHolder<Item, BlockItem> BORAX_BLOCK = bridgeBlockItem(ModBlocks.BORAX_BLOCK);

  // Dies
  public static final DeferredHolder<Item, Item> SHEET_DIE = ITEMS.register("sheet_die", () -> new Item(ItemProperties.SINGLE_STACKED));
  public static final DeferredHolder<Item, Item> PACKING_DIE = ITEMS.register("packing_die", () -> new Item(ItemProperties.SINGLE_STACKED));
  public static final DeferredHolder<Item, Item> DEPACKING_DIE = ITEMS.register("depacking_die", () -> new Item(ItemProperties.SINGLE_STACKED));

  // Materials
  public static final DeferredHolder<Item, Item> CIRCUIT = ITEMS.register("circuit", () -> new Item(ItemProperties.NONSPECIAL));
  public static final DeferredHolder<Item, Item> IRON_DUST = ITEMS.register("iron_dust", () -> new Item(ItemProperties.NONSPECIAL));
  public static final DeferredHolder<Item, Item> IRON_PLATE = ITEMS.register("iron_plate", () -> new Item(ItemProperties.NONSPECIAL));
  public static final DeferredHolder<Item, Item> COPPER_NUGGET = ITEMS.register("copper_nugget", () -> new Item(ItemProperties.NONSPECIAL));
  public static final DeferredHolder<Item, Item> COPPER_DUST = ITEMS.register("copper_dust", () -> new Item(ItemProperties.NONSPECIAL));
  public static final DeferredHolder<Item, Item> COPPER_PLATE = ITEMS.register("copper_plate", () -> new Item(ItemProperties.NONSPECIAL));
  public static final DeferredHolder<Item, Item> GOLD_DUST = ITEMS.register("gold_dust", () -> new Item(ItemProperties.NONSPECIAL));
  public static final DeferredHolder<Item, Item> GOLDEN_PLATE = ITEMS.register("gold_plate", () -> new Item(ItemProperties.NONSPECIAL));
  public static final DeferredHolder<Item, Item> RAW_TIN = ITEMS.register("raw_tin", () -> new Item(ItemProperties.NONSPECIAL));
  public static final DeferredHolder<Item, Item> TIN_INGOT = ITEMS.register("tin_ingot", () -> new Item(ItemProperties.NONSPECIAL));
  public static final DeferredHolder<Item, Item> TIN_NUGGET = ITEMS.register("tin_nugget", () -> new Item(ItemProperties.NONSPECIAL));
  public static final DeferredHolder<Item, Item> TIN_DUST = ITEMS.register("tin_dust", () -> new Item(ItemProperties.NONSPECIAL));
  public static final DeferredHolder<Item, Item> TIN_PLATE = ITEMS.register("tin_plate", () -> new Item(ItemProperties.NONSPECIAL));
  public static final DeferredHolder<Item, Item> RAW_TITANIUM = ITEMS.register("raw_titanium", () -> new Item(ItemProperties.NONSPECIAL));
  public static final DeferredHolder<Item, Item> TITANIUM_INGOT = ITEMS.register("titanium_ingot", () -> new Item(ItemProperties.NONSPECIAL));
  public static final DeferredHolder<Item, Item> TITANIUM_NUGGET = ITEMS.register("titanium_nugget", () -> new Item(ItemProperties.NONSPECIAL));
  public static final DeferredHolder<Item, Item> TITANIUM_DUST = ITEMS.register("titanium_dust", () -> new Item(ItemProperties.NONSPECIAL));
  public static final DeferredHolder<Item, Item> TITANIUM_PLATE = ITEMS.register("titanium_plate", () -> new Item(ItemProperties.NONSPECIAL));
  public static final DeferredHolder<Item, Item> BORAX = ITEMS.register("borax", () -> new Item(ItemProperties.NONSPECIAL));
  public static final DeferredHolder<Item, Item> BORAX_DUST = ITEMS.register("borax_dust", () -> new Item(ItemProperties.NONSPECIAL));
  public static final DeferredHolder<Item, Item> MONAZITE = ITEMS.register("monazite", () -> new Item(ItemProperties.NONSPECIAL));
  public static final DeferredHolder<Item, Item> MONAZITE_DUST = ITEMS.register("monazite_dust", () -> new Item(ItemProperties.NONSPECIAL));
  public static final DeferredHolder<Item, Item> SULFUR_DUST = ITEMS.register("sulfur_dust", () -> new Item(ItemProperties.NONSPECIAL));

  private ModItems() {
  }

  private static DeferredHolder<Item, BlockItem> bridgeBlockItem(DeferredHolder<Block, ? extends Block> blockHolder) {
    return ITEMS.register(blockHolder.getId().getPath(), () -> new BlockItem(BuiltInRegistries.BLOCK.get(blockHolder.getId()), ItemProperties.NONSPECIAL));
  }

  public static void trigger() {
    // just trigger the class loader
  }
}
