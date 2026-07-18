package com.hypothetic.ten4.datagen.tag;

import com.hypothetic.ten4.Ten4;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.FluidTags;
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
        .addOptional(Ten4.id("liquid_xp"))
        .addOptional(Ten4.id("liquid_xp_flowing"))
        .addOptional(Ten4.id("liquid_bizarrerie"))
        .addOptional(Ten4.id("liquid_bizarrerie_flowing"));
  }
}
