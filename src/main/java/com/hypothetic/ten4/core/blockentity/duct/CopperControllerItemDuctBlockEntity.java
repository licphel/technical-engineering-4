package com.hypothetic.ten4.core.blockentity.duct;

import com.hypothetic.ten4.api.blockentity.transmission.DuctInfo;
import com.hypothetic.ten4.api.blockentity.transmission.ItemDuctBlockEntity;
import com.hypothetic.ten4.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CopperControllerItemDuctBlockEntity extends ItemDuctBlockEntity {
  public CopperControllerItemDuctBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.COPPER_CONTROLLER_ITEM_DUCT.get(), pos, state);

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
    return DuctTiers.COPPER_ITEM;
  }

  private boolean lastPowered;

  @Override
  public void onLoad() {
    super.onLoad();

    lastPowered = isRedstonePowered();
  }

  @Override
  public void tick() {
    super.tick();

    if (lastPowered != isRedstonePowered()) {
      lastPowered = isRedstonePowered();
      transmitter.rebuild();
    }
  }
}
