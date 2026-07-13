package com.hypothetic.ten4.core.block;

import com.hypothetic.ten4.api.blockentity.SimpleTicker;
import com.hypothetic.ten4.api.blockentity.internet.FluidDuctBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

public class FluidDuctBlock extends DuctBlock {
  protected final int bufferCapacity, ticksPerBlock;

  public FluidDuctBlock(Properties props, int capacity, int ticksPerBlock) {
    super(props);
    this.bufferCapacity = capacity;
    this.ticksPerBlock = ticksPerBlock;
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return simpleCodec(p -> new FluidDuctBlock(p, bufferCapacity, ticksPerBlock));
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new FluidDuctBlockEntity(pos, state, bufferCapacity, ticksPerBlock);
  }

  @Override
  protected boolean hasConnection(Level level, Direction facing, BlockPos pos) {
    BlockPos neighborPos = pos.relative(facing);
    return level.getCapability(Capabilities.FluidHandler.BLOCK, neighborPos,
        level.getBlockState(neighborPos), null, facing.getOpposite()) != null;
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
    return new SimpleTicker<>();
  }
}
