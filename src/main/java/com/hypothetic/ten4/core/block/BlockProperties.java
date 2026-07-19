package com.hypothetic.ten4.core.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public final class BlockProperties {
  public static final Block.Properties METAL_DEVICE = Block.Properties.of()
      .mapColor(MapColor.METAL)
      .strength(3.5F)
      .requiresCorrectToolForDrops()
      .sound(SoundType.METAL)
      // .lightLevel(state -> state.hasProperty(BuiltinBlockStates.ACTIVE)
      //    && state.getValue(BuiltinBlockStates.ACTIVE) ? 8 : 0)
      .explosionResistance(2.5F);
  public static final BlockBehaviour.Properties COPPER_DUCT = Block.Properties.of()
      .mapColor(MapColor.TERRACOTTA_ORANGE)
      .strength(1.5F)
      .requiresCorrectToolForDrops()
      .sound(SoundType.COPPER)
      .explosionResistance(1.0F);
  public static final BlockBehaviour.Properties IRON_DUCT = Block.Properties.of()
      .mapColor(MapColor.METAL)
      .strength(2.0F)
      .requiresCorrectToolForDrops()
      .sound(SoundType.METAL)
      .explosionResistance(1.25F);

  public static BlockBehaviour.Properties coloredFluid(MapColor color) {
    return Block.Properties.of()
        .replaceable()
        .noCollission()
        .strength(100.0F)
        .pushReaction(PushReaction.DESTROY)
        .noLootTable()
        .liquid()
        .sound(SoundType.EMPTY)
        .mapColor(color);
  }

  private BlockProperties() {
  }
}
