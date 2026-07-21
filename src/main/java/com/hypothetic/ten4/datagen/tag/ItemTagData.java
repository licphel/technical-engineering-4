package com.hypothetic.ten4.datagen.tag;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.core.registry.ModBlocks;
import com.hypothetic.ten4.core.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.concurrent.CompletableFuture;

public class ItemTagData extends net.minecraft.data.tags.TagsProvider<Item> {
  public static final TagKey<Item> DIES = TagKey.create(Registries.ITEM, Ten4.id("dies"));

  public ItemTagData(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                     ExistingFileHelper existingFileHelper) {
    super(output, Registries.ITEM, lookupProvider, Ten4.ID, existingFileHelper);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    tag(DIES)
        .add(key(ModItems.SHEET_DIE))
        .add(key(ModItems.DEPACKING_DIE))
        .add(key(ModItems.PACKING_DIE));

    TagKey<Item> cDusts = cTag("dusts");
    TagKey<Item> cDustsCopper = cTag("dusts/copper");
    TagKey<Item> cDustsGold = cTag("dusts/gold");
    TagKey<Item> cDustsIron = cTag("dusts/iron");

    tag(cDustsCopper).add(key(ModItems.COPPER_DUST));
    tag(cDustsGold).add(key(ModItems.GOLD_DUST));
    tag(cDustsIron).add(key(ModItems.IRON_DUST));
    TagKey<Item> cDustsTin = cTag("dusts/tin");
    tag(cDustsTin).add(key(ModItems.TIN_DUST));
    TagKey<Item> cDustsTitanium = cTag("dusts/titanium");
    tag(cDustsTitanium).add(key(ModItems.TITANIUM_DUST));
    TagKey<Item> cDustsMonazite = cTag("dusts/monazite");
    tag(cDustsMonazite).add(key(ModItems.MONAZITE_DUST));
    TagKey<Item> cDustsSulfur = cTag("dusts/sulfur");
    tag(cDustsSulfur).add(key(ModItems.SULFUR_DUST));
    TagKey<Item> cDustsBorax = cTag("dusts/borax");
    tag(cDustsBorax).add(key(ModItems.BORAX_DUST));
    tag(cDusts).addTag(cDustsTin)
        .addTag(cDustsGold)
        .addTag(cDustsIron)
        .addTag(cDustsCopper)
        .addTag(cDustsTitanium)
        .addTag(cDustsMonazite)
        .addTag(cDustsSulfur)
        .addTag(cDustsBorax);

    TagKey<Item> cPlates = cTag("plates");
    TagKey<Item> cPlatesCopper = cTag("plates/copper");
    TagKey<Item> cPlatesGold = cTag("plates/gold");
    TagKey<Item> cPlatesIron = cTag("plates/iron");
    TagKey<Item> cPlatesTin = cTag("plates/tin");

    tag(cPlatesCopper).add(key(ModItems.COPPER_PLATE));
    tag(cPlatesGold).add(key(ModItems.GOLDEN_PLATE));
    tag(cPlatesIron).add(key(ModItems.IRON_PLATE));
    TagKey<Item> cPlatesTitanium = cTag("plates/titanium");
    tag(cPlatesTitanium).add(key(ModItems.TITANIUM_PLATE));
    tag(cPlatesTin).add(key(ModItems.TIN_PLATE));
    tag(cPlates).addTag(cPlatesTin)
        .addTag(cPlatesGold)
        .addTag(cPlatesIron)
        .addTag(cPlatesCopper)
        .addTag(cPlatesTitanium);

    TagKey<Item> cIngotsTin = cTag("ingots/tin");
    tag(cIngotsTin).add(key(ModItems.TIN_INGOT));
    TagKey<Item> cIngotsTitanium = cTag("ingots/titanium");
    tag(cIngotsTitanium).add(key(ModItems.TITANIUM_INGOT));
    tag(cTag("ingots")).addTag(cIngotsTin).addTag(cIngotsTitanium);

    TagKey<Item> cNuggetsCopper = cTag("nuggets/copper");
    tag(cNuggetsCopper).add(key(ModItems.COPPER_NUGGET));
    TagKey<Item> cNuggetsTin = cTag("nuggets/tin");
    tag(cNuggetsTin).add(key(ModItems.TIN_NUGGET));
    TagKey<Item> cNuggetsTitanium = cTag("nuggets/titanium");
    tag(cNuggetsTitanium).add(key(ModItems.TITANIUM_NUGGET));
    tag(cTag("nuggets")).addTag(cNuggetsCopper).addTag(cNuggetsTin).addTag(cNuggetsTitanium);

    TagKey<Item> cRawTin = cTag("raw_materials/tin");
    tag(cRawTin).add(key(ModItems.RAW_TIN));
    TagKey<Item> cRawTitanium = cTag("raw_materials/titanium");
    tag(cRawTitanium).add(key(ModItems.RAW_TITANIUM));
    tag(cTag("raw_materials")).addTag(cRawTin).addTag(cRawTitanium);

    TagKey<Item> cGemBorax = cTag("gems/borax");
    tag(cGemBorax).add(key(ModItems.BORAX));
    TagKey<Item> cGemMonazite = cTag("gems/monazite");
    tag(cGemMonazite).add(key(ModItems.MONAZITE));
    tag(cTag("gems")).addTag(cGemBorax).addTag(cGemMonazite);

    tag(cTag("tools/wrench")).add(key(ModItems.WRENCH));

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

  private static ResourceKey<Item> key(DeferredHolder<?, ?> holder) {
    return ResourceKey.create(Registries.ITEM, holder.getId());
  }

  private static TagKey<Item> cTag(String path) {
    return TagKey.create(Registries.ITEM, cLoc(path));
  }

  private static ResourceLocation cLoc(String path) {
    return ResourceLocation.fromNamespaceAndPath("c", path);
  }
}
