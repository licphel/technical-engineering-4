package com.hypothetic.ten4.datagen.loot;

import com.hypothetic.ten4.core.registry.ModBlocks;
import com.hypothetic.ten4.core.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BlockLootData extends BlockLootSubProvider {
  private final Set<Block> knownBlocks = new HashSet<>();

  public BlockLootData(HolderLookup.Provider registries) {
    super(Collections.emptySet(), FeatureFlags.VANILLA_SET, registries);
  }

  @Override
  protected void generate() {
    // Ores
    add(ModBlocks.TIN_ORE.get(), createOreDrop(ModBlocks.TIN_ORE.get(), ModItems.RAW_TIN.get()));
    add(ModBlocks.DEEPSLATE_TIN_ORE.get(), createOreDrop(ModBlocks.DEEPSLATE_TIN_ORE.get(), ModItems.RAW_TIN.get()));
    add(ModBlocks.TITANIUM_ORE.get(), createOreDrop(ModBlocks.TITANIUM_ORE.get(), ModItems.RAW_TITANIUM.get()));
    add(ModBlocks.DEEPSLATE_TITANIUM_ORE.get(), createOreDrop(ModBlocks.DEEPSLATE_TITANIUM_ORE.get(), ModItems.RAW_TITANIUM.get()));
    add(ModBlocks.MONAZITE_ORE.get(), createOreDrop(ModBlocks.MONAZITE_ORE.get(), ModItems.MONAZITE.get()));
    add(ModBlocks.SULFUR_ORE.get(), createOreDrop(ModBlocks.SULFUR_ORE.get(), ModItems.SULFUR_DUST.get()));
    dropSelf(ModBlocks.BORAX_BLOCK.get());
    dropSelf(ModBlocks.OIL_SAND.get());

    // Storage blocks
    dropSelf(ModBlocks.TIN_BLOCK.get());
    dropSelf(ModBlocks.RAW_TIN_BLOCK.get());
    dropSelf(ModBlocks.TITANIUM_BLOCK.get());
    dropSelf(ModBlocks.RAW_TITANIUM_BLOCK.get());

    // Devices
    dropSelf(ModBlocks.DEVICE_CASING.get());
    dropSelf(ModBlocks.PULVERIZER.get());
    dropSelf(ModBlocks.PRESS.get());
    dropSelf(ModBlocks.SMELTER.get());
    dropSelf(ModBlocks.REFINER.get());
    dropSelf(ModBlocks.WATER_PUMP.get());
    dropSelf(ModBlocks.HEAT_GENERATOR.get());

    // Ducts
    dropSelf(ModBlocks.COPPER_ENERGY_DUCT.get());
    dropSelf(ModBlocks.OPAQUE_COPPER_ENERGY_DUCT.get());
    dropSelf(ModBlocks.COPPER_CONTROLLER_ENERGY_DUCT.get());
    dropSelf(ModBlocks.COPPER_ITEM_DUCT.get());
    dropSelf(ModBlocks.OPAQUE_COPPER_ITEM_DUCT.get());
    dropSelf(ModBlocks.COPPER_CONTROLLER_ITEM_DUCT.get());
    dropSelf(ModBlocks.COPPER_FLUID_DUCT.get());
    dropSelf(ModBlocks.OPAQUE_COPPER_FLUID_DUCT.get());
    dropSelf(ModBlocks.COPPER_CONTROLLER_FLUID_DUCT.get());
  }

  @Override
  protected void add(Block block, LootTable.Builder table) {
    super.add(block, table);
    knownBlocks.add(block);
  }

  @Override
  protected Iterable<Block> getKnownBlocks() {
    return knownBlocks;
  }
}
