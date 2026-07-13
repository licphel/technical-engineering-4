package com.hypothetic.ten4.registry;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.core.device.HeatGeneratorBlockEntity;
import com.hypothetic.ten4.core.device.PulverizerBlockEntity;
import com.hypothetic.ten4.api.blockentity.internet.EnergyDuctBlockEntity;
import com.hypothetic.ten4.api.blockentity.internet.FluidDuctBlockEntity;
import com.hypothetic.ten4.api.blockentity.internet.ItemDuctBlockEntity;
import com.hypothetic.ten4.core.device.WaterPumpBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("all")
public class ModBlockEntities {
  public static final DeferredRegister<BlockEntityType<?>> TILES =
      DeferredRegister.create(net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE, Ten4.ID);

  public static void trigger() {
    // just trigger the class loader
  }

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PulverizerBlockEntity>> PULVERIZER =
      TILES.register("pulverizer", () ->
          BlockEntityType.Builder.of(
              PulverizerBlockEntity::new,
              ModBlocks.PULVERIZER.get()).build(null));

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HeatGeneratorBlockEntity>> HEAT_GENERATOR =
      TILES.register("heat_generator", () ->
          BlockEntityType.Builder.of(
              HeatGeneratorBlockEntity::new,
              ModBlocks.HEAT_GENERATOR.get()).build(null));

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WaterPumpBlockEntity>> WATER_PUMP =
      TILES.register("water_pump", () ->
          BlockEntityType.Builder.of(
              WaterPumpBlockEntity::new,
              ModBlocks.WATER_PUMP.get()).build(null));

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyDuctBlockEntity>> COPPER_ENERGY_DUCT =
      TILES.register("glass_energy_cable", () ->
          BlockEntityType.Builder.of((pos, state) ->
              new EnergyDuctBlockEntity(pos, state, EnergyDuctBlockEntity.CAP_COPPER),
              ModBlocks.COPPER_ENERGY_DUCT.get()).build(null));

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemDuctBlockEntity>> COPPER_ITEM_DUCT =
      TILES.register("copper_item_duct", () ->
          BlockEntityType.Builder.of((pos, state) ->
                  new ItemDuctBlockEntity(pos, state, ModBlockEntities.COPPER_ITEM_DUCT.get(),
                      ItemDuctBlockEntity.TPB_COPPER, ItemDuctBlockEntity.SLOTS_COPPER, ItemDuctBlockEntity.CAP_COPPER),
              ModBlocks.COPPER_ITEM_DUCT.get()).build(null));

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidDuctBlockEntity>> COPPER_FLUID_DUCT =
      TILES.register("copper_fluid_duct", () ->
          BlockEntityType.Builder.of((pos, state) ->
              new FluidDuctBlockEntity(pos, state, FluidDuctBlockEntity.CAP_COPPER, FluidDuctBlockEntity.TPB_COPPER),
              ModBlocks.COPPER_FLUID_DUCT.get()).build(null));
}
