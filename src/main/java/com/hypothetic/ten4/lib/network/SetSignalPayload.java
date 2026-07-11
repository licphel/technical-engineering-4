package com.hypothetic.ten4.lib.network;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.lib.blockentity.SignalMode;
import com.hypothetic.ten4.lib.blockentity.device.AbstractDeviceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetSignalPayload(BlockPos pos, SignalMode mode) implements CustomPacketPayload {
  public static final Type<SetSignalPayload> TYPE = new Type<>(Ten4.id("set_signal"));
  public static final StreamCodec<RegistryFriendlyByteBuf, SetSignalPayload> CODEC = new StreamCodec<>() {
    @Override
    public SetSignalPayload decode(RegistryFriendlyByteBuf buf) {
      return new SetSignalPayload(buf.readBlockPos(), buf.readEnum(SignalMode.class));
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf, SetSignalPayload pkt) {
      buf.writeBlockPos(pkt.pos);
      buf.writeEnum(pkt.mode);
    }
  };

  public static void handle(SetSignalPayload pkt, IPayloadContext ctx) {
    if (ctx.player() instanceof ServerPlayer sp) {
      ServerLevel level = sp.serverLevel();
      if (level.isLoaded(pkt.pos)) {
        BlockEntity be = level.getBlockEntity(pkt.pos);
        if (be instanceof AbstractDeviceBlockEntity device) {
          device.setSigMode(pkt.mode);
        }
      }
    }
  }

  @Override
  public Type<SetSignalPayload> type() {
    return TYPE;
  }
}
