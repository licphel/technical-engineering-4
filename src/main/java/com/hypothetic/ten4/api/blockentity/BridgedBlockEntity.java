package com.hypothetic.ten4.api.blockentity;

import com.hypothetic.ten4.api.registry.BlockEntityBridges;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class BridgedBlockEntity extends BlockEntity implements ILootProvider {
  public BridgedBlockEntity(BlockPos pos, BlockState blockState) {
    super(BlockEntityBridges.getEntity(blockState.getBlock()), pos, blockState);
  }

  @Override
  public ItemStack createSelfStack() {
    return new ItemStack(getBlockState().getBlock().asItem());
  }

  @Override
  public void getLoot(List<ItemStack> loot) {
  }
}
