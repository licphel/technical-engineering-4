package com.hypothetic.ten4.datagen.loot;

import com.hypothetic.ten4.core.registry.ModBlocks;
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
    dropSelf(ModBlocks.DEVICE_CASING.get());
    dropSelf(ModBlocks.PULVERIZER.get());
    dropSelf(ModBlocks.PRESS.get());
    dropSelf(ModBlocks.SMELTER.get());
    dropSelf(ModBlocks.WATER_PUMP.get());
    dropSelf(ModBlocks.HEAT_GENERATOR.get());

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
