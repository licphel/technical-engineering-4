package com.hypothetic.ten4.core.block;

import com.google.common.collect.Maps;
import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.core.item.PaintItem;
import com.hypothetic.ten4.registry.ModBlockEntities;
import com.hypothetic.ten4.api.blockentity.internet.EnergyDuctBlockEntity;
import com.hypothetic.ten4.api.capability.internet.ConnectionType;
import com.hypothetic.ten4.api.capability.internet.Transmitter;
import com.hypothetic.ten4.api.capability.internet.TransmitterNetworkRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
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

  private final Supplier<BlockEntityType<EnergyDuctBlockEntity>> blockEntityType;

  public static final Map<Direction, BooleanProperty> DIR_PROPS;

  static {
    Map<Direction, BooleanProperty> map = Maps.newEnumMap(Direction.class);
    map.put(Direction.NORTH, BlockStateProperties.NORTH);
    map.put(Direction.EAST,  BlockStateProperties.EAST);
    map.put(Direction.SOUTH, BlockStateProperties.SOUTH);
    map.put(Direction.WEST,  BlockStateProperties.WEST);
    map.put(Direction.UP,    BlockStateProperties.UP);
    map.put(Direction.DOWN,  BlockStateProperties.DOWN);
    DIR_PROPS = Collections.unmodifiableMap(map);
  }

  private static final VoxelShape SHAPE = Block.box(4, 4, 4, 12, 12, 12);

  public EnergyCableBlock(Properties props, Supplier<BlockEntityType<EnergyDuctBlockEntity>> bet) {
    super(props);
    this.blockEntityType = bet;
    BlockState state = defaultBlockState()
        .setValue(BuiltinBlockStates.ACTIVE, false)
        .setValue(BlockStateProperties.WATERLOGGED, false);
    for (BooleanProperty prop : DIR_PROPS.values()) {
      state = state.setValue(prop, false);
    }
    registerDefaultState(state);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return simpleCodec(p -> new EnergyCableBlock(p, ModBlockEntities.COPPER_ENERGY_DUCT::get));
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
    return new EnergyDuctBlockEntity(pos, state, EnergyDuctBlockEntity.CAPACITY);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
    if (type != blockEntityType.get()) {
      return null;
    }
    return (l, p, s, be) -> EnergyDuctBlockEntity.tick((EnergyDuctBlockEntity) be);
  }

  // --- Connection logic ---

  public void updateConnections(Level level, BlockPos pos) {
    BlockState state = level.getBlockState(pos);
    for (Direction facing : Direction.values()) {
      state = state.setValue(DIR_PROPS.get(facing), hasConnection(level, facing, pos));
    }
    level.setBlock(pos, state, 3);

    BlockEntity be = level.getBlockEntity(pos);
    if (be instanceof EnergyDuctBlockEntity cable) {
      cable.transmitter.refreshConnections();
    }
  }

  /** True if this side connects to an energy-capable neighbor (cable or machine). */
  public boolean hasConnection(Level level, Direction facing, BlockPos pos) {
    BlockPos neighborPos = pos.relative(facing);
    BlockState neighborState = level.getBlockState(neighborPos);
    return level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos,
        neighborState, null, facing.getOpposite()) != null;
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
    builder.add(DIR_PROPS.values().toArray(new BooleanProperty[0]));
    builder.add(BlockStateProperties.WATERLOGGED);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext ctx) {
    BlockState state = defaultBlockState();
    for (Direction facing : Direction.values()) {
      state = state.setValue(DIR_PROPS.get(facing), hasConnection(ctx.getLevel(), facing, ctx.getClickedPos()));
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
    BlockEntity be = level.getBlockEntity(pos);
    if (!(be instanceof EnergyDuctBlockEntity cable)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

    Direction side = hit.getDirection();

    // Paint item: dye the cable (separate networks by color); sponge (null) clears color
    if (stack.getItem() instanceof PaintItem paint) {
      if (!level.isClientSide()) {
        DyeColor newColor = paint.getColor();
        if (!Objects.equals(cable.transmitter.getColor(), newColor)) {
          cable.transmitter.setColor(newColor);
          cable.transmitter.refreshConnections();
          // invalidateTransmitter schedules network invalidation; on next tick the network
          // will split via net.invalidate() → registerOrphan for all nodes in the old network
          TransmitterNetworkRegistry.invalidateTransmitter(cable.transmitter);
          cable.transmitter.requestsUpdate();
        }
      }
      return ItemInteractionResult.SUCCESS;
    }

    // Not connected → nothing to do (ConnectionType cycling)
    if (!state.getValue(DIR_PROPS.get(side))) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

    // Only allow switching mode on cable-to-machine connections, not cable-to-cable
    if (!Transmitter.connectionBit(cable.transmitter.getAcceptorConnections(), side))
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

    if (!level.isClientSide()) {
      ConnectionType current = cable.transmitter.getConnectionTypeRaw(side);
      ConnectionType next = current.next();
      cable.transmitter.setConnectionTypeRaw(side, next);
      cable.transmitter.onModeChange(side);
      cable.transmitter.refreshConnections();
      cable.transmitter.requestsUpdate();

      MutableComponent mc = Component.translatable(Ten4.getLangKey("misc.connection_type"));
      mc.append(next.getComponent());
      player.displayClientMessage(mc, true);
    }
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
