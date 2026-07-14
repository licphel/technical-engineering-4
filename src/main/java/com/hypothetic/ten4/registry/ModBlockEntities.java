package com.hypothetic.ten4.registry;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.core.blockentity.HeatGeneratorBlockEntity;
import com.hypothetic.ten4.core.blockentity.PulverizerBlockEntity;
import com.hypothetic.ten4.core.blockentity.WaterPumpBlockEntity;
import com.hypothetic.ten4.core.blockentity.duct.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
  public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Ten4.ID);

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CopperEnergyDuctBlockEntity>>
      COPPER_ENERGY_DUCT =
      register("copper_energy_duct", ModBlocks.COPPER_ENERGY_DUCT, CopperEnergyDuctBlockEntity::new);
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CopperItemDuctBlockEntity>>
      COPPER_ITEM_DUCT =
      register("copper_item_duct", ModBlocks.COPPER_ITEM_DUCT, CopperItemDuctBlockEntity::new);
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CopperFluidDuctBlockEntity>>
      COPPER_FLUID_DUCT =
      register("copper_fluid_duct", ModBlocks.COPPER_FLUID_DUCT, CopperFluidDuctBlockEntity::new);
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CopperControllerEnergyDuctBlockEntity>>
      COPPER_CONTROLLER_ENERGY_DUCT =
      register("copper_controller_energy_duct", ModBlocks.COPPER_CONTROLLER_ENERGY_DUCT, CopperControllerEnergyDuctBlockEntity::new);
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CopperControllerItemDuctBlockEntity>>
      COPPER_CONTROLLER_ITEM_DUCT =
      register("copper_controller_item_duct", ModBlocks.COPPER_CONTROLLER_ITEM_DUCT, CopperControllerItemDuctBlockEntity::new);
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CopperControllerFluidDuctBlockEntity>>
      COPPER_CONTROLLER_FLUID_DUCT =
      register("copper_controller_fluid_duct", ModBlocks.COPPER_CONTROLLER_FLUID_DUCT, CopperControllerFluidDuctBlockEntity::new);

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PulverizerBlockEntity>>
      PULVERIZER =
      register("pulverizer", ModBlocks.PULVERIZER, PulverizerBlockEntity::new);
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HeatGeneratorBlockEntity>>
      HEAT_GENERATOR =
      register("heat_generator", ModBlocks.HEAT_GENERATOR, HeatGeneratorBlockEntity::new);
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WaterPumpBlockEntity>>
      WATER_PUMP =
      register("water_pump", ModBlocks.WATER_PUMP, WaterPumpBlockEntity::new);

  // format-off

  private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>>
  register(String id, DeferredHolder<Block, ? extends Block> block, BlockEntityType.BlockEntitySupplier<T> supplier) {
    return TILES.register(id, () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));
  }

  public static void trigger() {
    // just trigger the class loader
  }
}
