package com.hypothetic.ten4.api.network.device;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.device.AbstractDeviceBlockEntity;
import com.hypothetic.ten4.api.blockentity.device.SignalMode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SigmodePayload(BlockPos pos, SignalMode mode) implements CustomPacketPayload {
  public static final Type<SigmodePayload> TYPE = new Type<>(Ten4.id("sigmode"));
  public static final StreamCodec<RegistryFriendlyByteBuf, SigmodePayload> CODEC = new StreamCodec<>() {
    @Override
    public SigmodePayload decode(RegistryFriendlyByteBuf buf) {
      return new SigmodePayload(buf.readBlockPos(), buf.readEnum(SignalMode.class));
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf, SigmodePayload pkt) {
      buf.writeBlockPos(pkt.pos);
      buf.writeEnum(pkt.mode);
    }
  };

  public static void handle(SigmodePayload pkt, IPayloadContext ctx) {
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
  public Type<SigmodePayload> type() {
    return TYPE;
  }
}
