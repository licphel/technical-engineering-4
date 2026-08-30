package com.hypothetic.ten4.core.registry;

import com.hypothetic.ten4.api.registry.BlockEntityBridges;

public final class ModBlockEntityBridges {
  public static void createMapBeforeRegistry() {
    // DeviceTiers
    BlockEntityBridges.register(ModBlocks.PULVERIZER, ModBlockEntities.PULVERIZER);
    BlockEntityBridges.register(ModBlocks.PRESS, ModBlockEntities.PRESS);
    BlockEntityBridges.register(ModBlocks.SMELTER, ModBlockEntities.SMELTER);
    BlockEntityBridges.register(ModBlocks.REFINER, ModBlockEntities.REFINER);
    BlockEntityBridges.register(ModBlocks.WATER_PUMP, ModBlockEntities.WATER_PUMP);

    // Generators
    BlockEntityBridges.register(ModBlocks.HEAT_GENERATOR, ModBlockEntities.HEAT_GENERATOR);
    BlockEntityBridges.register(ModBlocks.FUEL_GENERATOR, ModBlockEntities.FUEL_GENERATOR);

    // Storage
    BlockEntityBridges.register(ModBlocks.LIQUID_UNIT, ModBlockEntities.LIQUID_UNIT);
    BlockEntityBridges.register(ModBlocks.ENERGY_UNIT, ModBlockEntities.ENERGY_UNIT);

    // Utility
    BlockEntityBridges.register(ModBlocks.VOID_UNIT, ModBlockEntities.VOID_UNIT);

    // DuctTiers
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
