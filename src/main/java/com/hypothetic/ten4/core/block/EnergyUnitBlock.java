package com.hypothetic.ten4.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public class EnergyUnitBlock extends DeviceBlock {
  public static final MapCodec<EnergyUnitBlock> CODEC = simpleCodec(EnergyUnitBlock::new);

  public EnergyUnitBlock(Properties p) {
    super(p);
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
  }
}
