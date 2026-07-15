package com.hypothetic.ten4.core.registry;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.core.block.BlockProperties;
import com.hypothetic.ten4.core.block.DeviceBlock;
import com.hypothetic.ten4.core.block.duct.EnergyDuctBlock;
import com.hypothetic.ten4.core.block.duct.FluidDuctBlock;
import com.hypothetic.ten4.core.block.duct.ItemDuctBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("unused")
public final class ModBlocks {
  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Ten4.ID);

  // Devices
  public static final DeferredHolder<Block, Block> DEVICE_CASING = BLOCKS.register("device_casing", () -> new Block(BlockProperties.METAL_DEVICE));
  public static final DeferredHolder<Block, Block> PULVERIZER = BLOCKS.register("pulverizer", () -> new DeviceBlock(BlockProperties.METAL_DEVICE).tickBothSide());
  public static final DeferredHolder<Block, Block> SMELTER = BLOCKS.register("smelter", () -> new DeviceBlock(BlockProperties.METAL_DEVICE).tickBothSide());
  public static final DeferredHolder<Block, Block> WATER_PUMP = BLOCKS.register("water_pump", () -> new DeviceBlock(BlockProperties.METAL_DEVICE).tickBothSide());

  // Generators
  public static final DeferredHolder<Block, Block> HEAT_GENERATOR = BLOCKS.register("heat_generator", () -> new DeviceBlock(BlockProperties.METAL_DEVICE).tickBothSide());

  // Ducts
  public static final DeferredHolder<Block, Block> COPPER_ENERGY_DUCT = BLOCKS.register("copper_energy_duct", () -> new EnergyDuctBlock(BlockProperties.COPPER_DUCT).tickBothSide());
  public static final DeferredHolder<Block, Block> OPAQUE_COPPER_ENERGY_DUCT = BLOCKS.register("opaque_copper_energy_duct", () -> new EnergyDuctBlock(BlockProperties.COPPER_DUCT));
  public static final DeferredHolder<Block, Block> COPPER_CONTROLLER_ENERGY_DUCT = BLOCKS.register("copper_controller_energy_duct", () -> new EnergyDuctBlock(BlockProperties.COPPER_DUCT).tickBothSide());
  public static final DeferredHolder<Block, Block> COPPER_ITEM_DUCT = BLOCKS.register("copper_item_duct", () -> new ItemDuctBlock(BlockProperties.COPPER_DUCT).tickBothSide());
  public static final DeferredHolder<Block, Block> OPAQUE_COPPER_ITEM_DUCT = BLOCKS.register("opaque_copper_item_duct", () -> new ItemDuctBlock(BlockProperties.COPPER_DUCT));
  public static final DeferredHolder<Block, Block> COPPER_CONTROLLER_ITEM_DUCT = BLOCKS.register("copper_controller_item_duct", () -> new ItemDuctBlock(BlockProperties.COPPER_DUCT).tickBothSide());
  public static final DeferredHolder<Block, Block> COPPER_FLUID_DUCT = BLOCKS.register("copper_fluid_duct", () -> new FluidDuctBlock(BlockProperties.COPPER_DUCT).tickBothSide());
  public static final DeferredHolder<Block, Block> OPAQUE_COPPER_FLUID_DUCT = BLOCKS.register("opaque_copper_fluid_duct", () -> new FluidDuctBlock(BlockProperties.COPPER_DUCT));
  public static final DeferredHolder<Block, Block> COPPER_CONTROLLER_FLUID_DUCT = BLOCKS.register("copper_controller_fluid_duct", () -> new FluidDuctBlock(BlockProperties.COPPER_DUCT).tickBothSide());
}
