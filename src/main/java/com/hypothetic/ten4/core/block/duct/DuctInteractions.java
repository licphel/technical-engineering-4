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

  public static ItemInteractionResult changeConnection(Level level, BlockPos pos, Direction side, Player player) {
    BlockEntity be = level.getBlockEntity(pos);
    if (be == null) return ItemInteractionResult.FAIL;
    Transmitter<?, ?, ?> t = findTransmitter(be);
    if (t == null) return ItemInteractionResult.FAIL;

    BlockEntity neighborBe = level.getBlockEntity(pos.relative(side));

    // Duct-to-duct: NONE ~ NORMAL, both sides together
    if (neighborBe instanceof ITransmitterProvider ntb) {
      Transmitter<?, ?, ?> nt = ntb.getTransmitter();
      ConnectionType neighborType = nt.getConnectionTypeRaw(side.getOpposite());
      ConnectionType next;

      if (t.getConnectionTypeRaw(side) == ConnectionType.NONE || neighborType == ConnectionType.NONE) {
        t.setConnectionTypeRaw(side, ConnectionType.NORMAL);
        nt.setConnectionTypeRaw(side.getOpposite(), ConnectionType.NORMAL);
        next = ConnectionType.NORMAL;
      } else {
        t.setConnectionTypeRaw(side, ConnectionType.NONE);
        next = ConnectionType.NONE;
      }
      t.onModeChange(side);
      t.refreshConnections();
      t.rebuild();
      t.requestsUpdate();
      nt.onModeChange(side.getOpposite());
      nt.refreshConnections();
      nt.rebuild();
      nt.requestsUpdate();
      player.displayClientMessage(ConnectionType.NORMAL.createGroup().append(next.createTranslation()), true);
      return ItemInteractionResult.SUCCESS;
    }

    // Duct-to-device: 4-way cycle NONE ~ NORMAL ~ PUSH ~ PULL
    if (Transmitter.connectionBit(t.getAcceptorConnections(), side)) {
      ConnectionType current = t.getConnectionTypeRaw(side);
      ConnectionType next = current.next();
      t.setConnectionTypeRaw(side, next);
      t.onModeChange(side);
      t.refreshConnections();
      t.rebuild();
      t.requestsUpdate();
      MutableComponent mc = current.createGroup();
      mc.append(next.createTranslation());
      player.displayClientMessage(mc, true);
      return ItemInteractionResult.SUCCESS;
    }

    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
  }

  private static @Nullable Transmitter<?, ?, ?> findTransmitter(BlockEntity be) {
    if (be instanceof ITransmitterProvider duct) {
      return duct.getTransmitter();
    }
    return null;
  }
}
