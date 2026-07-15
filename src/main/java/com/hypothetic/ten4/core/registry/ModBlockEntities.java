package com.hypothetic.ten4.core.registry;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.transmission.EnergyDuctBlockEntity;
import com.hypothetic.ten4.api.blockentity.transmission.FluidDuctBlockEntity;
import com.hypothetic.ten4.api.blockentity.transmission.ItemDuctBlockEntity;
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
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyDuctBlockEntity>> COPPER_ENERGY_DUCT = register("copper_energy_duct", (p, s) -> new EnergyDuctBlockEntity(p, s, DuctTiers.COPPER_ENERGY));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyDuctBlockEntity>> OPAQUE_COPPER_ENERGY_DUCT = register("opaque_copper_energy_duct", (p, s) -> new EnergyDuctBlockEntity(p, s, DuctTiers.COPPER_ENERGY));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ControllerEnergyDuctBlockEntity>> COPPER_CONTROLLER_ENERGY_DUCT = register("copper_controller_energy_duct", (p, s) -> new ControllerEnergyDuctBlockEntity(p, s, DuctTiers.COPPER_ENERGY));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemDuctBlockEntity>> COPPER_ITEM_DUCT = register("copper_item_duct", (p, s) -> new ItemDuctBlockEntity(p, s, DuctTiers.COPPER_ITEM));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemDuctBlockEntity>> OPAQUE_COPPER_ITEM_DUCT = register("opaque_copper_item_duct", (p, s) -> new ItemDuctBlockEntity(p, s, DuctTiers.COPPER_ITEM));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ControllerItemDuctBlockEntity>> COPPER_CONTROLLER_ITEM_DUCT = register("copper_controller_item_duct", (p, s) -> new ControllerItemDuctBlockEntity(p, s, DuctTiers.COPPER_ITEM));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidDuctBlockEntity>> COPPER_FLUID_DUCT = register("copper_fluid_duct", (p, s) -> new FluidDuctBlockEntity(p, s, DuctTiers.COPPER_FLUID));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidDuctBlockEntity>> OPAQUE_COPPER_FLUID_DUCT = register("opaque_copper_fluid_duct", (p, s) -> new FluidDuctBlockEntity(p, s, DuctTiers.COPPER_FLUID));
  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ControllerFluidDuctBlockEntity>> COPPER_CONTROLLER_FLUID_DUCT = register("copper_controller_fluid_duct", (p, s) -> new ControllerFluidDuctBlockEntity(p, s, DuctTiers.COPPER_FLUID));

  private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String id, BlockEntityType.BlockEntitySupplier<T> supplier) {
    return BES.register(id, () -> BlockEntityBridges.makeType(Ten4.id(id), supplier));
  }
}
