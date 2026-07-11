package com.hypothetic.ten4.lib.client.render;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.lib.blockentity.internet.EnergyCableBlockEntity;
import com.hypothetic.ten4.lib.capability.internet.ConnectionType;
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

/**
 * Energy cable renderer. Picks model per ConnectionType.
 * Contents rendered with same model geometry, water texture, green tint.
 * Pattern: Mekanism's RenderUniversalCable.
 */
public class EnergyCableRenderer extends RenderTransmitterBase<EnergyCableBlockEntity> {
  private static final ResourceLocation TEX1 = Ten4.id("block/glass_energy_cable");
  private static final ResourceLocation MODEL_PART = Ten4.id("block/connectable/connectable_part");
  private static final ResourceLocation MODEL_PULL = Ten4.id("block/connectable/connectable_pull");
  private static final ResourceLocation MODEL_PUSH = Ten4.id("block/connectable/connectable_push");

  private static final TextureAtlasSprite WATER = SpriteHelper.get(
      ResourceLocation.withDefaultNamespace("block/water_still"));

  public EnergyCableRenderer(BlockEntityRendererProvider.Context ctx) {
    super(ctx, Ten4.id("block/connectable/connectable_core"), MODEL_PART, MODEL_PULL, MODEL_PUSH);
  }

  @Override
  protected ResourceLocation coreTexture() {
    return TEX1;
  }

  @Override
  protected ResourceLocation sideTexture() {
    return TEX1; // terminal texture for parts/pull/push
  }

  @Override
  protected boolean hasConnection(EnergyCableBlockEntity be, Direction d) {
    return be.transmitter.getConnectionType(d) != ConnectionType.NONE;
  }

  @Override
  protected String partName(EnergyCableBlockEntity be, Direction d) {
    return switch (be.transmitter.getConnectionType(d)) {
      case PULL -> "pull";
      case PUSH -> "push";
      default -> "part";
    };
  }

  // --- Energy contents (Mekanism pattern: inner model + water texture + green tint) ---
  private static final ResourceLocation INNER_CORE  = Ten4.id("block/connectable/inner_core");
  private static final ResourceLocation INNER_PART  = Ten4.id("block/connectable/inner_part");

  private final CableModelBaker innerBaker = new CableModelBaker(INNER_CORE, INNER_PART, INNER_PART, INNER_PART);

  @Override
  protected void renderContents(EnergyCableBlockEntity be, float pt, PoseStack pose,
                                MultiBufferSource buffers, TextureAtlasSprite cs, TextureAtlasSprite ss,
                                int light, int overlay) {
    float fill = be.transmitter.getClientFillRatio();
    if (fill <= 0) return;
    Player player = Minecraft.getInstance().player;
    if (player == null || !player.blockPosition().closerThan(be.getBlockPos(), 32)) return;

    VertexConsumer vc = buffers.getBuffer(RenderType.translucent());
    float alpha = Math.min(fill, 1f);
    float s = 0.99f, off = (1 - s) / 2;

    pose.pushPose();
    pose.translate(off, off, off);
    pose.scale(s, s, s);
    var entry = pose.last();

    // Inner core
    for (BakedQuad q : innerBaker.getPart("core", WATER))
      vc.putBulkData(entry, q, 0.9f, 0.2f, 0.3f, alpha, light, overlay, true);

    // Inner parts
    for (Direction d : Direction.values()) {
      if (be.transmitter.getConnectionType(d) != ConnectionType.NORMAL) continue;
      for (BakedQuad q : innerBaker.getPart(d.getSerializedName() + "_" + partName(be, d), WATER))
        vc.putBulkData(entry, q, 0.9f, 0.2f, 0.3f, alpha, light, overlay, true);
    }

    pose.popPose();
  }
}
