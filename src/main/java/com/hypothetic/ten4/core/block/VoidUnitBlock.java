package com.hypothetic.ten4.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public class VoidUnitBlock extends DeviceBlock {
  public static final MapCodec<VoidUnitBlock> CODEC = simpleCodec(VoidUnitBlock::new);

  public VoidUnitBlock(Properties p) {
    super(p);
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
  }
}
