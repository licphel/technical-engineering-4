package com.hypothetic.ten4.datagen;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.datagen.loot.BlockLootData;
import com.hypothetic.ten4.datagen.tag.BlockTagData;
import com.hypothetic.ten4.datagen.tag.FluidTagData;
import com.hypothetic.ten4.datagen.tag.ItemTagData;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Ten4.ID)
public final class DataGenDispatcher {
  private DataGenDispatcher() {
  }

  @SubscribeEvent
  public static void gatherData(GatherDataEvent event) {
    if (!Ten4.DATA_GEN) {
      return;
    }

    DataGenerator generator = event.getGenerator();
    PackOutput output = generator.getPackOutput();
    CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
    ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

    generator.addProvider(event.includeServer(), new BlockTagData(output, lookupProvider, existingFileHelper));
    generator.addProvider(event.includeServer(), new ItemTagData(output, lookupProvider, existingFileHelper));
    generator.addProvider(event.includeServer(), new FluidTagData(output, lookupProvider, existingFileHelper));
    generator.addProvider(event.includeServer(), new LootTableProvider(output, Set.of(), List.of(new LootTableProvider.SubProviderEntry(BlockLootData::new, LootContextParamSets.BLOCK)), lookupProvider));
  }
}
