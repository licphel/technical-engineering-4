package com.hypothetic.ten4.api.network.duct;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.transmission.FluidDuctBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Network-level fluid scale sync (0..1) + fluid type for color.
 */
public record DuctFluidPayload(BlockPos pos, float scale, FluidStack fluid) implements CustomPacketPayload {
  public static final Type<DuctFluidPayload> TYPE = new Type<>(Ten4.id("duct_fluid"));
  public static final StreamCodec<RegistryFriendlyByteBuf, DuctFluidPayload> CODEC = new StreamCodec<>() {
    @Override
    public DuctFluidPayload decode(RegistryFriendlyByteBuf buf) {
      return new DuctFluidPayload(buf.readBlockPos(), buf.readFloat(), FluidStack.OPTIONAL_STREAM_CODEC.decode(buf));
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf, DuctFluidPayload pkt) {
      buf.writeBlockPos(pkt.pos);
      buf.writeFloat(pkt.scale);
      FluidStack.OPTIONAL_STREAM_CODEC.encode(buf, pkt.fluid);
    }
  };

  public static void handle(DuctFluidPayload pkt, IPayloadContext ctx) {
    Level level = ctx.player().level();
    if (level.getBlockEntity(pkt.pos) instanceof FluidDuctBlockEntity duct) {
      duct.transmitter.syncClientFluid(pkt.scale, pkt.fluid);
    }
  }

  @Override
  public Type<DuctFluidPayload> type() {
    return TYPE;
  }
}
