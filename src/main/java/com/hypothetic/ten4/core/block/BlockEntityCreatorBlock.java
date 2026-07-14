package com.hypothetic.ten4.core.block;

import com.hypothetic.ten4.api.IRedstoneBlockEntity;
import com.hypothetic.ten4.api.ILootProvider;
import com.hypothetic.ten4.api.blockentity.SimpleTicker;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockEntityCreatorBlock extends BaseEntityBlock {
  @Nullable BlockEntityType.BlockEntitySupplier<?> supplier;
  boolean tickable;

  public BlockEntityCreatorBlock(Properties props) {
    super(props);
  }

  public BlockEntityCreatorBlock builder(@Nullable BlockEntityType.BlockEntitySupplier<?> supplier) {
    this.supplier = supplier;
    return this;
  }

  public BlockEntityCreatorBlock tickable(boolean tickable) {
    this.tickable = tickable;
    return this;
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return simpleCodec(pp -> new BlockEntityCreatorBlock(properties).builder(supplier));
  }

  @Override
  protected RenderShape getRenderShape(BlockState p_49232_) {
    // By default, BaseEntityBlock uses INVISIBLE
    return RenderShape.MODEL;
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    if (supplier == null) {
      return null;
    }
    return supplier.create(pos, state);
  }

  @Override
  public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
    return tickable ? new SimpleTicker<>() : null;
  }

  @Override
  protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean flag) {
    ILootProvider.onRemoved(state, newState, level, pos);
    super.onRemove(state, level, pos, newState, flag);
  }

  @Override
  public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
    return IRedstoneBlockEntity.canConnect(level, pos, direction);
  }

  @Override
  protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
    return IRedstoneBlockEntity.getAnalogSignal(level, pos);
  }
}
