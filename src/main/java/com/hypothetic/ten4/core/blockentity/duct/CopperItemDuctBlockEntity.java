package com.hypothetic.ten4.core.blockentity.duct;

import com.hypothetic.ten4.api.blockentity.transmission.DuctInfo;
import com.hypothetic.ten4.api.blockentity.transmission.ItemDuctBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CopperItemDuctBlockEntity extends ItemDuctBlockEntity {
  public CopperItemDuctBlockEntity(BlockPos pos, BlockState state) {
    super(pos, state);
  }

  @Override
  protected DuctInfo makeDuctInfo() {
    return DuctTiers.COPPER_ITEM;
  }
}
