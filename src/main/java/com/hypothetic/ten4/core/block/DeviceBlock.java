package com.hypothetic.ten4.core.block;

import com.hypothetic.ten4.api.block.BridgedEntityBlock;
import com.hypothetic.ten4.api.blockentity.device.AbstractDeviceBlockEntity;
import com.hypothetic.ten4.api.blockentity.device.ComparatorMode;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
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

  @Override
  public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
    BlockEntity be = level.getBlockEntity(pos);
    if (be instanceof AbstractDeviceBlockEntity device) {
      int s = device.getComparatorSignal();
      return device.getComparatorMode() == ComparatorMode.OFF ? 0 : s;
    }
    return 0;
  }

  @Override
  protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    if (level.isClientSide()) {
      return ItemInteractionResult.SUCCESS;
    }
    BlockEntity be = level.getBlockEntity(pos);
    if (be instanceof MenuProvider menuProvider) {
      player.openMenu(menuProvider, pos);
    }
    return ItemInteractionResult.CONSUME;
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState state) {
    return true;
  }
}
