package com.hypothetic.ten4.datagen.tag;

import com.hypothetic.ten4.Ten4;
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
    tag(cDusts)
        .addTag(cDustsGold)
        .addTag(cDustsIron)
        .addTag(cDustsCopper);

    TagKey<Item> cPlates = cTag("plates");
    TagKey<Item> cPlatesCopper = cTag("plates/copper");
    TagKey<Item> cPlatesGold = cTag("plates/gold");
    TagKey<Item> cPlatesIron = cTag("plates/iron");

    tag(cPlatesCopper).add(key(ModItems.COPPER_PLATE));
    tag(cPlatesGold).add(key(ModItems.GOLDEN_PLATE));
    tag(cPlatesIron).add(key(ModItems.IRON_PLATE));
    tag(cPlates)
        .addTag(cPlatesGold)
        .addTag(cPlatesIron)
        .addTag(cPlatesCopper);

    tag(cTag("tools/wrench"))
        .add(key(ModItems.WRENCH));
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
