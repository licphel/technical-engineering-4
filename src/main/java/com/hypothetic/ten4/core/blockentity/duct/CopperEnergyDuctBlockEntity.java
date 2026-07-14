package com.hypothetic.ten4.core.blockentity.duct;

import com.hypothetic.ten4.api.blockentity.transmission.DuctInfo;
import com.hypothetic.ten4.api.blockentity.transmission.EnergyDuctBlockEntity;
import com.hypothetic.ten4.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CopperEnergyDuctBlockEntity extends EnergyDuctBlockEntity {
  public CopperEnergyDuctBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.COPPER_ENERGY_DUCT.get(), pos, state);
  }

  @Override
  protected DuctInfo makeDuctInfo() {
    return DuctTiers.COPPER_ENERGY;
  }
}
