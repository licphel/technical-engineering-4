package com.hypothetic.ten4.api.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public interface ILootProvider {
  static void onRemoved(BlockState oldState, BlockState newState, Level level, BlockPos pos) {
    if (!oldState.is(newState.getBlock())) {
      createDrops(level, pos);
    }
  }

  static void createDrops(Level level, BlockPos pos) {
    BlockEntity be = level.getBlockEntity(pos);
    if (be instanceof ILootProvider provider) {
      NonNullList<ItemStack> loot = NonNullList.create();
      provider.getLoot(loot);
      Containers.dropContents(level, pos, loot);
    }
  }

  void getLoot(List<ItemStack> loot);
}
