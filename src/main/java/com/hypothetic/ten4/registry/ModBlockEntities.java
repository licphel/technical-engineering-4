package com.hypothetic.ten4.registry;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.registry.BlockEntityBridges;
import com.hypothetic.ten4.core.blockentity.HeatGeneratorBlockEntity;
import com.hypothetic.ten4.core.blockentity.PulverizerBlockEntity;
import com.hypothetic.ten4.core.blockentity.SmelterBlockEntity;
import com.hypothetic.ten4.core.blockentity.WaterPumpBlockEntity;
import com.hypothetic.ten4.core.blockentity.duct.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
  public static final DeferredRegister<BlockEntityType<?>> BES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Ten4.ID);

  // Devices
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PulverizerBlockEntity>> PULVERIZER = register("pulverizer", PulverizerBlockEntity::new);
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SmelterBlockEntity>> SMELTER = register("smelter", SmelterBlockEntity::new);
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WaterPumpBlockEntity>> WATER_PUMP = register("water_pump", WaterPumpBlockEntity::new);

  // Generators
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HeatGeneratorBlockEntity>> HEAT_GENERATOR = register("heat_generator", HeatGeneratorBlockEntity::new);

  // Ducts
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CopperEnergyDuctBlockEntity>> COPPER_ENERGY_DUCT = register("copper_energy_duct", CopperEnergyDuctBlockEntity::new);
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CopperItemDuctBlockEntity>> COPPER_ITEM_DUCT = register("copper_item_duct", CopperItemDuctBlockEntity::new);
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CopperFluidDuctBlockEntity>> COPPER_FLUID_DUCT = register("copper_fluid_duct", CopperFluidDuctBlockEntity::new);
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CopperControllerEnergyDuctBlockEntity>> COPPER_CONTROLLER_ENERGY_DUCT = register("copper_controller_energy_duct", CopperControllerEnergyDuctBlockEntity::new);
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CopperControllerItemDuctBlockEntity>> COPPER_CONTROLLER_ITEM_DUCT = register("copper_controller_item_duct", CopperControllerItemDuctBlockEntity::new);
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CopperControllerFluidDuctBlockEntity>> COPPER_CONTROLLER_FLUID_DUCT = register("copper_controller_fluid_duct", CopperControllerFluidDuctBlockEntity::new);

  private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String id, BlockEntityType.BlockEntitySupplier<T> supplier) {
    return BES.register(id, () -> BlockEntityBridges.makeType(Ten4.id(id), supplier));
  }
}
