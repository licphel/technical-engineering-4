package com.hypothetic.ten4.core.registry;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.duct.EnergyDuctBlockEntity;
import com.hypothetic.ten4.api.blockentity.duct.FluidDuctBlockEntity;
import com.hypothetic.ten4.api.blockentity.duct.ItemDuctBlockEntity;
import com.hypothetic.ten4.api.registry.BlockEntityBridges;
import com.hypothetic.ten4.core.blockentity.TankBlockEntity;
import com.hypothetic.ten4.core.blockentity.device.*;
import com.hypothetic.ten4.core.blockentity.duct.ControllerEnergyDuctBlockEntity;
import com.hypothetic.ten4.core.blockentity.duct.ControllerFluidDuctBlockEntity;
import com.hypothetic.ten4.core.blockentity.duct.ControllerItemDuctBlockEntity;
import com.hypothetic.ten4.core.blockentity.duct.DuctTiers;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
  public static final DeferredRegister<BlockEntityType<?>> BES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Ten4.ID);

  // DeviceTiers
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> PULVERIZER = register("pulverizer", PulverizerBlockEntity::new);
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> PRESS = register("press", PressBlockEntity::new);
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> SMELTER = register("smelter", SmelterBlockEntity::new);
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> REFINER = register("refiner", RefinerBlockEntity::new);
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> WATER_PUMP = register("water_pump", WaterPumpBlockEntity::new);

  // Generators
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> HEAT_GENERATOR = register("heat_generator", HeatGeneratorBlockEntity::new);

  // Storage
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> TANK = register("tank", TankBlockEntity::new);

  // DuctTiers
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> COPPER_ENERGY_DUCT = register("copper_energy_duct", (p, s) -> new EnergyDuctBlockEntity(p, s, DuctTiers.COPPER_ENERGY.get()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> OPAQUE_COPPER_ENERGY_DUCT = register("opaque_copper_energy_duct", (p, s) -> new EnergyDuctBlockEntity(p, s, DuctTiers.COPPER_ENERGY.get()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> COPPER_CONTROLLER_ENERGY_DUCT = register("copper_controller_energy_duct", (p, s) -> new ControllerEnergyDuctBlockEntity(p, s, DuctTiers.COPPER_ENERGY.get()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> COPPER_ITEM_DUCT = register("copper_item_duct", (p, s) -> new ItemDuctBlockEntity(p, s, DuctTiers.COPPER_ITEM.get()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> OPAQUE_COPPER_ITEM_DUCT = register("opaque_copper_item_duct", (p, s) -> new ItemDuctBlockEntity(p, s, DuctTiers.COPPER_ITEM.get()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> COPPER_CONTROLLER_ITEM_DUCT = register("copper_controller_item_duct", (p, s) -> new ControllerItemDuctBlockEntity(p, s, DuctTiers.COPPER_ITEM.get()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> COPPER_FLUID_DUCT = register("copper_fluid_duct", (p, s) -> new FluidDuctBlockEntity(p, s, DuctTiers.COPPER_FLUID.get()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> OPAQUE_COPPER_FLUID_DUCT = register("opaque_copper_fluid_duct", (p, s) -> new FluidDuctBlockEntity(p, s, DuctTiers.COPPER_FLUID.get()));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> COPPER_CONTROLLER_FLUID_DUCT = register("copper_controller_fluid_duct", (p, s) -> new ControllerFluidDuctBlockEntity(p, s, DuctTiers.COPPER_FLUID.get()));

  private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> register(String id, BlockEntityType.BlockEntitySupplier<T> supplier) {
    return BES.register(id, () -> BlockEntityBridges.makeType(Ten4.id(id), supplier));
  }
}
