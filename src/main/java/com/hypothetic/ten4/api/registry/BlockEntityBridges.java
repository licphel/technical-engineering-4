package com.hypothetic.ten4.api.registry;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.hypothetic.ten4.Ten4;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public final class BlockEntityBridges {
  private static final Map<ResourceLocation, ResourceLocation> BLOCK_TO_ENTITY = new HashMap<>();
  private static final Multimap<ResourceLocation, ResourceLocation> ENTITY_TO_BLOCK_SET = MultimapBuilder.hashKeys().hashSetValues().build();

  private BlockEntityBridges() {
  }

  @SuppressWarnings("unchecked")
  public static <T extends BlockEntity> @Nullable BlockEntityType<T> getEntity(ResourceLocation id) {
    id = BLOCK_TO_ENTITY.getOrDefault(id, null);
    return (BlockEntityType<T>) BuiltInRegistries.BLOCK_ENTITY_TYPE.getOptional(id).orElse(null);
  }

  public static <T extends BlockEntity> @Nullable BlockEntityType<T> getEntity(Block block) {
    return getEntity(BuiltInRegistries.BLOCK.getKey(block));
  }

  public static Set<Block> getValidBlocks(@Nullable ResourceLocation id) {
    Collection<ResourceLocation> set = ENTITY_TO_BLOCK_SET.get(id);
    return set.stream().map(BuiltInRegistries.BLOCK::get).collect(Collectors.toCollection(HashSet::new));
  }

  public static Set<Block> getValidBlocks(BlockEntityType<?> type) {
    return getValidBlocks(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type));
  }

  @SuppressWarnings("all")
  public static <T extends BlockEntity> BlockEntityType<T> makeType(ResourceLocation id, BlockEntityType.BlockEntitySupplier<T> supplier) {
    Set<Block> set = getValidBlocks(id);
    if (set.isEmpty()) {
      Ten4.LOGGER.fatal("BlockEntityType '{}' has zero valid blocks — "
          + "did you put ModBlockEntityBridges.register() before any mod registry?", id);
    }

    return new BlockEntityType<>(supplier, set, null);
  }

  public static void register(DeferredHolder<?, ?> block, DeferredHolder<?, ?> blockEntity) {
    BLOCK_TO_ENTITY.put(block.getId(), blockEntity.getId());
    ENTITY_TO_BLOCK_SET.put(blockEntity.getId(), block.getId());
  }
}
