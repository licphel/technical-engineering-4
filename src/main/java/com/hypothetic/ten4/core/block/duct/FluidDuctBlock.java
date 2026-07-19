package com.hypothetic.ten4.core.block.duct;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;

public class FluidDuctBlock extends DuctBlock {
  public static final MapCodec<FluidDuctBlock> CODEC = simpleCodec(FluidDuctBlock::new);

  public FluidDuctBlock(Properties props) {
    super(props);
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
  }

  @Override
  public boolean hasConnection(Level level, Direction facing, BlockPos pos) {
    BlockPos neighborPos = pos.relative(facing);
    return level.getCapability(Capabilities.FluidHandler.BLOCK, neighborPos,
        level.getBlockState(neighborPos), null, facing.getOpposite()) != null;
  }

  @Override
  protected int getLightBlock(BlockState state, BlockGetter p_60586_, BlockPos p_60587_) {
    return super.getLightBlock(state, p_60586_, p_60587_);
  }
}
