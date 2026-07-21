package com.hypothetic.ten4.core.item;

import com.hypothetic.ten4.datagen.tag.BlockTagData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

public class WrenchItem extends Item {
  public WrenchItem(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    if (context.getPlayer() != null
        && context.getPlayer().isShiftKeyDown()
        && context.getHand() == InteractionHand.MAIN_HAND) {
      BlockPos pos = context.getClickedPos();
      BlockState blockstate = context.getLevel().getBlockState(pos);

      if (blockstate.is(BlockTagData.DEVICES) || blockstate.is(BlockTagData.DUCTS)) {
        context.getLevel().destroyBlock(pos, true);
        return InteractionResult.SUCCESS;
      }
    }

    return super.useOn(context);
  }
}
