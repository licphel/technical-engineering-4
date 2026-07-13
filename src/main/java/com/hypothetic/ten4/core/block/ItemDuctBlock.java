package com.hypothetic.ten4.core.block;

import com.hypothetic.ten4.api.blockentity.SimpleTicker;
import com.hypothetic.ten4.api.blockentity.internet.ItemDuctBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ItemDuctBlock extends DuctBlock {
  protected final int ticksPerBlock, slots, slotCapacity;
  private final Supplier<BlockEntityType<ItemDuctBlockEntity>> beType;

  public ItemDuctBlock(Properties props, Supplier<BlockEntityType<ItemDuctBlockEntity>> beType,
                       int ticksPerBlock, int slots, int slotCapacity) {
    super(props);
    this.beType = beType;
    this.ticksPerBlock = ticksPerBlock;
    this.slots = slots;
    this.slotCapacity = slotCapacity;
  }

  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return simpleCodec(p -> new ItemDuctBlock(p, beType, ticksPerBlock, slots, slotCapacity));
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new ItemDuctBlockEntity(pos, state, beType.get(), ticksPerBlock, slots, slotCapacity);
  }

  @Override
  protected boolean hasConnection(Level level, Direction facing, BlockPos pos) {
    BlockPos neighborPos = pos.relative(facing);
    return level.getCapability(Capabilities.ItemHandler.BLOCK, neighborPos,
        level.getBlockState(neighborPos), null, facing.getOpposite()) != null;
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
    return new SimpleTicker<>();
  }
}
