package com.hypothetic.ten4.core.blockentity.duct;

import com.hypothetic.ten4.api.blockentity.duct.DuctInfo;
import com.hypothetic.ten4.api.blockentity.duct.ItemDuctBlockEntity;
import com.hypothetic.ten4.core.block.BuiltinBlockStates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ControllerItemDuctBlockEntity extends ItemDuctBlockEntity {
  private boolean lastPowered;

  public ControllerItemDuctBlockEntity(BlockPos pos, BlockState state, DuctInfo info) {
    super(pos, state, info);

    for (Direction side : Direction.values()) {
      transmitter.setBlocker(side, d -> isRedstonePowered());
    }
  }

  @Override
  public boolean canConnectRedstone(@Nullable Direction side) {
    return true;
  }

  @Override
  public void onLoad() {
    super.onLoad();

    lastPowered = isRedstonePowered();
  }

  @Override
  public void tick() {
    super.tick();

    boolean powered = isRedstonePowered();
    if (lastPowered != powered) {
      lastPowered = BuiltinBlockStates.toggleActive(this, lastPowered, powered);
      transmitter.rebuild();
    }
  }
}
