package com.hypothetic.ten4.core.block;

import com.hypothetic.ten4.core.registry.config.ModConfigs;
import net.minecraft.world.level.block.Block;

public class AsphaltBlock extends Block {
  public AsphaltBlock(Properties properties) {
    super(properties);
  }

  @Override
  public float getSpeedFactor() {
    return 1 + (float) ModConfigs.COMMON.others.asphaltSpeedBoost.getAsDouble();
  }

  @Override
  public float getJumpFactor() {
    return 1 + (float) ModConfigs.COMMON.others.asphaltJumpBoost.getAsDouble();
  }
}
