package com.hypothetic.ten4.api.network.duct;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.duct.ItemDuctBlockEntity;
import com.hypothetic.ten4.api.transmission.item.ItemTransmitter.TransitEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

public record DuctItemPayload(BlockPos pos, @Nullable TransitEntry entry) implements CustomPacketPayload {
  public static final Type<DuctItemPayload> TYPE = new Type<>(Ten4.id("item_extracted"));

  public static final StreamCodec<RegistryFriendlyByteBuf, DuctItemPayload> CODEC = new StreamCodec<>() {
    @Override
    public DuctItemPayload decode(RegistryFriendlyByteBuf buf) {
      BlockPos pos = buf.readBlockPos();
      boolean hasEntry = buf.readBoolean();
      if (!hasEntry) {
        return new DuctItemPayload(pos, null);
      }
      TransitEntry e = new TransitEntry();
      e.id = buf.readVarInt();
      e.stack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
      e.progress = buf.readVarInt();
      e.entrySide = buf.readByte();
      e.exitSide = buf.readByte();
      boolean hasRoute = buf.readBoolean();
      if (hasRoute) {
        e.route = buf.readByteArray();
        e.index = buf.readVarInt();
      }
      return new DuctItemPayload(pos, e);
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf, DuctItemPayload pkt) {
      buf.writeBlockPos(pkt.pos);
      TransitEntry e = pkt.entry;
      buf.writeBoolean(e != null);
      if (e == null) {
        return;
      }
      buf.writeVarInt(e.id);
      ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, e.stack);
      buf.writeVarInt(e.progress);
      buf.writeByte(e.entrySide);
      buf.writeByte(e.exitSide);
      boolean hasRoute = e.route != null;
      buf.writeBoolean(hasRoute);
      if (hasRoute) {
        buf.writeByteArray(e.route);
        buf.writeVarInt(e.index);
      }
    }
  };

  public static void handle(DuctItemPayload pkt, IPayloadContext ctx) {
    Level level = ctx.player().level();
    if (level.getBlockEntity(pkt.pos) instanceof ItemDuctBlockEntity duct) {
      duct.transmitter.setSyncedEntry(pkt.entry, level.getGameTime());
    }
  }

  @Override
  public Type<DuctItemPayload> type() {
    return TYPE;
  }
}
