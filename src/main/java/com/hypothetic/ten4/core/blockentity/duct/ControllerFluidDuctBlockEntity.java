package com.hypothetic.ten4.core.blockentity.duct;

import com.hypothetic.ten4.api.blockentity.transmission.DuctInfo;
import com.hypothetic.ten4.api.blockentity.transmission.FluidDuctBlockEntity;
import com.hypothetic.ten4.core.block.BuiltinBlockStates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ControllerFluidDuctBlockEntity extends FluidDuctBlockEntity {
  private boolean lastPowered;

  public ControllerFluidDuctBlockEntity(BlockPos pos, BlockState state, DuctInfo info) {
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
