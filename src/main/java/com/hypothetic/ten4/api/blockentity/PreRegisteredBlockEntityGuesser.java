package com.hypothetic.ten4.api.blockentity;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface PreRegisteredBlockEntityGuesser<T extends BlockEntity> {
  T create(BlockEntityType<?> type, BlockPos pos, BlockState state);

  default T create(BlockPos pos, BlockState state) {
    BlockEntityType<?> type = MAPPING.getOrDefault(state.getBlock(), null);
    if (type == null) {
      throw new IllegalStateException("Unknown block entity type: " + state.getBlock());
    }
    return create(type, pos, state);
  }

  BiMap<Block, BlockEntityType<?>> MAPPING = HashBiMap.create();

  static void register(Block block, BlockEntityType<?> entity) {
    MAPPING.put(block, entity);
  }

  static <R extends BlockEntity> BlockEntityType.BlockEntitySupplier<R> map(PreRegisteredBlockEntityGuesser<R> guesser) {
    return guesser::create;
  }
}
