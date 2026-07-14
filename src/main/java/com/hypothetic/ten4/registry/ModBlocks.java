package com.hypothetic.ten4.registry;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.core.block.DeviceBlock;
import com.hypothetic.ten4.core.block.duct.EnergyDuctBlock;
import com.hypothetic.ten4.core.block.duct.FluidDuctBlock;
import com.hypothetic.ten4.core.block.duct.ItemDuctBlock;
import com.hypothetic.ten4.core.blockentity.HeatGeneratorBlockEntity;
import com.hypothetic.ten4.core.blockentity.PulverizerBlockEntity;
import com.hypothetic.ten4.core.blockentity.WaterPumpBlockEntity;
import com.hypothetic.ten4.core.blockentity.duct.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class ModBlocks {
  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Ten4.ID);
  public static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.create(Registries.ITEM, Ten4.ID);

  public static final DeferredHolder<Block, Block> PULVERIZER =
      device("pulverizer", PulverizerBlockEntity::new, true);
  public static final DeferredHolder<Block, Block> HEAT_GENERATOR =
      device("heat_generator", HeatGeneratorBlockEntity::new, true);
  public static final DeferredHolder<Block, Block> WATER_PUMP =
      device("water_pump", WaterPumpBlockEntity::new, true);
  public static final DeferredHolder<Block, Block> COPPER_ENERGY_DUCT =
      copperEnergyDuct("copper_energy_duct", CopperEnergyDuctBlockEntity::new);
  public static final DeferredHolder<Block, Block> COPPER_ITEM_DUCT =
      copperItemDuct("copper_item_duct", CopperItemDuctBlockEntity::new);
  public static final DeferredHolder<Block, Block> COPPER_FLUID_DUCT =
      copperFluidDuct("copper_fluid_duct", CopperFluidDuctBlockEntity::new);
  public static final DeferredHolder<Block, Block> COPPER_CONTROLLER_ENERGY_DUCT =
      copperEnergyDuct("copper_controller_energy_duct", CopperControllerEnergyDuctBlockEntity::new);
  public static final DeferredHolder<Block, Block> COPPER_CONTROLLER_ITEM_DUCT =
      copperItemDuct("copper_controller_item_duct", CopperControllerItemDuctBlockEntity::new);
  public static final DeferredHolder<Block, Block> COPPER_CONTROLLER_FLUID_DUCT =
      copperFluidDuct("copper_controller_fluid_duct", CopperControllerFluidDuctBlockEntity::new);

  private ModBlocks() {
  }

  private static DeferredHolder<Block, Block> device(String name,
                                                     BlockEntityType.BlockEntitySupplier<?> supplier,
                                                     boolean tickable) {
    DeferredHolder<Block, Block> b = BLOCKS.register(name, () -> new DeviceBlock(
        Block.Properties.of()
            .mapColor(MapColor.METAL)
            .strength(3.5f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.METAL))
        .builder(supplier)
        .tickable(tickable));
    blockItem(name, b);
    ModCreativeTabs.deviceTab.add(() -> b.get().asItem());
    return b;
  }

  private static DeferredHolder<Block, Block> copperEnergyDuct(String name,
                                                               BlockEntityType.BlockEntitySupplier<?> supplier) {
    DeferredHolder<Block, Block> b = BLOCKS.register(name, () -> new EnergyDuctBlock(
        Block.Properties.of()
            .mapColor(MapColor.COLOR_ORANGE)
            .strength(3.5f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.COPPER))
        .builder(supplier)
        .tickable(true));
    blockItem(name, b);
    ModCreativeTabs.deviceTab.add(() -> b.get().asItem());
    return b;
  }

  private static DeferredHolder<Block, Block> copperItemDuct(String name,
                                                               BlockEntityType.BlockEntitySupplier<?> supplier) {
    DeferredHolder<Block, Block> b = BLOCKS.register(name, () -> new ItemDuctBlock(
        Block.Properties.of()
            .mapColor(MapColor.COLOR_ORANGE)
            .strength(3.5f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.COPPER))
        .builder(supplier)
        .tickable(true));
    blockItem(name, b);
    ModCreativeTabs.deviceTab.add(() -> b.get().asItem());
    return b;
  }

  private static DeferredHolder<Block, Block> copperFluidDuct(String name,
                                                               BlockEntityType.BlockEntitySupplier<?> supplier) {
    DeferredHolder<Block, Block> b = BLOCKS.register(name, () -> new FluidDuctBlock(
        Block.Properties.of()
            .mapColor(MapColor.COLOR_ORANGE)
            .strength(3.5f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.COPPER))
        .builder(supplier)
        .tickable(true));
    blockItem(name, b);
    ModCreativeTabs.deviceTab.add(() -> b.get().asItem());
    return b;
  }

  private static void blockItem(String name, Supplier<? extends Block> block) {
    BLOCK_ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
  }

  public static void trigger() {
    // just trigger the class loader
  }
}
