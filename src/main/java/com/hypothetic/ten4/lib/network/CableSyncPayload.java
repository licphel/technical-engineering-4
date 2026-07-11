package com.hypothetic.ten4.lib.network;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.lib.blockentity.internet.EnergyCableBlockEntity;
import com.hypothetic.ten4.lib.capability.internet.ConnectionType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CableSyncPayload(BlockPos pos, byte conn, byte acc, ConnectionType[] types,
                                long buffer, long capacity) implements CustomPacketPayload {

  public static final Type<CableSyncPayload> TYPE = new Type<>(Ten4.id("cable_sync"));

  public static final StreamCodec<RegistryFriendlyByteBuf, CableSyncPayload> CODEC = new StreamCodec<>() {
    @Override
    public CableSyncPayload decode(RegistryFriendlyByteBuf buf) {
      BlockPos pos = buf.readBlockPos();
      byte conn = buf.readByte();
      byte acc = buf.readByte();
      ConnectionType[] types = new ConnectionType[6];
      for (int i = 0; i < 6; i++) types[i] = ConnectionType.VALUES[buf.readByte() % ConnectionType.VALUES.length];
      return new CableSyncPayload(pos, conn, acc, types, buf.readLong(), buf.readLong());
    }
    @Override
    public void encode(RegistryFriendlyByteBuf buf, CableSyncPayload pkt) {
      buf.writeBlockPos(pkt.pos);
      buf.writeByte(pkt.conn);
      buf.writeByte(pkt.acc);
      for (ConnectionType t : pkt.types) buf.writeByte(t.ordinal());
      buf.writeLong(pkt.buffer);
      buf.writeLong(pkt.capacity);
    }
  };

  @Override public Type<CableSyncPayload> type() { return TYPE; }

  public static void handle(CableSyncPayload pkt, IPayloadContext ctx) {
    Level level = ctx.player().level();
    if (level.getBlockEntity(pkt.pos) instanceof EnergyCableBlockEntity cable) {
      cable.transmitter.applySyncData(pkt.conn, pkt.acc, pkt.types, pkt.buffer, pkt.capacity);
    }
  }
}
