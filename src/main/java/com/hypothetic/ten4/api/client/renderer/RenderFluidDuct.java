package com.hypothetic.ten4.api.client.renderer;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.internet.FluidDuctBlockEntity;
import com.hypothetic.ten4.api.transmission.ConnectionType;
import com.hypothetic.ten4.util.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class RenderFluidDuct extends RenderTransmitterBlock<FluidDuctBlockEntity> {
  private static final ResourceLocation MODEL_CORE = Ten4.id("block/connectable/connectable_core");
  private static final ResourceLocation MODEL_PART = Ten4.id("block/connectable/connectable_part");
  private static final ResourceLocation MODEL_PULL = Ten4.id("block/connectable/connectable_pull");
  private static final ResourceLocation MODEL_PUSH = Ten4.id("block/connectable/connectable_push");
  private static final ResourceLocation INNER_CORE = Ten4.id("block/connectable/inner_core");
  private static final ResourceLocation INNER_PART = Ten4.id("block/connectable/inner_part");
  private static final TextureAtlasSprite WATER = RenderHelper.getBlockSprite(
      ResourceLocation.withDefaultNamespace("block/water_still"));
  private final ResourceLocation texture;
  private final DuctModelBaker innerBaker = new DuctModelBaker(INNER_CORE, INNER_PART, INNER_PART, INNER_PART);

  public RenderFluidDuct(BlockEntityRendererProvider.Context ctx, ResourceLocation textureName) {
    super(ctx, MODEL_CORE, MODEL_PART, MODEL_PULL, MODEL_PUSH);
    texture = textureName;
  }

  @Override
  protected ResourceLocation coreTexture() {
    return texture;
  }

  @Override
  protected ResourceLocation sideTexture() {
    return texture;
  }

  @Override
  protected boolean hasConnection(FluidDuctBlockEntity be, Direction d) {
    return be.transmitter.getConnectionType(d) != ConnectionType.NONE;
  }

  @Override
  protected String partName(FluidDuctBlockEntity be, Direction d) {
    return switch (be.transmitter.getConnectionType(d)) {
      case PULL -> "pull";
      case PUSH -> "push";
      default -> "part";
    };
  }

  @Override
  protected void renderContents(FluidDuctBlockEntity be, float pt, PoseStack pose,
                                MultiBufferSource buffers, TextureAtlasSprite cs, TextureAtlasSprite ss,
                                int light, int overlay) {
    float scale = be.transmitter.getClientScale();
    if (scale <= 0) return;

    Player player = Minecraft.getInstance().player;
    if (player == null || !player.blockPosition().closerThan(be.getBlockPos(), 32)) return;

    FluidStack fluid = be.transmitter.getSyncedFluid();
    if (fluid.isEmpty()) return;

    VertexConsumer vc = buffers.getBuffer(RenderType.translucent());
    float alpha = Math.min(scale, 0.8f);
    float s = 0.97f, off = (1 - s) / 2;
    int color = IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor(fluid);
    float r = ((color >> 16) & 0xFF) / 255f;
    float g = ((color >> 8) & 0xFF) / 255f;
    float b = (color & 0xFF) / 255f;

    pose.pushPose();
    pose.translate(off, off, off);
    pose.scale(s, s, s);
    var entry = pose.last();

    TextureAtlasSprite fs = RenderHelper.getBlockSprite(IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture());
    for (BakedQuad q : innerBaker.getPart("core", fs)) {
      vc.putBulkData(entry, q, r, g, b, alpha, light, overlay, true);
    }

    for (Direction d : Direction.values()) {
      if (be.transmitter.getConnectionType(d) != ConnectionType.NORMAL) continue;
      for (BakedQuad q : innerBaker.getPart(d.getSerializedName() + "_" + partName(be, d), fs)) {
        vc.putBulkData(entry, q, r, g, b, alpha, light, overlay, true);
      }
    }

    pose.popPose();
  }
}
