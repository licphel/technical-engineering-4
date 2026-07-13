package com.hypothetic.ten4.api.client.renderer;

import com.hypothetic.ten4.api.transmission.ITransmitterProvider;
import com.hypothetic.ten4.util.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class RenderTransmitterBlock<BE extends BlockEntity> implements BlockEntityRenderer<BE> {
  private static final TextureAtlasSprite OVERLAY = RenderHelper.getBlockSprite(
      ResourceLocation.withDefaultNamespace("block/white_concrete"));

  protected final DuctModelBaker baker;
  private TextureAtlasSprite spriteCore, spriteSide;

  protected RenderTransmitterBlock(BlockEntityRendererProvider.Context ctx,
                                   ResourceLocation core, ResourceLocation part,
                                   ResourceLocation pull, ResourceLocation push) {
    this.baker = new DuctModelBaker(core, part, pull, push);
  }

  private static int applyDirectionalLight(int packed, Direction face) {
    int block = (packed >> 4) & 0xF;
    int sky = (packed >> 20) & 0xF;
    float factor = switch (face) {
      case UP -> 1.0f;
      case DOWN -> 0.6f;
      default -> 0.8f;
    };
    int adjSky = (int) (sky * factor);
    int adjBlock = (int) (block * factor);
    return (adjBlock << 4) | (adjSky << 20);
  }

  protected abstract ResourceLocation coreTexture();

  protected abstract ResourceLocation sideTexture();

  protected abstract boolean hasConnection(BE be, Direction d);

  protected abstract String partName(BE be, Direction d);

  protected TextureAtlasSprite core() {
    if (spriteCore == null) {
      spriteCore = RenderHelper.getBlockSprite(coreTexture());
    }
    return spriteCore;
  }

  protected TextureAtlasSprite side() {
    if (spriteSide == null) {
      spriteSide = RenderHelper.getBlockSprite(sideTexture());
    }
    return spriteSide;
  }

  @Override
  public void render(BE be, float pt, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
    TextureAtlasSprite cs = core();
    TextureAtlasSprite ss = side();
    renderBody(be, pose, buffers, cs, ss, 1.0F, 1.0F, 1.0F, 1.0F, light, overlay);
    renderContents(be, pt, pose, buffers, cs, ss, light, overlay);

    // Colored overlay when dyed
    DyeColor dye = getDyeColor(be);
    if (dye != null) {
      int color = dye.getFireworkColor();
      float cr = ((color >> 16) & 0xFF) / 255F;
      float cg = ((color >> 8) & 0xFF) / 255F;
      float cb = (color & 0xFF) / 255F;
      float s = 1.001F, off = (1 - s) / 2;
      pose.pushPose();
      pose.translate(off, off, off);
      pose.scale(s, s, s);
      renderBody(be, pose, buffers, OVERLAY, OVERLAY, cr, cg, cb, 0.45F, light, overlay);
      pose.popPose();
    }
  }

  protected int bodyColor(BE be) {
    DyeColor c = getDyeColor(be);
    return c == null ? 0xFFFFFF : c.getFireworkColor();
  }

  @Nullable
  protected net.minecraft.world.item.DyeColor getDyeColor(BE be) {
    if (be instanceof ITransmitterProvider provider && provider.getTransmitter() != null) {
      return provider.getTransmitter().getColor();
    }
    return null;
  }

  protected void renderBody(BE be, PoseStack pose, MultiBufferSource buffers,
                            TextureAtlasSprite cs, TextureAtlasSprite ss,
                            float r, float g, float b, float a, int light, int overlay) {
    VertexConsumer vc = buffers.getBuffer(a == 1 ? RenderType.cutout() : RenderType.translucent());
    var entry = pose.last();
    List<String> names = new ArrayList<>(6);

    // Core: skip faces where connections exist (avoid overlap with parts)
    for (BakedQuad q : baker.getPart("core", cs)) {
      if (!hasConnection(be, q.getDirection())) {
        int faceLight = applyDirectionalLight(light, q.getDirection());
        vc.putBulkData(entry, q, r, g, b, a, faceLight, overlay, false);
      }
    }

    // Parts per direction
    for (Direction d : Direction.values()) {
      if (!hasConnection(be, d)) {
        continue;
      }
      names.clear();
      names.add(d.getSerializedName() + "_" + partName(be, d));
      drawParts(entry, vc, ss, names, r, g, b, a, light, overlay);
    }
  }

  protected void renderContents(BE be, float pt, PoseStack pose, MultiBufferSource buffers,
                                TextureAtlasSprite cs, TextureAtlasSprite ss, int light, int overlay) {
  }

  protected void drawParts(PoseStack.Pose entry, VertexConsumer vc, TextureAtlasSprite sprite,
                           List<String> partNames, float r, float g, float b, float a,
                           int light, int overlay) {
    for (String name : partNames) {
      for (BakedQuad q : baker.getPart(name, sprite)) {
        int faceLight = applyDirectionalLight(light, q.getDirection());
        vc.putBulkData(entry, q, r, g, b, a, faceLight, overlay, false);
      }
    }
  }
}
