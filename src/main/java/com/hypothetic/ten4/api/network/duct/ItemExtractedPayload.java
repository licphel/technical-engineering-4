package com.hypothetic.ten4.api.network.duct;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.transmission.ItemDuctBlockEntity;
import com.hypothetic.ten4.api.transmission.item.ItemTransmitter.TransitEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ItemExtractedPayload(BlockPos pos, TransitEntry entry) implements CustomPacketPayload {
  public static final Type<ItemExtractedPayload> TYPE = new Type<>(Ten4.id("item_extracted"));

  public static final StreamCodec<RegistryFriendlyByteBuf, ItemExtractedPayload> CODEC = new StreamCodec<>() {
    @Override
    public ItemExtractedPayload decode(RegistryFriendlyByteBuf buf) {
      BlockPos pos = buf.readBlockPos();
      TransitEntry e = new TransitEntry();
      e.id = buf.readVarInt();
      e.stack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
      e.entrySide = buf.readByte();
      boolean hasRoute = buf.readBoolean();
      if (hasRoute) {
        e.route = buf.readByteArray();
        e.index = buf.readVarInt();
        e.exitSide = e.route.length > 0 ? e.route[0] : 0;
      }
      return new ItemExtractedPayload(pos, e);
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf, ItemExtractedPayload pkt) {
      buf.writeBlockPos(pkt.pos);
      TransitEntry e = pkt.entry;
      buf.writeVarInt(e.id);
      ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, e.stack);
      buf.writeByte(e.entrySide);
      boolean hasRoute = e.route != null;
      buf.writeBoolean(hasRoute);
      if (hasRoute) {
        buf.writeByteArray(e.route);
        buf.writeVarInt(e.index);
      }
    }
  };

  public static void handle(ItemExtractedPayload pkt, IPayloadContext ctx) {
    Level level = ctx.player().level();
    if (level.getBlockEntity(pkt.pos) instanceof ItemDuctBlockEntity duct) {
      duct.transmitter.addToClientTransit(pkt.entry);
    }
  }

  @Override
  public Type<ItemExtractedPayload> type() {
    return TYPE;
  }
}
