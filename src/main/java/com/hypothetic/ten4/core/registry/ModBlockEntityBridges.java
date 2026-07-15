package com.hypothetic.ten4.core.registry;

import com.hypothetic.ten4.api.registry.BlockEntityBridges;

public final class ModBlockEntityBridges {
  public static void createMapBeforeRegistry() {
    // Devices
    BlockEntityBridges.register(ModBlocks.PULVERIZER, ModBlockEntities.PULVERIZER);
    BlockEntityBridges.register(ModBlocks.SMELTER, ModBlockEntities.SMELTER);
    BlockEntityBridges.register(ModBlocks.WATER_PUMP, ModBlockEntities.WATER_PUMP);

    // Generators
    BlockEntityBridges.register(ModBlocks.HEAT_GENERATOR, ModBlockEntities.HEAT_GENERATOR);

    // Ducts
    BlockEntityBridges.register(ModBlocks.COPPER_ENERGY_DUCT, ModBlockEntities.COPPER_ENERGY_DUCT);
    BlockEntityBridges.register(ModBlocks.OPAQUE_COPPER_ENERGY_DUCT, ModBlockEntities.OPAQUE_COPPER_ENERGY_DUCT);
    BlockEntityBridges.register(ModBlocks.COPPER_CONTROLLER_ENERGY_DUCT, ModBlockEntities.COPPER_CONTROLLER_ENERGY_DUCT);
    BlockEntityBridges.register(ModBlocks.COPPER_ITEM_DUCT, ModBlockEntities.COPPER_ITEM_DUCT);
    BlockEntityBridges.register(ModBlocks.OPAQUE_COPPER_ITEM_DUCT, ModBlockEntities.OPAQUE_COPPER_ITEM_DUCT);
    BlockEntityBridges.register(ModBlocks.COPPER_CONTROLLER_ITEM_DUCT, ModBlockEntities.COPPER_CONTROLLER_ITEM_DUCT);
    BlockEntityBridges.register(ModBlocks.COPPER_FLUID_DUCT, ModBlockEntities.COPPER_FLUID_DUCT);
    BlockEntityBridges.register(ModBlocks.OPAQUE_COPPER_FLUID_DUCT, ModBlockEntities.OPAQUE_COPPER_FLUID_DUCT);
    BlockEntityBridges.register(ModBlocks.COPPER_CONTROLLER_FLUID_DUCT, ModBlockEntities.COPPER_CONTROLLER_FLUID_DUCT);
  }
}
