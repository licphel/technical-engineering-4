package com.hypothetic.ten4.core.block.duct;

import com.hypothetic.ten4.api.transmission.ConnectionType;
import com.hypothetic.ten4.api.transmission.ITransmitterProvider;
import com.hypothetic.ten4.api.transmission.Transmitter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class DuctInteractions {
  private DuctInteractions() {
  }

  public static ItemInteractionResult dye(@Nullable DyeColor dye, Level level, BlockPos pos) {
    BlockEntity be = level.getBlockEntity(pos);
    if (be != null) {
      Transmitter<?, ?, ?> t = findTransmitter(be);
      if (t != null) {
        t.setColor(dye);
        t.refreshConnections();
        t.rebuild();
        t.requestsUpdate();
        return ItemInteractionResult.SUCCESS;
      }
    }
    return ItemInteractionResult.FAIL;
  }

  public static ItemInteractionResult changeConnection(Level level, BlockState state, BlockPos pos, Direction side, Player player) {
    BlockEntity be = level.getBlockEntity(pos);
    if (be != null) {
      Transmitter<?, ?, ?> t = findTransmitter(be);
      if (t != null) {

        if (!state.getValue(DuctBlock.CONNECTIONS.get(side))) {
          return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // Only allow switching mode on cable-to-device connections
        if (!Transmitter.connectionBit(t.getAcceptorConnections(), side)) {
          return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        ConnectionType current = t.getConnectionTypeRaw(side);
        ConnectionType next = current.next();
        t.setConnectionTypeRaw(side, next);
        t.onModeChange(side);
        t.refreshConnections();
        t.requestsUpdate();

        MutableComponent mc = current.createGroup();
        mc.append(next.createTranslation());
        player.displayClientMessage(mc, true);
        return ItemInteractionResult.SUCCESS;
      }
    }
    return ItemInteractionResult.FAIL;
  }

  public static void updateConnections(Level level, BlockPos pos, DuctBlock ductBlock) {
    BlockState state = level.getBlockState(pos);
    for (Direction facing : Direction.values()) {
      state = state.setValue(DuctBlock.CONNECTIONS.get(facing), ductBlock.hasConnection(level, facing, pos));
    }
    level.setBlockAndUpdate(pos, state);

    BlockEntity be = level.getBlockEntity(pos);
    if (be instanceof ITransmitterProvider duct) {
      Transmitter<?, ?, ?> t = duct.getTransmitter();
      t.refreshConnections();
    }
  }

  private static @Nullable Transmitter<?, ?, ?> findTransmitter(BlockEntity be) {
    if (be instanceof ITransmitterProvider duct) {
      return duct.getTransmitter();
    }
    return null;
  }
}
