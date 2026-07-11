package com.hypothetic.ten4.lib.network;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.lib.blockentity.ComparatorMode;
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

public record DeviceConfigPayload(BlockPos pos, int key, int value) implements CustomPacketPayload {
  public static final Type<DeviceConfigPayload> TYPE = new Type<>(Ten4.id("device_cfg"));
  public static final int SIGNAL_MODE = 0;
  public static final int STRICT_INPUT = 1;
  public static final int COMPARATOR_MODE = 2;
  public static final int REQUEST_RATE = 3;
  public static final StreamCodec<RegistryFriendlyByteBuf, DeviceConfigPayload> CODEC = new StreamCodec<>() {
    @Override
    public DeviceConfigPayload decode(RegistryFriendlyByteBuf buf) {
      return new DeviceConfigPayload(buf.readBlockPos(), buf.readByte(), buf.readInt());
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf, DeviceConfigPayload pkt) {
      buf.writeBlockPos(pkt.pos);
      buf.writeByte(pkt.key);
      buf.writeInt(pkt.value);
    }
  };

  public static void handle(DeviceConfigPayload pkt, IPayloadContext ctx) {
    if (ctx.player() instanceof ServerPlayer sp) {
      ServerLevel level = sp.serverLevel();
      if (level.isLoaded(pkt.pos)) {
        BlockEntity be = level.getBlockEntity(pkt.pos);
        if (be instanceof AbstractDeviceBlockEntity device) {
          switch (pkt.key) {
            case SIGNAL_MODE -> device.setSigMode(SignalMode.of(pkt.value));
            case STRICT_INPUT -> device.setStrictInput(pkt.value != 0);
            case COMPARATOR_MODE -> device.setComparatorMode(ComparatorMode.of(pkt.value));
            case REQUEST_RATE -> device.setRequestRate(pkt.value);
          }
        }
      }
    }
  }

  @Override
  public Type<DeviceConfigPayload> type() {
    return TYPE;
  }
}
