package com.hypothetic.ten4.api.network.device;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.device.AbstractDeviceBlockEntity;
import com.hypothetic.ten4.api.blockentity.device.FaceMode;
import com.hypothetic.ten4.api.blockentity.device.FaceModePacker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class IoFacePayload implements CustomPacketPayload {
  public static final Type<IoFacePayload> TYPE = new Type<>(Ten4.id("io_face"));
  public static final StreamCodec<RegistryFriendlyByteBuf, IoFacePayload> CODEC = new StreamCodec<>() {
    @Override
    public IoFacePayload decode(RegistryFriendlyByteBuf buf) {
      return new IoFacePayload(buf.readBlockPos(), buf.readByte(), buf.readInt());
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf, IoFacePayload pkt) {
      buf.writeBlockPos(pkt.pos);
      buf.writeByte(pkt.type);
      buf.writeInt(pkt.packed);
    }
  };
  public final BlockPos pos;
  public final int type;
  public final int packed;

  public IoFacePayload(BlockPos pos, int type, int packed) {
    this.pos = pos;
    this.type = type;
    this.packed = packed;
  }

  public static void handle(IoFacePayload pkt, IPayloadContext ctx) {
    if (ctx.player() instanceof ServerPlayer sp) {
      ServerLevel level = sp.serverLevel();
      if (level.isLoaded(pkt.pos)) {
        BlockEntity be = level.getBlockEntity(pkt.pos);
        if (be instanceof AbstractDeviceBlockEntity device) {
          for (Direction d : Direction.values()) {
            FaceMode mode = FaceModePacker.get(pkt.packed, d);
            switch (pkt.type) {
              case 0 -> device.setEnergyFaceMode(d, mode);
              case 1 -> device.setItemFaceMode(d, mode);
              case 2 -> device.setFluidFaceMode(d, mode);
            }
          }
        }
      }
    }
  }

  @Override
  public Type<IoFacePayload> type() {
    return TYPE;
  }
}
