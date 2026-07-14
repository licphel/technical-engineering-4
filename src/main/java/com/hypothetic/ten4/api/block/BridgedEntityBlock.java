package com.hypothetic.ten4.api.block;

import com.hypothetic.ten4.api.blockentity.ILootProvider;
import com.hypothetic.ten4.api.blockentity.IRedstoneBlockEntity;
import com.hypothetic.ten4.api.blockentity.SimpleTicker;
import com.hypothetic.ten4.api.registry.BlockEntityBridges;
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

public class BridgedEntityBlock extends BaseEntityBlock {
  boolean serverTicker;
  boolean clientTicker;

  public BridgedEntityBlock(Properties props) {
    super(props);
  }

  public BridgedEntityBlock tickBothSide() {
    this.serverTicker = true;
    this.clientTicker = true;
    return this;
  }

  public BridgedEntityBlock tickServer() {
    this.serverTicker = true;
    return this;
  }

  public BridgedEntityBlock tickClient() {
    this.clientTicker = true;
    return this;
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return simpleCodec(pp -> new BridgedEntityBlock(properties));
  }

  @Override
  protected RenderShape getRenderShape(BlockState p_49232_) {
    // By default, BaseEntityBlock uses INVISIBLE
    return RenderShape.MODEL;
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    @Nullable BlockEntityType<?> type = BlockEntityBridges.getEntity(this);
    return type != null ? type.create(pos, state) : null;
  }

  @Override
  public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
    if (level.isClientSide()) {
      return clientTicker ? new SimpleTicker<>() : null;
    }
    return serverTicker ? new SimpleTicker<>() : null;
  }

  @Override
  protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean flag) {
    ILootProvider.onRemoved(state, newState, level, pos);
    super.onRemove(state, level, pos, newState, flag);
  }

  @Override
  protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
    return IRedstoneBlockEntity.getAnalogSignal(level, pos);
  }

  @Override
  public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
    return IRedstoneBlockEntity.canConnect(level, pos, direction);
  }
}
