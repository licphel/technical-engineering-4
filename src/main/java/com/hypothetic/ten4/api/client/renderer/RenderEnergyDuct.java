package com.hypothetic.ten4.api.client.renderer;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.transmission.EnergyDuctBlockEntity;
import com.hypothetic.ten4.api.transmission.ConnectionType;
import com.hypothetic.ten4.util.ClientUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class RenderEnergyDuct extends RenderTransmitterBlock<EnergyDuctBlockEntity> {
  private static final ResourceLocation MODEL_CORE = Ten4.id("block/connectable/connectable_core");
  private static final ResourceLocation MODEL_PART = Ten4.id("block/connectable/connectable_part");
  private static final ResourceLocation MODEL_PULL = Ten4.id("block/connectable/connectable_pull");
  private static final ResourceLocation MODEL_PUSH = Ten4.id("block/connectable/connectable_push");
  private static final ResourceLocation INNER_CORE = Ten4.id("block/connectable/inner_core");
  private static final ResourceLocation INNER_PART = Ten4.id("block/connectable/inner_part");
  private static final TextureAtlasSprite WATER = ClientUtil.getBlockSprite(ResourceLocation.withDefaultNamespace("block/water_still"));
  private final DuctModelBaker innerBaker = new DuctModelBaker(INNER_CORE, INNER_PART, INNER_PART, INNER_PART);

  public RenderEnergyDuct(BlockEntityRendererProvider.Context ctx, ResourceLocation textureName) {
    super(ctx, MODEL_CORE, MODEL_PART, MODEL_PULL, MODEL_PUSH,
        textureName,
        ResourceLocation.parse(textureName + "_active"));
  }

  @Override
  protected void renderContents(EnergyDuctBlockEntity be, float pt, PoseStack pose,
                                MultiBufferSource buffers, TextureAtlasSprite cs,
                                int light, int overlay) {
    float scale = be.transmitter.getClientScale();
    if (scale <= 0) {
      return;
    }

    if (shouldRenderLess(be)) {
      return;
    }

    VertexConsumer vc = buffers.getBuffer(RenderType.translucent());
    float alpha = Math.min(scale, 1f);
    float s = 0.971f, off = (1 - s) / 2;
    final float r = 0.32F, g = 0.7F, b = 0.5F;

    pose.pushPose();
    pose.translate(off, off, off);
    pose.scale(s, s, s);
    PoseStack.Pose entry = pose.last();

    // Inner core
    for (BakedQuad q : innerBaker.getPart("core", WATER)) {
      vc.putBulkData(entry, q, r, g, b, alpha, light, overlay, true);
    }

    // Inner parts
    for (Direction d : Direction.values()) {
      if (be.transmitter.getConnectionType(d) != ConnectionType.NORMAL) {
        continue;
      }
      for (BakedQuad q : innerBaker.getPart(d.getSerializedName() + "_" + getPartName(be, d), WATER)) {
        vc.putBulkData(entry, q, r, g, b, alpha, light, overlay, true);
      }
    }

    pose.popPose();
  }
}
