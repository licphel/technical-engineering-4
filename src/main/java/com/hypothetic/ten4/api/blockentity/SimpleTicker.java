package com.hypothetic.ten4.api.blockentity;

import com.hypothetic.ten4.api.ITickable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleTicker<T extends BlockEntity> implements BlockEntityTicker<T> {
  @Override
  public void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
    BlockEntity entity = level.getBlockEntity(pos);

    if (entity instanceof ITickable tickable) {
      tickable.tick();
    }
  }
}
