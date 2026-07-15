package com.hypothetic.ten4.registry;

import com.hypothetic.ten4.Ten4;
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

  // Devices
  public static final DeferredHolder<Item, BlockItem> PULVERIZER = bridgeBlockItem(ModBlocks.PULVERIZER);
  public static final DeferredHolder<Item, BlockItem> SMELTER = bridgeBlockItem(ModBlocks.SMELTER);
  public static final DeferredHolder<Item, BlockItem> WATER_PUMP = bridgeBlockItem(ModBlocks.WATER_PUMP);

  // Generators
  public static final DeferredHolder<Item, BlockItem> HEAT_GENERATOR = bridgeBlockItem(ModBlocks.HEAT_GENERATOR);

  // Ducts
  public static final DeferredHolder<Item, BlockItem> COPPER_ENERGY_DUCT = bridgeBlockItem(ModBlocks.COPPER_ENERGY_DUCT);
  public static final DeferredHolder<Item, BlockItem> COPPER_CONTROLLER_ENERGY_DUCT = bridgeBlockItem(ModBlocks.COPPER_CONTROLLER_ENERGY_DUCT);
  public static final DeferredHolder<Item, BlockItem> COPPER_ITEM_DUCT = bridgeBlockItem(ModBlocks.COPPER_ITEM_DUCT);
  public static final DeferredHolder<Item, BlockItem> COPPER_CONTROLLER_ITEM_DUCT = bridgeBlockItem(ModBlocks.COPPER_CONTROLLER_ITEM_DUCT);
  public static final DeferredHolder<Item, BlockItem> COPPER_FLUID_DUCT = bridgeBlockItem(ModBlocks.COPPER_FLUID_DUCT);
  public static final DeferredHolder<Item, BlockItem> COPPER_CONTROLLER_FLUID_DUCT = bridgeBlockItem(ModBlocks.COPPER_CONTROLLER_FLUID_DUCT);

  // Materials
  public static final DeferredHolder<Item, Item> IRON_DUST = ITEMS.register("iron_dust", () -> new Item(new Item.Properties()));
  public static final DeferredHolder<Item, Item> IRON_PLATE = ITEMS.register("iron_plate", () -> new Item(new Item.Properties()));
  public static final DeferredHolder<Item, Item> COPPER_DUST = ITEMS.register("copper_dust", () -> new Item(new Item.Properties()));
  public static final DeferredHolder<Item, Item> COPPER_PLATE = ITEMS.register("copper_plate", () -> new Item(new Item.Properties()));
  public static final DeferredHolder<Item, Item> GOLD_DUST = ITEMS.register("gold_dust", () -> new Item(new Item.Properties()));
  public static final DeferredHolder<Item, Item> GOLDEN_PLATE = ITEMS.register("gold_plate", () -> new Item(new Item.Properties()));

  private ModItems() {
  }

  private static DeferredHolder<Item, BlockItem> bridgeBlockItem(DeferredHolder<Block, ? extends Block> blockHolder) {
    return ITEMS.register(blockHolder.getId().getPath(), () -> new BlockItem(BuiltInRegistries.BLOCK.get(blockHolder.getId()), new Item.Properties()));
  }

  public static void trigger() {
    // just trigger the class loader
  }
}
