package com.hypothetic.ten4.api.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RedstoneAwareBlockEntity extends BridgedBlockEntity implements IRedstoneBlockEntity {
  public RedstoneAwareBlockEntity(BlockPos pos, BlockState blockState) {
    super(pos, blockState);
  }

  public boolean isRedstonePowered() {
    if (level == null) {
      return false;
    }
    return level.hasNeighborSignal(worldPosition);
  }

  @Override
  public boolean canConnectRedstone(@Nullable Direction side) {
    return false;
  }

  @Override
  public int getComparatorSignal() {
    return 0;
  }
}
