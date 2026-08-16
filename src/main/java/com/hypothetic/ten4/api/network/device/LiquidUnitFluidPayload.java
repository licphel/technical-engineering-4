package com.hypothetic.ten4.api.network.device;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.core.blockentity.LiquidUnitBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record LiquidUnitFluidPayload(BlockPos pos, float scale, FluidStack fluid) implements CustomPacketPayload {
  public static final Type<LiquidUnitFluidPayload> TYPE = new Type<>(Ten4.id("liquid_unit_fluid"));
  public static final StreamCodec<RegistryFriendlyByteBuf, LiquidUnitFluidPayload> CODEC = new StreamCodec<>() {
    @Override
    public LiquidUnitFluidPayload decode(RegistryFriendlyByteBuf buf) {
      return new LiquidUnitFluidPayload(buf.readBlockPos(), buf.readFloat(), FluidStack.OPTIONAL_STREAM_CODEC.decode(buf));
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf, LiquidUnitFluidPayload pkt) {
      buf.writeBlockPos(pkt.pos);
      buf.writeFloat(pkt.scale);
      FluidStack.OPTIONAL_STREAM_CODEC.encode(buf, pkt.fluid);
    }
  };

  public static void handle(LiquidUnitFluidPayload pkt, IPayloadContext ctx) {
    Level level = ctx.player().level();
    if (level.getBlockEntity(pkt.pos) instanceof LiquidUnitBlockEntity unit) {
      unit.syncClientFluid(pkt.scale, pkt.fluid);
    }
  }

  @Override
  public Type<LiquidUnitFluidPayload> type() {
    return TYPE;
  }
}
