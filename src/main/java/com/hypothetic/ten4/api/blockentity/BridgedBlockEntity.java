package com.hypothetic.ten4.api.blockentity;

import com.hypothetic.ten4.api.registry.BlockEntityBridges;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BridgedBlockEntity extends BlockEntity {
  public BridgedBlockEntity(BlockPos pos, BlockState blockState) {
    super(BlockEntityBridges.getEntity(blockState.getBlock()), pos, blockState);
  }
}
