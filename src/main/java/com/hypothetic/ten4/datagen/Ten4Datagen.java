package com.hypothetic.ten4.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class Ten4Datagen {
  private Ten4Datagen() {
  }

  public static void gatherData(GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();
    PackOutput output = generator.getPackOutput();
    CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
    ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

    generator.addProvider(event.includeServer(),
        new LootTableProvider(output, Set.of(), List.of(
            new LootTableProvider.SubProviderEntry(
                Ten4BlockLoot::new,
                LootContextParamSets.BLOCK
            )
        ), lookupProvider));
  }
}
