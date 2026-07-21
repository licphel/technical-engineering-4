package com.hypothetic.ten4.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class OilSandBlock extends ColoredFallingBlock {
  public OilSandBlock() {
    super(new ColorRGBA(0x1D1D2111), Properties.of()
        .strength(1.0F, 1.0F)
        .mapColor(DyeColor.BLACK)
        .friction(0.8F)
        .sound(SoundType.MUD)
    );
  }

  @Override
  public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
    if (rand.nextInt(2) == 0) {
      ParticleUtils.spawnParticlesOnBlockFaces(level, pos,
          new BlockParticleOption(ParticleTypes.FALLING_DUST, state), ConstantInt.of(1));
    }
  }
}
