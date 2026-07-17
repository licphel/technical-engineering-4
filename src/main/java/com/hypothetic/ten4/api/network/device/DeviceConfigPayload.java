package com.hypothetic.ten4.api.network.device;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.device.AbstractDeviceBlockEntity;
import com.hypothetic.ten4.api.blockentity.device.ComparatorMode;
import com.hypothetic.ten4.api.blockentity.device.SecurityMode;
import com.hypothetic.ten4.api.blockentity.device.SignalMode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DeviceConfigPayload(BlockPos pos, int key, int value, int extra) implements CustomPacketPayload {
  public static final Type<DeviceConfigPayload> TYPE = new Type<>(Ten4.id("device_config"));
  public static final int SIGNAL_MODE = 0;
  public static final int COMPARATOR_MODE = 1;
  public static final int ENERGY_AUTO_FLAGS = 2;
  public static final int ITEM_AUTO_FLAGS = 3;
  public static final int FLUID_AUTO_FLAGS = 4;
  public static final int SECURITY_MODE = 5;
  // For auto flag packets: value=type(0/1/2), extra=packed flags
  public static final StreamCodec<RegistryFriendlyByteBuf, DeviceConfigPayload> CODEC = new StreamCodec<>() {
    @Override
    public DeviceConfigPayload decode(RegistryFriendlyByteBuf buf) {
      return new DeviceConfigPayload(buf.readBlockPos(), buf.readByte(), buf.readInt(), buf.readInt());
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf, DeviceConfigPayload pkt) {
      buf.writeBlockPos(pkt.pos);
      buf.writeByte(pkt.key);
      buf.writeInt(pkt.value);
      buf.writeInt(pkt.extra);
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
            case COMPARATOR_MODE -> device.setComparatorMode(ComparatorMode.of(pkt.value));
            case ENERGY_AUTO_FLAGS -> device.setRawEnergyAutoFlags(pkt.value);
            case ITEM_AUTO_FLAGS -> device.setRawItemAutoFlags(pkt.value);
            case FLUID_AUTO_FLAGS -> device.setRawFluidAutoFlags(pkt.value);
            case SECURITY_MODE -> device.setSecurityMode(SecurityMode.of(pkt.value), sp.getUUID());
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
