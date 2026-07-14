package com.hypothetic.ten4.core.block;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class BuiltinBlockStates {
  public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

  public static boolean toggleActive(BlockEntity be, boolean old, boolean value) {
    if (be.getLevel() == null) {
      return old;
    }

    if (old != value) {
      BlockState state = be.getBlockState();
      if (state.hasProperty(BuiltinBlockStates.ACTIVE)) {
        be.getLevel().setBlockAndUpdate(be.getBlockPos(), state.setValue(BuiltinBlockStates.ACTIVE, value));
      }
      be.setChanged();
    }

    return value;
  }
}
