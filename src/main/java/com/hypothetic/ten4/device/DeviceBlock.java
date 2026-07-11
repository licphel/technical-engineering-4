package com.hypothetic.ten4.device;

import com.hypothetic.ten4.lib.block.BuiltinBlockStates;
import com.hypothetic.ten4.lib.blockentity.ComparatorMode;
import com.hypothetic.ten4.lib.blockentity.IDropContent;
import com.hypothetic.ten4.lib.blockentity.SimpleTicker;
import com.hypothetic.ten4.lib.blockentity.device.AbstractDeviceBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class DeviceBlock extends BaseEntityBlock {
  private final BiFunction<BlockPos, BlockState, ? extends BlockEntity> factory;
  private final Supplier<BlockEntityType<?>> bet;

  public DeviceBlock(Properties p, Supplier<BlockEntityType<?>> bet,
                     BiFunction<BlockPos, BlockState, ? extends BlockEntity> factory) {
    super(p);
    this.bet = bet;
    this.factory = factory;
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return simpleCodec(pp -> new DeviceBlock(pp, bet, factory));
  }

  @Override
  public RenderShape getRenderShape(BlockState state) {
    return RenderShape.MODEL;
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return factory.apply(pos, state);
  }

  @Override
  public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level l, BlockState s, BlockEntityType<T> t) {
    return t == bet.get() ? new SimpleTicker<>() : null;
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState state) { return true; }

  @Override
  public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
    var be = level.getBlockEntity(pos);
    if (be instanceof AbstractDeviceBlockEntity device) {
      int s = device.getComparatorSignal();
      return device.getComparatorMode() == ComparatorMode.OFF ? 0 : s;
    }
    return 0;
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
  protected void onRemove(BlockState old, Level level, BlockPos pos, BlockState newState, boolean flag) {
    if (!old.is(newState.getBlock())) {
      BlockEntity be = level.getBlockEntity(pos);
      if (be instanceof IDropContent idc) {
        NonNullList<ItemStack> loot = NonNullList.create();
        idc.getLoot(loot);
        Containers.dropContents(level, pos, loot);
        level.updateNeighbourForOutputSignal(pos, old.getBlock());
      }
    }

    super.onRemove(old, level, pos, newState, flag);
  }

  @Override
  protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    if (level.isClientSide()) {
      return ItemInteractionResult.SUCCESS;
    }
    BlockEntity be = level.getBlockEntity(pos);
    if (be instanceof AbstractDeviceBlockEntity mbe) {
      player.openMenu(mbe, pos);
    }
    return ItemInteractionResult.CONSUME;
  }
}
