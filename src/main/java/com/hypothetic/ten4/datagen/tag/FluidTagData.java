package com.hypothetic.ten4.datagen.tag;

import com.hypothetic.ten4.Ten4;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class FluidTagData extends net.minecraft.data.tags.TagsProvider<Fluid> {
  public FluidTagData(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                      ExistingFileHelper existingFileHelper) {
    super(output, Registries.FLUID, lookupProvider, Ten4.ID, existingFileHelper);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    tag(FluidTags.WATER)
        .addOptional(Ten4.id("liquid_experience"))
        .addOptional(Ten4.id("liquid_experience_flowing"));

    cTag("crude_oil", "crude_oil");
    cTag("naphtha", "naphtha");
    cTag("kerosene", "kerosene");
    cTag("liquid_redstone", "liquid_redstone");
    cTag("nutrient_solution", "nutrient_solution");
    cTag("toran_concentrate", "toran_concentrate");
    cTag("liquid_experience", "liquid_experience");

    for (DyeColor dye : DyeColor.values()) {
      String name = dye.getSerializedName() + "_dye_solution";
      cTag(name, name);
    }
  }

  private void cTag(String tagName, String fluidName) {
    var key = TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath("c", tagName));
    tag(key)
        .addOptional(Ten4.id(fluidName))
        .addOptional(Ten4.id(fluidName + "_flowing"));
  }
}
