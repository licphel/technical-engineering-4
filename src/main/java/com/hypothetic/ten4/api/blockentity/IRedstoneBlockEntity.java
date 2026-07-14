package com.hypothetic.ten4.api.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public interface IRedstoneBlockEntity {
  static boolean canConnect(BlockGetter level, BlockPos pos, @Nullable Direction direction) {
    BlockEntity blockEntity = level.getBlockEntity(pos);

    if (blockEntity instanceof IRedstoneBlockEntity cct) {
      return cct.canConnectRedstone(direction);
    }

    return false;
  }

  static int getAnalogSignal(BlockGetter level, BlockPos pos) {
    BlockEntity blockEntity = level.getBlockEntity(pos);

    if (blockEntity instanceof IRedstoneBlockEntity cct) {
      return cct.getComparatorSignal();
    }

    return 0;
  }

  boolean canConnectRedstone(@Nullable Direction side);

  int getComparatorSignal();
}
