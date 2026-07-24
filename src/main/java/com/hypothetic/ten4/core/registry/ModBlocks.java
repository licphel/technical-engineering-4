package com.hypothetic.ten4.core.registry;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.registry.FluidDeferredRegister;
import com.hypothetic.ten4.core.block.AsphaltBlock;
import com.hypothetic.ten4.core.block.BlockProperties;
import com.hypothetic.ten4.core.block.DeviceBlock;
import com.hypothetic.ten4.core.block.OilSandBlock;
import com.hypothetic.ten4.core.block.TankBlock;
import com.hypothetic.ten4.core.block.duct.EnergyDuctBlock;
import com.hypothetic.ten4.core.block.duct.FluidDuctBlock;
import com.hypothetic.ten4.core.block.duct.ItemDuctBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class ModBlocks {
  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Ten4.ID);

  // DeviceTiers
  public static final DeferredHolder<Block, Block> DEVICE_CASING = BLOCKS.register("device_casing", () -> new Block(BlockProperties.METAL_DEVICE));
  public static final DeferredHolder<Block, Block> PULVERIZER = BLOCKS.register("pulverizer", () -> new DeviceBlock(BlockProperties.METAL_DEVICE).tickBothSide());
  public static final DeferredHolder<Block, Block> PRESS = BLOCKS.register("press", () -> new DeviceBlock(BlockProperties.METAL_DEVICE).tickBothSide());
  public static final DeferredHolder<Block, Block> SMELTER = BLOCKS.register("smelter", () -> new DeviceBlock(BlockProperties.METAL_DEVICE).tickBothSide());
  public static final DeferredHolder<Block, Block> REFINER = BLOCKS.register("refiner", () -> new DeviceBlock(BlockProperties.METAL_DEVICE).tickBothSide());
  public static final DeferredHolder<Block, Block> WATER_PUMP = BLOCKS.register("water_pump", () -> new DeviceBlock(BlockProperties.METAL_DEVICE).tickBothSide());

  // Generators
  public static final DeferredHolder<Block, Block> HEAT_GENERATOR = BLOCKS.register("heat_generator", () -> new DeviceBlock(BlockProperties.METAL_DEVICE).tickBothSide());

  // Storage
  public static final DeferredHolder<Block, Block> TANK = BLOCKS.register("tank", () -> new TankBlock(BlockProperties.METAL_DEVICE).tickBothSide());

  // Other Blocks
  public static final DeferredHolder<Block, Block> ASPHALT = BLOCKS.register("asphalt", () -> new AsphaltBlock(BlockBehaviour.Properties.of().sound(SoundType.LODESTONE).mapColor(DyeColor.BLACK).strength(2.5F, 3.5F)));

  // Ores & Storage
  public static final DeferredHolder<Block, Block> TIN_ORE = BLOCKS.register("tin_ore", () -> new DropExperienceBlock(ConstantInt.of(0), BlockProperties.ore(2.75F, 2.5F, MapColor.STONE)));
  public static final DeferredHolder<Block, Block> DEEPSLATE_TIN_ORE = BLOCKS.register("deepslate_tin_ore", () -> new DropExperienceBlock(ConstantInt.of(0), BlockProperties.ore(4.0F, 3.0F, MapColor.DEEPSLATE)));
  public static final DeferredHolder<Block, Block> TIN_BLOCK = BLOCKS.register("tin_block", () -> new Block(BlockProperties.metalBlock(3.5F, 3.5F, MapColor.METAL)));
  public static final DeferredHolder<Block, Block> RAW_TIN_BLOCK = BLOCKS.register("raw_tin_block", () -> new Block(BlockProperties.metalBlock(2.75F, 2.5F, MapColor.METAL)));
  public static final DeferredHolder<Block, Block> TITANIUM_ORE = BLOCKS.register("titanium_ore", () -> new DropExperienceBlock(ConstantInt.of(0), BlockProperties.ore(4.0F, 3.0F, MapColor.STONE)));
  public static final DeferredHolder<Block, Block> DEEPSLATE_TITANIUM_ORE = BLOCKS.register("deepslate_titanium_ore", () -> new DropExperienceBlock(ConstantInt.of(0), BlockProperties.ore(5.5F, 4.0F, MapColor.DEEPSLATE)));
  public static final DeferredHolder<Block, Block> TITANIUM_BLOCK = BLOCKS.register("titanium_block", () -> new Block(BlockProperties.metalBlock(5.0F, 6.0F, MapColor.METAL)));
  public static final DeferredHolder<Block, Block> RAW_TITANIUM_BLOCK = BLOCKS.register("raw_titanium_block", () -> new Block(BlockProperties.metalBlock(4.0F, 4.5F, MapColor.METAL)));
  public static final DeferredHolder<Block, Block> MONAZITE_ORE = BLOCKS.register("monazite_ore", () -> new DropExperienceBlock(ConstantInt.of(0), BlockProperties.ore(5.0F, 4.0F, MapColor.NETHER)));
  public static final DeferredHolder<Block, Block> SULFUR_ORE = BLOCKS.register("sulfur_ore", () -> new DropExperienceBlock(UniformInt.of(2, 5), BlockProperties.ore(3.5F, 3.0F, MapColor.SAND)));
  public static final DeferredHolder<Block, Block> OIL_SAND = BLOCKS.register("oil_sand", OilSandBlock::new);
  public static final DeferredHolder<Block, Block> BORAX_BLOCK = BLOCKS.register("borax_block", () -> new DropExperienceBlock(ConstantInt.of(0), BlockProperties.ore(3.5F, 3.0F, MapColor.SAND)));

  // DuctTiers
  public static final DeferredHolder<Block, Block> COPPER_ENERGY_DUCT = BLOCKS.register("copper_energy_duct", () -> new EnergyDuctBlock(BlockProperties.COPPER_DUCT).tickBothSide());
  public static final DeferredHolder<Block, Block> OPAQUE_COPPER_ENERGY_DUCT = BLOCKS.register("opaque_copper_energy_duct", () -> new EnergyDuctBlock(BlockProperties.COPPER_DUCT));
  public static final DeferredHolder<Block, Block> COPPER_CONTROLLER_ENERGY_DUCT = BLOCKS.register("copper_controller_energy_duct", () -> new EnergyDuctBlock(BlockProperties.COPPER_DUCT).tickBothSide());
  public static final DeferredHolder<Block, Block> COPPER_ITEM_DUCT = BLOCKS.register("copper_item_duct", () -> new ItemDuctBlock(BlockProperties.COPPER_DUCT).tickBothSide());
  public static final DeferredHolder<Block, Block> OPAQUE_COPPER_ITEM_DUCT = BLOCKS.register("opaque_copper_item_duct", () -> new ItemDuctBlock(BlockProperties.COPPER_DUCT));
  public static final DeferredHolder<Block, Block> COPPER_CONTROLLER_ITEM_DUCT = BLOCKS.register("copper_controller_item_duct", () -> new ItemDuctBlock(BlockProperties.COPPER_DUCT).tickBothSide());
  public static final DeferredHolder<Block, Block> COPPER_FLUID_DUCT = BLOCKS.register("copper_fluid_duct", () -> new FluidDuctBlock(BlockProperties.COPPER_DUCT).tickBothSide());
  public static final DeferredHolder<Block, Block> OPAQUE_COPPER_FLUID_DUCT = BLOCKS.register("opaque_copper_fluid_duct", () -> new FluidDuctBlock(BlockProperties.COPPER_DUCT));
  public static final DeferredHolder<Block, Block> COPPER_CONTROLLER_FLUID_DUCT = BLOCKS.register("copper_controller_fluid_duct", () -> new FluidDuctBlock(BlockProperties.COPPER_DUCT).tickBothSide());

  // Fluids
  public static final DeferredHolder<Block, Block> TORAN = fluidBlock("toran_concentrate", () -> ModFluids.TORAN, MapColor.COLOR_PURPLE);
  public static final DeferredHolder<Block, Block> EXPERIENCE = fluidBlock("liquid_experience", () -> ModFluids.EXPERIENCE, MapColor.COLOR_LIGHT_GREEN);
  public static final DeferredHolder<Block, Block> REDSTONE = fluidBlock("liquid_redstone", () -> ModFluids.REDSTONE, MapColor.COLOR_RED);
  public static final DeferredHolder<Block, Block> NUTRIENT = fluidBlock("nutrient_solution", () -> ModFluids.NUTRIENT, MapColor.COLOR_LIGHT_GREEN);
  public static final DeferredHolder<Block, Block> CRUDE_OIL = fluidBlock("crude_oil", () -> ModFluids.CRUDE_OIL, MapColor.COLOR_BLACK);
  public static final DeferredHolder<Block, Block> NAPHTHA = fluidBlock("naphtha", () -> ModFluids.NAPHTHA, MapColor.COLOR_YELLOW);
  public static final DeferredHolder<Block, Block> KEROSENE = fluidBlock("kerosene", () -> ModFluids.KEROSENE, MapColor.TERRACOTTA_YELLOW);

  public static final Map<DyeColor, DeferredHolder<Block, Block>> DYE_BLOCKS = new EnumMap<>(DyeColor.class);
  static {
    for (DyeColor dye : DyeColor.values()) {
      DYE_BLOCKS.put(dye, fluidBlock(dye.getSerializedName() + "_dye_solution", () -> ModFluids.DYES.get(dye), dye.getMapColor()));
    }
  }

  private static DeferredHolder<Block, Block> fluidBlock(String name, Supplier<FluidDeferredRegister.FluidHolder> fluid, MapColor color) {
    return BLOCKS.register(name, () -> new LiquidBlock(fluid.get().flowing().get(), BlockProperties.coloredFluid(color)));
  }
}
