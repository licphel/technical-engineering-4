package com.hypothetic.ten4.core.block;

import com.google.common.collect.Maps;
import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.internet.FluidDuctBlockEntity;
import com.hypothetic.ten4.api.transmission.ConnectionType;
import com.hypothetic.ten4.api.transmission.ITransmitterProvider;
import com.hypothetic.ten4.api.transmission.Transmitter;
import com.hypothetic.ten4.api.transmission.TransmitterNetworkRegistry;
import com.hypothetic.ten4.api.ILootProvider;
import com.hypothetic.ten4.core.item.PaintItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
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
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class DuctBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
  public static final Map<Direction, BooleanProperty> DIR_PROPS;
  private static final VoxelShape SHAPE = Block.box(4, 4, 4, 12, 12, 12);

  static {
    Map<Direction, BooleanProperty> map = Maps.newEnumMap(Direction.class);
    map.put(Direction.NORTH, BlockStateProperties.NORTH);
    map.put(Direction.EAST, BlockStateProperties.EAST);
    map.put(Direction.SOUTH, BlockStateProperties.SOUTH);
    map.put(Direction.WEST, BlockStateProperties.WEST);
    map.put(Direction.UP, BlockStateProperties.UP);
    map.put(Direction.DOWN, BlockStateProperties.DOWN);
    DIR_PROPS = Collections.unmodifiableMap(map);
  }

  protected DuctBlock(Properties props) {
    super(props);
    BlockState state = defaultBlockState()
        .setValue(BuiltinBlockStates.ACTIVE, false)
        .setValue(BlockStateProperties.WATERLOGGED, false);
    for (BooleanProperty prop : DIR_PROPS.values()) {
      state = state.setValue(prop, false);
    }
    registerDefaultState(state);
  }

  @Override
  public RenderShape getRenderShape(BlockState state) {
    return RenderShape.ENTITYBLOCK_ANIMATED;
  }

  protected abstract boolean hasConnection(Level level, Direction facing, BlockPos pos);

  public void updateConnections(Level level, BlockPos pos) {
    BlockState state = level.getBlockState(pos);
    for (Direction facing : Direction.values()) {
      state = state.setValue(DIR_PROPS.get(facing), hasConnection(level, facing, pos));
    }
    level.setBlock(pos, state, 3);

    BlockEntity be = level.getBlockEntity(pos);
    if (be instanceof ITransmitterProvider duct) {
      Transmitter<?, ?, ?> t = duct.getTransmitter();
      if (t != null) {
        t.refreshConnections();
      }
    }
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
  public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable net.minecraft.world.entity.LivingEntity placer, ItemStack stack) {
    super.setPlacedBy(level, pos, state, placer, stack);
    if (!(placer instanceof Player player) || level.isClientSide()) return;
    // Auto-dye from offhand paint item
    ItemStack offhand = player.getItemInHand(InteractionHand.OFF_HAND);
    if (offhand.getItem() instanceof PaintItem paint) {
      BlockEntity be = level.getBlockEntity(pos);
      Transmitter<?, ?, ?> t = getTransmitter(be);
      if (t != null) {
        t.setColor(paint.getColor());
        t.refreshConnections();
        // Destroy old network and rebuild from all valid neighbors
        TransmitterNetworkRegistry.onTransmitterRemoved(t);
        TransmitterNetworkRegistry.joinNetwork(t);
        t.requestsUpdate();
      }
    }
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(BuiltinBlockStates.ACTIVE);
    builder.add(DIR_PROPS.values().toArray(new BooleanProperty[0]));
    builder.add(BlockStateProperties.WATERLOGGED);
  }

  protected @Nullable Transmitter<?, ?, ?> getTransmitter(BlockEntity be) {
    if (be instanceof ITransmitterProvider duct) {
      return duct.getTransmitter();
    }
    return null;
  }

  @Override
  public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
                              BlockPos neighborPos, boolean moved) {
    super.neighborChanged(state, level, pos, neighborBlock, neighborPos, moved);
    if (!level.isClientSide()) {
      updateConnections(level, pos);
    }
  }

  @Override
  protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                            Player player, InteractionHand hand, BlockHitResult hit) {
    BlockEntity be = level.getBlockEntity(pos);
    Transmitter<?, ?, ?> transmitter = getTransmitter(be);
    if (transmitter == null) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    Direction side = hit.getDirection();

    if (!level.isClientSide() && stack.isDamageableItem()) {
      var net = transmitter.getNetwork();
      String id = net != null ? net.getUUID().toString().substring(0, 8) : "none";
      String color = transmitter.getColor() != null ? transmitter.getColor().getName() : "none";
      if (this instanceof FluidDuctBlock fd) {
        FluidDuctBlockEntity fbe = (FluidDuctBlockEntity) be;
        player.displayClientMessage(
            Component.literal(fbe.transmitter.getBuffer().toString()), false);
      }
      player.displayClientMessage(
          Component.literal("Net: " + id + "  Color: " + color + "  Orphan: " + transmitter.isOrphan()), true);
      return ItemInteractionResult.SUCCESS;
    }

    // Paint item: dye the cable (separate networks by color); sponge (null) clears color
    if (stack.getItem() instanceof PaintItem paint) {
      if (!level.isClientSide()) {
        DyeColor newColor = paint.getColor();
        if (!Objects.equals(transmitter.getColor(), newColor)) {
          transmitter.setColor(newColor);
          transmitter.refreshConnections();
          // Destroy old network and rebuild from all valid neighbors (new color may split network)
          TransmitterNetworkRegistry.onTransmitterRemoved(transmitter);
          // Re-join: will find all color-compatible connected transmitters
          TransmitterNetworkRegistry.joinNetwork(transmitter);
          transmitter.requestsUpdate();
        }
      }
      return ItemInteractionResult.SUCCESS;
    }

    // Not connected. nothing to do (ConnectionType cycling)
    if (!state.getValue(DIR_PROPS.get(side))) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    // Only allow switching mode on cable-to-device connections, not cable-to-cable
    if (!Transmitter.connectionBit(transmitter.getAcceptorConnections(), side)) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    if (!level.isClientSide()) {
      ConnectionType current = transmitter.getConnectionTypeRaw(side);
      ConnectionType next = current.next();
      transmitter.setConnectionTypeRaw(side, next);
      transmitter.onModeChange(side);
      transmitter.refreshConnections();
      transmitter.requestsUpdate();

      MutableComponent mc = Component.translatable(Ten4.getLangKey("misc.connection_type"));
      mc.append(next.getComponent());
      player.displayClientMessage(mc, true);
    }
    return ItemInteractionResult.SUCCESS;
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
  public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean flag) {
    if (!state.is(newState.getBlock())) {
      BlockEntity be = level.getBlockEntity(pos);
      if (be instanceof ILootProvider provider) {
        NonNullList<ItemStack> loot = NonNullList.create();
        provider.getLoot(loot);
        Containers.dropContents(level, pos, loot);
      }
    }
    super.onRemove(state, level, pos, newState, flag);
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
