package com.hypothetic.ten4.init;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.device.HeatGeneratorBlockEntity;
import com.hypothetic.ten4.device.DeviceBlock;
import com.hypothetic.ten4.device.PulverizerBlockEntity;
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

  public static final DeferredRegister<Block> BLOCKS =
      DeferredRegister.create(Registries.BLOCK, Ten4.ID);
  public static final DeferredRegister<Item> BLOCK_ITEMS =
      DeferredRegister.create(Registries.ITEM, Ten4.ID);

  public static final DeferredHolder<Block, Block> TIN_ORE = ore("tin_ore", 3, MapColor.STONE, SoundType.STONE);
  public static final DeferredHolder<Block, Block> NICKEL_ORE = ore("nickel_ore", 4, MapColor.STONE, SoundType.STONE);
  public static final DeferredHolder<Block, Block> DEEP_TIN_ORE = ore("deep_tin_ore", 4, MapColor.DEEPSLATE, SoundType.DEEPSLATE);
  public static final DeferredHolder<Block, Block> DEEP_NICKEL_ORE = ore("deep_nickel_ore", 5, MapColor.DEEPSLATE, SoundType.DEEPSLATE);

  public static final DeferredHolder<Block, Block> TIN_BLOCK = storage("tin_block", 4, MapColor.METAL, SoundType.METAL);
  public static final DeferredHolder<Block, Block> NICKEL_BLOCK = storage("nickel_block", 5, MapColor.METAL, SoundType.METAL);
  public static final DeferredHolder<Block, Block> POWERED_TIN_BLOCK = storage("powered_tin_block", 5.5f, MapColor.METAL, SoundType.METAL);
  public static final DeferredHolder<Block, Block> CHLORIUM_BLOCK = storage("chlorium_block", 5, MapColor.METAL, SoundType.METAL);
  public static final DeferredHolder<Block, Block> RAW_TIN_BLOCK = rawStorage("raw_tin_block", 3);
  public static final DeferredHolder<Block, Block> RAW_NICKEL_BLOCK = rawStorage("raw_nickel_block", 4);

  @SuppressWarnings("unchecked")
  public static final DeferredHolder<Block, DeviceBlock> PULVERIZER =
      (DeferredHolder<Block, DeviceBlock>) device("pulverizer",
          ModBlockEntities.PULVERIZER::get,
          PulverizerBlockEntity::new);

  @SuppressWarnings("unchecked")
  public static final DeferredHolder<Block, DeviceBlock> HEAT_GENERATOR =
      (DeferredHolder<Block, DeviceBlock>) device("heat_generator",
          ModBlockEntities.HEAT_GENERATOR::get, HeatGeneratorBlockEntity::new);

  private ModBlocks() {
  }

  private static DeferredHolder<Block, Block> ore(String name, float h, MapColor c, SoundType s) {
    DeferredHolder<Block, Block> b = BLOCKS.register(name, () -> new Block(Block.Properties.of()
        .mapColor(c).strength(h, h).requiresCorrectToolForDrops().sound(s)));
    blockItem(name, b);
    ModCreativeTabs.blockTab.add(() -> b.get().asItem());
    return b;
  }

  private static DeferredHolder<Block, Block> storage(String name, float h, MapColor c, SoundType s) {
    DeferredHolder<Block, Block> b = BLOCKS.register(name, () -> new Block(Block.Properties.of()
        .mapColor(c).strength(h).requiresCorrectToolForDrops().sound(s)));
    blockItem(name, b);
    ModCreativeTabs.blockTab.add(() -> b.get().asItem());
    return b;
  }

  private static DeferredHolder<Block, Block> rawStorage(String name, float h) {
    DeferredHolder<Block, Block> b = BLOCKS.register(name, () -> new Block(Block.Properties.of()
        .mapColor(MapColor.STONE).strength(h).requiresCorrectToolForDrops().sound(SoundType.STONE)));
    blockItem(name, b);
    ModCreativeTabs.blockTab.add(() -> b.get().asItem());
    return b;
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
