package com.hypothetic.ten4.core.block.duct;

import com.hypothetic.ten4.api.block.BridgedEntityBlock;
import com.hypothetic.ten4.api.transmission.ITransmitterProvider;
import com.hypothetic.ten4.core.block.BuiltinBlockStates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class DuctBlock extends BridgedEntityBlock implements SimpleWaterloggedBlock {
  private static final VoxelShape SHAPE = Block.box(4, 4, 4, 12, 12, 12);

  protected DuctBlock(Properties props) {
    super(props);
    registerDefaultState(defaultBlockState()
        .setValue(BuiltinBlockStates.ACTIVE, false)
        .setValue(BlockStateProperties.WATERLOGGED, false));
  }

  @Override
  public RenderShape getRenderShape(BlockState state) {
    return RenderShape.ENTITYBLOCK_ANIMATED;
  }

  public abstract boolean hasConnection(Level level, Direction facing, BlockPos pos);

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext ctx) {
    FluidState fluid = ctx.getLevel().getFluidState(ctx.getClickedPos());
    return defaultBlockState()
        .setValue(BuiltinBlockStates.ACTIVE, false)
        .setValue(BlockStateProperties.WATERLOGGED, fluid.getType() == Fluids.WATER);
  }

  @Override
  public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    super.setPlacedBy(level, pos, state, placer, stack);
    if (!(placer instanceof Player player) || level.isClientSide()) {
      return;
    }

    ItemStack offhand = player.getItemInHand(InteractionHand.OFF_HAND);
    if (offhand.getItem() instanceof DyeItem dye) {
      DuctInteractions.dye(dye.getDyeColor(), level, pos);
    }
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(BuiltinBlockStates.ACTIVE, BlockStateProperties.WATERLOGGED);
  }

  @Override
  public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
                              BlockPos neighborPos, boolean moved) {
    super.neighborChanged(state, level, pos, neighborBlock, neighborPos, moved);
    if (!level.isClientSide()) {
      BlockEntity be = level.getBlockEntity(pos);
      if (be instanceof ITransmitterProvider duct) {
        duct.getTransmitter().refreshConnections();
      }
    }
  }

  @Override
  protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                            Player player, InteractionHand hand, BlockHitResult hit) {
    if (level.isClientSide()) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    if (stack.is(Items.PAPER) && hand == InteractionHand.MAIN_HAND) {
      return DuctInteractions.dye(null, level, pos);
    }

    if (stack.getItem() instanceof DyeItem dye && hand == InteractionHand.MAIN_HAND) {
      return DuctInteractions.dye(dye.getDyeColor(), level, pos);
    }

    final TagKey<Item> WRENCH = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:tools/wrench"));
    if (stack.is(WRENCH) && hand == InteractionHand.MAIN_HAND) {
      return DuctInteractions.changeConnection(level, pos, hit.getDirection(), player);
    }

    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
  }

  @Override
  public FluidState getFluidState(BlockState state) {
    return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }

  @Override
  public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
    return List.of(asItem().getDefaultInstance());
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
    return SHAPE;
  }

  @Override
  public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
    return SHAPE;
  }
}
