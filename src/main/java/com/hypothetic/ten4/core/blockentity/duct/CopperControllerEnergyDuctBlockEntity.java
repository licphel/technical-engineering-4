package com.hypothetic.ten4.core.blockentity.duct;

import com.hypothetic.ten4.api.blockentity.transmission.DuctInfo;
import com.hypothetic.ten4.api.blockentity.transmission.EnergyDuctBlockEntity;
import com.hypothetic.ten4.core.block.BuiltinBlockStates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CopperControllerEnergyDuctBlockEntity extends EnergyDuctBlockEntity {
  private boolean lastPowered;

  public CopperControllerEnergyDuctBlockEntity(BlockPos pos, BlockState state) {
    super(pos, state);

    for (Direction side : Direction.values()) {
      transmitter.setBlocker(side, d -> isRedstonePowered());
    }
  }

  @Override
  public boolean canConnectRedstone(@Nullable Direction side) {
    return true;
  }

  @Override
  protected DuctInfo makeDuctInfo() {
    return DuctTiers.COPPER_ENERGY;
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
