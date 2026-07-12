package com.hypothetic.ten4.api.network.duct;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.internet.EnergyDuctBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DuctEnergyPayload(BlockPos pos, long buffer, long capacity) implements CustomPacketPayload {
  public static final Type<DuctEnergyPayload> TYPE = new Type<>(Ten4.id("duct_energy"));
  public static final StreamCodec<RegistryFriendlyByteBuf, DuctEnergyPayload> CODEC = new StreamCodec<>() {
    @Override
    public DuctEnergyPayload decode(RegistryFriendlyByteBuf buf) {
      return new DuctEnergyPayload(buf.readBlockPos(), buf.readLong(), buf.readLong());
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf, DuctEnergyPayload pkt) {
      buf.writeBlockPos(pkt.pos);
      buf.writeLong(pkt.buffer);
      buf.writeLong(pkt.capacity);
    }
  };

  public static void handle(DuctEnergyPayload pkt, IPayloadContext ctx) {
    Level level = ctx.player().level();
    if (level.getBlockEntity(pkt.pos) instanceof EnergyDuctBlockEntity cable) {
      cable.transmitter.applyEnergySync(pkt.buffer, pkt.capacity);
    }
  }

  @Override
  public Type<DuctEnergyPayload> type() {
    return TYPE;
  }
}
