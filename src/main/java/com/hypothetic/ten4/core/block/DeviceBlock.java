package com.hypothetic.ten4.core.block;

import com.hypothetic.ten4.api.block.BridgedEntityBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class DeviceBlock extends BridgedEntityBlock {
  public static final MapCodec<DeviceBlock> CODEC = simpleCodec(DeviceBlock::new);

  public DeviceBlock(Properties p) {
    super(p);

    BlockState state = defaultBlockState().setValue(BuiltinBlockStates.ACTIVE, false);
    registerDefaultState(state);
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
  }

  @Override
  public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
    return defaultBlockState()
        .setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite())
        .setValue(BuiltinBlockStates.ACTIVE, false);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(BlockStateProperties.HORIZONTAL_FACING);
    builder.add(BuiltinBlockStates.ACTIVE);
  }
}
