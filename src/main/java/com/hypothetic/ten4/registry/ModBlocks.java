package com.hypothetic.ten4.registry;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.internet.EnergyDuctBlockEntity;
import com.hypothetic.ten4.core.block.EnergyDuctBlock;
import com.hypothetic.ten4.api.blockentity.internet.FluidDuctBlockEntity;
import com.hypothetic.ten4.core.block.FluidDuctBlock;
import com.hypothetic.ten4.core.block.ItemDuctBlock;
import com.hypothetic.ten4.core.block.DeviceBlock;
import com.hypothetic.ten4.api.blockentity.internet.ItemDuctBlockEntity;
import com.hypothetic.ten4.core.device.HeatGeneratorBlockEntity;
import com.hypothetic.ten4.core.device.PulverizerBlockEntity;
import com.hypothetic.ten4.core.device.WaterPumpBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class ModBlocks {
  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Ten4.ID);
  public static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.create(Registries.ITEM, Ten4.ID);

  @SuppressWarnings("unchecked")
  public static final DeferredHolder<Block, DeviceBlock> PULVERIZER =
      (DeferredHolder<Block, DeviceBlock>) device("pulverizer",
          ModBlockEntities.PULVERIZER::get,
          PulverizerBlockEntity::new);

  @SuppressWarnings("unchecked")
  public static final DeferredHolder<Block, DeviceBlock> HEAT_GENERATOR =
      (DeferredHolder<Block, DeviceBlock>) device("heat_generator",
          ModBlockEntities.HEAT_GENERATOR::get, HeatGeneratorBlockEntity::new);

  @SuppressWarnings("unchecked")
  public static final DeferredHolder<Block, DeviceBlock> WATER_PUMP =
      (DeferredHolder<Block, DeviceBlock>) device("water_pump",
          ModBlockEntities.WATER_PUMP::get, WaterPumpBlockEntity::new);

  public static final DeferredHolder<Block, EnergyDuctBlock> COPPER_ENERGY_DUCT =
      BLOCKS.register("copper_energy_duct", () -> new EnergyDuctBlock(
          Block.Properties.of().mapColor(MapColor.NONE).strength(1.5F)
              .sound(SoundType.GLASS).noOcclusion(), EnergyDuctBlockEntity.CAP_COPPER));

  public static final DeferredHolder<Block, ItemDuctBlock> COPPER_ITEM_DUCT =
      BLOCKS.register("copper_item_duct", () -> new ItemDuctBlock(
          Block.Properties.of().mapColor(MapColor.NONE).strength(1.5F)
              .sound(SoundType.GLASS).noOcclusion(),
          ModBlockEntities.COPPER_ITEM_DUCT,
          ItemDuctBlockEntity.TPB_COPPER, ItemDuctBlockEntity.SLOTS_COPPER, ItemDuctBlockEntity.CAP_COPPER));

  public static final DeferredHolder<Block, FluidDuctBlock> COPPER_FLUID_DUCT =
      BLOCKS.register("copper_fluid_duct", () -> new FluidDuctBlock(
          Block.Properties.of().mapColor(MapColor.NONE).strength(1.5F)
              .sound(SoundType.GLASS).noOcclusion(),
          FluidDuctBlockEntity.CAP_COPPER, FluidDuctBlockEntity.TPB_COPPER));

  static {
    BLOCK_ITEMS.register("copper_energy_duct", () -> new BlockItem(COPPER_ENERGY_DUCT.get(), new Item.Properties()));
    BLOCK_ITEMS.register("copper_item_duct", () -> new BlockItem(COPPER_ITEM_DUCT.get(), new Item.Properties()));
    BLOCK_ITEMS.register("copper_fluid_duct", () -> new BlockItem(COPPER_FLUID_DUCT.get(), new Item.Properties()));
    ModCreativeTabs.deviceTab.add(() -> COPPER_ENERGY_DUCT.get().asItem());
    ModCreativeTabs.deviceTab.add(() -> COPPER_ITEM_DUCT.get().asItem());
    ModCreativeTabs.deviceTab.add(() -> COPPER_FLUID_DUCT.get().asItem());
  }

  private ModBlocks() {
  }

  private static DeferredHolder<Block, ? extends Block> device(String name,
                                                               Supplier<net.minecraft.world.level.block.entity.BlockEntityType<?>> bet,
                                                               java.util.function.BiFunction<net.minecraft.core.BlockPos,
                                                                   net.minecraft.world.level.block.state.BlockState,
                                                                   ? extends net.minecraft.world.level.block.entity.BlockEntity> factory) {
    DeferredHolder<Block, DeviceBlock> b = BLOCKS.register(name, () -> new DeviceBlock(
        Block.Properties.of().mapColor(MapColor.METAL).strength(3.5f)
            .requiresCorrectToolForDrops().sound(SoundType.METAL), bet, factory));
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
