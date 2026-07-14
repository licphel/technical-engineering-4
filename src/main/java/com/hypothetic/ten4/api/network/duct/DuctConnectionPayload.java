package com.hypothetic.ten4.api.network.duct;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.transmission.ConnectionType;
import com.hypothetic.ten4.api.transmission.ITransmitterProvider;
import com.hypothetic.ten4.api.transmission.Transmitter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

public record DuctConnectionPayload(BlockPos pos, byte conn, byte acc,
                                    ConnectionType[] types,
                                    @Nullable DyeColor color) implements CustomPacketPayload {
  public static final Type<DuctConnectionPayload> TYPE = new Type<>(Ten4.id("duct_connection"));

  public static final StreamCodec<RegistryFriendlyByteBuf, DuctConnectionPayload> CODEC = new StreamCodec<>() {
    @Override
    public DuctConnectionPayload decode(RegistryFriendlyByteBuf buf) {
      BlockPos pos = buf.readBlockPos();
      byte conn = buf.readByte();
      byte acc = buf.readByte();
      ConnectionType[] types = new ConnectionType[6];
      for (int i = 0; i < 6; i++) {
        types[i] = ConnectionType.of(buf.readByte());
      }
      byte colorOrd = buf.readByte();
      DyeColor color = colorOrd < 0 ? null : DyeColor.values()[colorOrd % DyeColor.values().length];
      return new DuctConnectionPayload(pos, conn, acc, types, color);
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf, DuctConnectionPayload pkt) {
      buf.writeBlockPos(pkt.pos);
      buf.writeByte(pkt.conn);
      buf.writeByte(pkt.acc);
      for (ConnectionType t : pkt.types) {
        buf.writeByte(t.ordinal());
      }
      buf.writeByte(pkt.color != null ? (byte) pkt.color.ordinal() : -1);
    }
  };

  public static void handle(DuctConnectionPayload pkt, IPayloadContext ctx) {
    Level level = ctx.player().level();
    if (level.getBlockEntity(pkt.pos) instanceof ITransmitterProvider duct) {
      Transmitter<?, ?, ?> t = duct.getTransmitter();
      if (t != null) {
        t.applyConnectionSync(pkt.conn, pkt.acc, pkt.types, pkt.color);
      }
    }
  }

  @Override
  public Type<DuctConnectionPayload> type() {
    return TYPE;
  }
}
