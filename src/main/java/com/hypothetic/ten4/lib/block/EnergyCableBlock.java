package com.hypothetic.ten4.lib.block;

import com.google.common.collect.Maps;
import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.init.ModBlockEntities;
import com.hypothetic.ten4.lib.blockentity.internet.EnergyCableBlockEntity;
import com.hypothetic.ten4.lib.capability.internet.ConnectionType;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class EnergyCableBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

  private final Supplier<BlockEntityType<EnergyCableBlockEntity>> blockEntityType;

  public static final Map<Direction, IntegerProperty> DIR_PROPS;

  static {
    Map<Direction, IntegerProperty> map = Maps.newEnumMap(Direction.class);
    map.put(Direction.NORTH, IntegerProperty.create("north", 0, 2));
    map.put(Direction.EAST, IntegerProperty.create("east", 0, 2));
    map.put(Direction.SOUTH, IntegerProperty.create("south", 0, 2));
    map.put(Direction.WEST, IntegerProperty.create("west", 0, 2));
    map.put(Direction.UP, IntegerProperty.create("up", 0, 2));
    map.put(Direction.DOWN, IntegerProperty.create("down", 0, 2));
    DIR_PROPS = Collections.unmodifiableMap(map);
  }

  private static final VoxelShape SHAPE = Block.box(4, 4, 4, 12, 12, 12);

  public EnergyCableBlock(Properties props, Supplier<BlockEntityType<EnergyCableBlockEntity>> bet) {
    super(props);
    this.blockEntityType = bet;
    BlockState state = defaultBlockState()
        .setValue(BuiltinBlockStates.ACTIVE, false)
        .setValue(BlockStateProperties.WATERLOGGED, false);
    for (IntegerProperty prop : DIR_PROPS.values()) {
      state = state.setValue(prop, 0);
    }
    registerDefaultState(state);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return simpleCodec(p -> new EnergyCableBlock(p, ModBlockEntities.GLASS_ENERGY_CABLE::get));
  }

  @Override
  public RenderShape getRenderShape(BlockState state) {
    return RenderShape.ENTITYBLOCK_ANIMATED;
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
    return SHAPE;
  }

  @Override
  public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
    return SHAPE;
  }

  // --- BE ---

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new EnergyCableBlockEntity(pos, state, EnergyCableBlockEntity.CAPACITY);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
    if (type != blockEntityType.get()) {
      return null;
    }
    return (l, p, s, be) -> EnergyCableBlockEntity.tick((EnergyCableBlockEntity) be);
  }

  // --- Connection logic ---

  public void updateConnections(Level level, BlockPos pos) {
    BlockState state = level.getBlockState(pos);
    for (Direction facing : Direction.values()) {
      state = state.setValue(DIR_PROPS.get(facing), connectType(level, facing, pos));
    }
    level.setBlock(pos, state, 3);

    // sync transmitter internal state
    BlockEntity be = level.getBlockEntity(pos);
    if (be instanceof EnergyCableBlockEntity cable) {
      cable.transmitter.refreshConnections();
    }
  }

  /** 0 = none, 1 = cable-to-cable, 2 = cable-to-machine */
  public int connectType(Level level, Direction facing, BlockPos pos) {
    BlockPos neighborPos = pos.relative(facing);
    BlockState neighborState = level.getBlockState(neighborPos);

    boolean canConnect = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos,
        neighborState, null, facing.getOpposite()) != null;

    if (canConnect) {
      return neighborState.getBlock() instanceof EnergyCableBlock ? 1 : 2;
    }
    return 0;
  }

  // --- Drops ---

  @Override
  public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
    return List.of(asItem().getDefaultInstance());
  }

  // --- BlockState ---

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(BuiltinBlockStates.ACTIVE);
    builder.add(DIR_PROPS.values().toArray(new Property<?>[0]));
    builder.add(BlockStateProperties.WATERLOGGED);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext ctx) {
    BlockState state = defaultBlockState();
    for (Direction facing : Direction.values()) {
      state = state.setValue(DIR_PROPS.get(facing), connectType(ctx.getLevel(), facing, ctx.getClickedPos()));
    }
    FluidState fluid = ctx.getLevel().getFluidState(ctx.getClickedPos());
    return state.setValue(BuiltinBlockStates.ACTIVE, false)
        .setValue(BlockStateProperties.WATERLOGGED, fluid.getType() == Fluids.WATER);
  }

  @Override
  public FluidState getFluidState(BlockState state) {
    return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }

  @Override
  protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                             Player player, InteractionHand hand, BlockHitResult hit) {
    if (level.isClientSide()) return ItemInteractionResult.SUCCESS;

    BlockEntity be = level.getBlockEntity(pos);
    if (!(be instanceof EnergyCableBlockEntity cable)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

    Direction side = hit.getDirection();
    // Only allow switching mode on cable-to-machine connections (conn=2), not cable-to-cable (conn=1)
    if (state.getValue(DIR_PROPS.get(side)) != 2) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

    ConnectionType current = cable.transmitter.getConnectionTypeRaw(side);
    ConnectionType next = current.next();
    cable.transmitter.setConnectionTypeRaw(side, next);
    cable.transmitter.onModeChange(side);
    cable.transmitter.refreshConnections();
    cable.transmitter.requestsUpdate();

    MutableComponent mc = Component.translatable(Ten4.getLangKey("misc.connection_type"));
    mc.append(next.getComponent());
    player.displayClientMessage(mc, true);
    return ItemInteractionResult.SUCCESS;
  }

  @Override
  public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
                               BlockPos neighborPos, boolean moved) {
    super.neighborChanged(state, level, pos, neighborBlock, neighborPos, moved);
    if (!level.isClientSide()) {
      updateConnections(level, pos);
    }
  }
}
