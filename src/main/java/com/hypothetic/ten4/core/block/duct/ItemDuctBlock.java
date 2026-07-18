package com.hypothetic.ten4.core.block.duct;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.neoforged.neoforge.capabilities.Capabilities;

public class ItemDuctBlock extends DuctBlock {
  public static final MapCodec<ItemDuctBlock> CODEC = simpleCodec(ItemDuctBlock::new);

  public ItemDuctBlock(Properties props) {
    super(props);
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
  }

  @Override
  public boolean hasConnection(Level level, Direction facing, BlockPos pos) {
    BlockPos neighborPos = pos.relative(facing);
    return level.getCapability(Capabilities.ItemHandler.BLOCK, neighborPos,
        level.getBlockState(neighborPos), null, facing.getOpposite()) != null;
  }
}
