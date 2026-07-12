package com.hypothetic.ten4.registry;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.core.block.EnergyCableBlock;
import com.hypothetic.ten4.core.block.DeviceBlock;
import com.hypothetic.ten4.core.device.HeatGeneratorBlockEntity;
import com.hypothetic.ten4.core.device.PulverizerBlockEntity;
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

  public static final DeferredHolder<Block, EnergyCableBlock> COPPER_ENERGY_DUCT =
      BLOCKS.register("copper_energy_duct", () -> new EnergyCableBlock(
          Block.Properties.of().mapColor(MapColor.NONE).strength(1.5F)
              .sound(SoundType.GLASS).noOcclusion(),
          ModBlockEntities.COPPER_ENERGY_DUCT));

  static {
    BLOCK_ITEMS.register("copper_energy_duct", () -> new BlockItem(COPPER_ENERGY_DUCT.get(), new Item.Properties()));
    ModCreativeTabs.deviceTab.add(() -> COPPER_ENERGY_DUCT.get().asItem());
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
