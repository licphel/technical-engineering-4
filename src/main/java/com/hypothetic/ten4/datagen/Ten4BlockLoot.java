package com.hypothetic.ten4.datagen;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.core.registry.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.block.Block;

import java.util.Collections;

public class Ten4BlockLoot extends BlockLootSubProvider {
  protected Ten4BlockLoot(HolderLookup.Provider registries) {
    super(Collections.emptySet(), FeatureFlagSet.of(), registries);
  }

  @Override
  protected void generate() {
    dropSelf(ModBlocks.DEVICE_CASING.get());
    dropSelf(ModBlocks.PULVERIZER.get());
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
  protected Iterable<Block> getKnownBlocks() {
    return BuiltInRegistries.BLOCK.stream()
        .filter(b -> Ten4.ID.equals(BuiltInRegistries.BLOCK.getKey(b).getNamespace()))
        .toList();
  }
}
