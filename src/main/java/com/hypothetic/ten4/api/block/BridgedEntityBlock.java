package com.hypothetic.ten4.api.block;

import com.hypothetic.ten4.api.blockentity.*;
import com.hypothetic.ten4.api.registry.BlockEntityBridges;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.OptionalInt;

public abstract class BridgedEntityBlock extends BaseEntityBlock {
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
  public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    super.setPlacedBy(level, pos, state, placer, stack);

    BlockEntity be = level.getBlockEntity(pos);

    if (be instanceof IRecoverablePlace irp) {
      irp.onPlaced(placer, stack);
    }
  }

  @Override
  protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean flag) {
    ILootProvider.onRemoved(state, newState, level, pos);
    super.onRemove(state, level, pos, newState, flag);
  }

  @Override
  protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    if (level.isClientSide()) {
      return ItemInteractionResult.SUCCESS;
    }
    BlockEntity be = level.getBlockEntity(pos);
    if (be instanceof IClickFluidInteractable fluid) {
      if (fluid.tryInteractFluid(level, pos, player, hand, stack, hit)) {
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
      }
    }
    if (be instanceof MenuProvider menuProvider) {
      OptionalInt oi = player.openMenu(menuProvider, pos);
      if (oi.isPresent()) {
        return ItemInteractionResult.SUCCESS;
      }
    }
    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState state) {
    return true;
  }

  @Override
  protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
    BlockEntity be = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);

    if (be instanceof ILootProvider lp) {
      NonNullList<ItemStack> loot = NonNullList.create();
      loot.add(lp.createSelfStack());
      return loot;
    }

    // Fallback
    return super.getDrops(state, params);
  }

  @Override
  protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
    return IRedstoneBlockEntity.getAnalogSignal(level, pos);
  }

  @Override
  public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
    return IRedstoneBlockEntity.canConnect(level, pos, direction);
  }

  @Override
  public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
    int light0 = super.getLightEmission(state, level, pos);
    BlockEntity be = level.getBlockEntity(pos);
    if (be instanceof ILightProvider lp) {
      return lp.getLight();
    }
    return light0;
  }
}
