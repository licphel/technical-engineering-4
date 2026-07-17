package com.hypothetic.ten4.api.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

public final class PacketDist {
  private PacketDist() {
  }

  public static void sendToNearbyPlayers(ServerLevel level, CustomPacketPayload payload,
                                         BlockPos pos, double distance) {
    Packet<?> packet = new ClientboundCustomPayloadPacket(payload);
    for (ServerPlayer player : level.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false)) {
      if (player.blockPosition().closerThan(pos, distance)) {
        player.connection.send(packet);
      }
    }
  }
}
