package com.hypothetic.ten4.api.blockentity;

import com.hypothetic.ten4.api.IRedstoneBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RedstoneAwareBlockEntity extends BlockEntity implements IRedstoneBlockEntity {
  public RedstoneAwareBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
    super(type, pos, blockState);
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
