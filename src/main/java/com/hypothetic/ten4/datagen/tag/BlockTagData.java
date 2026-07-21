package com.hypothetic.ten4.datagen.tag;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.core.registry.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.concurrent.CompletableFuture;

public class BlockTagData extends net.minecraft.data.tags.TagsProvider<Block> {
  public static final TagKey<Block> DUCTS = TagKey.create(Registries.BLOCK, Ten4.id("ducts"));
  public static final TagKey<Block> DEVICES = TagKey.create(Registries.BLOCK, Ten4.id("devices"));

  public BlockTagData(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                      ExistingFileHelper existingFileHelper) {
    super(output, Registries.BLOCK, lookupProvider, Ten4.ID, existingFileHelper);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    tag(DUCTS)
        .add(key(ModBlocks.COPPER_ENERGY_DUCT))
        .add(key(ModBlocks.OPAQUE_COPPER_ENERGY_DUCT))
        .add(key(ModBlocks.COPPER_CONTROLLER_ENERGY_DUCT))
        .add(key(ModBlocks.COPPER_ITEM_DUCT))
        .add(key(ModBlocks.OPAQUE_COPPER_ITEM_DUCT))
        .add(key(ModBlocks.COPPER_CONTROLLER_ITEM_DUCT))
        .add(key(ModBlocks.COPPER_FLUID_DUCT))
        .add(key(ModBlocks.OPAQUE_COPPER_FLUID_DUCT))
        .add(key(ModBlocks.COPPER_CONTROLLER_FLUID_DUCT));

    tag(DEVICES)
        .add(key(ModBlocks.PULVERIZER))
        .add(key(ModBlocks.PRESS))
        .add(key(ModBlocks.SMELTER))
        .add(key(ModBlocks.WATER_PUMP))
        .add(key(ModBlocks.HEAT_GENERATOR));

    tag(BlockTags.MINEABLE_WITH_SHOVEL)
        .add(key(ModBlocks.OIL_SAND));

    tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .add(key(ModBlocks.TIN_ORE)).add(key(ModBlocks.DEEPSLATE_TIN_ORE))
        .add(key(ModBlocks.TIN_BLOCK)).add(key(ModBlocks.RAW_TIN_BLOCK))
        .add(key(ModBlocks.TITANIUM_ORE)).add(key(ModBlocks.DEEPSLATE_TITANIUM_ORE))
        .add(key(ModBlocks.TITANIUM_BLOCK)).add(key(ModBlocks.RAW_TITANIUM_BLOCK))
        .add(key(ModBlocks.MONAZITE_ORE)).add(key(ModBlocks.SULFUR_ORE)).add(key(ModBlocks.BORAX_BLOCK))
        .add(key(ModBlocks.DEVICE_CASING))
        .addTag(DEVICES).addTag(DUCTS);

    tag(BlockTags.NEEDS_IRON_TOOL)
        .add(key(ModBlocks.DEVICE_CASING))
        .add(key(ModBlocks.TITANIUM_ORE)).add(key(ModBlocks.DEEPSLATE_TITANIUM_ORE))
        .add(key(ModBlocks.TITANIUM_BLOCK)).add(key(ModBlocks.RAW_TITANIUM_BLOCK))
        .add(key(ModBlocks.MONAZITE_ORE))
        .addTag(DEVICES);

    tag(BlockTags.NEEDS_STONE_TOOL)
        .add(key(ModBlocks.TIN_ORE)).add(key(ModBlocks.DEEPSLATE_TIN_ORE))
        .add(key(ModBlocks.TIN_BLOCK)).add(key(ModBlocks.RAW_TIN_BLOCK))
        .add(key(ModBlocks.SULFUR_ORE)).add(key(ModBlocks.BORAX_BLOCK));

    tag(cTag("ores/tin")).add(key(ModBlocks.TIN_ORE)).add(key(ModBlocks.DEEPSLATE_TIN_ORE));
    tag(cTag("ores/titanium")).add(key(ModBlocks.TITANIUM_ORE)).add(key(ModBlocks.DEEPSLATE_TITANIUM_ORE));
    tag(cTag("ores/monazite")).add(key(ModBlocks.MONAZITE_ORE));
    tag(cTag("ores/sulfur")).add(key(ModBlocks.SULFUR_ORE));
    tag(cTag("ores")).addTag(cTag("ores/tin")).addTag(cTag("ores/titanium")).addTag(cTag("ores/monazite")).addTag(cTag("ores/sulfur"));
    tag(cTag("storage_blocks/tin")).add(key(ModBlocks.TIN_BLOCK));
    tag(cTag("storage_blocks/raw_tin")).add(key(ModBlocks.RAW_TIN_BLOCK));
    tag(cTag("storage_blocks/titanium")).add(key(ModBlocks.TITANIUM_BLOCK));
    tag(cTag("storage_blocks/raw_titanium")).add(key(ModBlocks.RAW_TITANIUM_BLOCK));
    tag(cTag("storage_blocks/borax")).add(key(ModBlocks.BORAX_BLOCK));
    tag(cTag("storage_blocks")).addTag(cTag("storage_blocks/tin")).addTag(cTag("storage_blocks/raw_tin")).addTag(cTag("storage_blocks/titanium")).addTag(cTag("storage_blocks/raw_titanium")).addTag(cTag("storage_blocks/borax"));
  }

  private static ResourceKey<Block> key(DeferredHolder<?, ?> holder) {
    return ResourceKey.create(Registries.BLOCK, holder.getId());
  }

  private static ResourceLocation ten4(String path) {
    return Ten4.id(path);
  }

  private static TagKey<Block> cTag(String path) {
    return TagKey.create(Registries.BLOCK, cLoc(path));
  }

  private static ResourceLocation cLoc(String path) {
    return ResourceLocation.fromNamespaceAndPath("c", path);
  }
}
