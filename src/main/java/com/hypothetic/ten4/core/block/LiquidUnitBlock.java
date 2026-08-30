package com.hypothetic.ten4.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class LiquidUnitBlock extends DeviceBlock {
  public static final MapCodec<LiquidUnitBlock> CODEC = simpleCodec(LiquidUnitBlock::new);

  public LiquidUnitBlock(Properties p) {
    super(p);
  }

  @Override
  protected RenderShape getRenderShape(BlockState p_49232_) {
    return RenderShape.ENTITYBLOCK_ANIMATED;
  }

  @Override
  public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
    return 1; // light passes through the window, so the BER's light isn't 0
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
  }
}