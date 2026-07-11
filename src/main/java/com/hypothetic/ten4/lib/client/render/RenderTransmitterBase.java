package com.hypothetic.ten4.lib.client.render;

import com.hypothetic.ten4.Ten4;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.StandaloneGeometryBakingContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base for cable/pipe renderers. Subclasses provide model paths,
 * textures, and connection-type→model-name mapping.
 * <p>
 * Pattern: Mekanism's RenderTransmitterBase.
 */
public abstract class RenderTransmitterBase<BE extends BlockEntity> implements BlockEntityRenderer<BE> {

  protected final CableModelBaker baker;
  private TextureAtlasSprite spriteCore, spriteSide;

  protected RenderTransmitterBase(BlockEntityRendererProvider.Context ctx,
                                  ResourceLocation core, ResourceLocation part,
                                  ResourceLocation pull, ResourceLocation push) {
    this.baker = new CableModelBaker(core, part, pull, push);
  }

  protected abstract ResourceLocation coreTexture();

  protected abstract ResourceLocation sideTexture();

  protected abstract boolean hasConnection(BE be, Direction d);

  /**
   * Return "part", "pull", or "push" based on connection type.
   */
  protected abstract String partName(BE be, Direction d);

  protected TextureAtlasSprite core() {
    if (spriteCore == null) {
      spriteCore = SpriteHelper.get(coreTexture());
    }
    return spriteCore;
  }

  protected TextureAtlasSprite side() {
    if (spriteSide == null) {
      spriteSide = SpriteHelper.get(sideTexture());
    }
    return spriteSide;
  }

  @Override
  public void render(BE be, float pt, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
    TextureAtlasSprite cs = core(), ss = side();
    renderBody(be, pose, buffers, cs, ss, light, overlay);
    renderContents(be, pt, pose, buffers, cs, ss, light, overlay);
  }

  protected void renderBody(BE be, PoseStack pose, MultiBufferSource buffers,
                            TextureAtlasSprite cs, TextureAtlasSprite ss, int light, int overlay) {
    VertexConsumer vc = buffers.getBuffer(RenderType.cutout());
    var entry = pose.last();
    List<String> names = new ArrayList<>(6);

    // Core: skip faces where connections exist (avoid overlap with parts)
    for (BakedQuad q : baker.getPart("core", cs)) {
      if (!hasConnection(be, q.getDirection())) {
        int faceLight = applyDirectionalLight(light, q.getDirection());
        vc.putBulkData(entry, q, 1, 1, 1, 1, faceLight, overlay, false);
      }
    }

    // Parts per direction
    for (Direction d : Direction.values()) {
      if (!hasConnection(be, d)) {
        continue;
      }
      names.clear();
      names.add(d.getSerializedName() + "_" + partName(be, d));
      drawParts(entry, vc, ss, names, 1, 1, 1, 1, light, overlay);
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

  /** Scale sky light by face normal: top=1.0, sides=0.6, bottom=0.4. */
  private static int applyDirectionalLight(int packed, Direction face) {
    int block = (packed >> 4) & 0xF;
    int sky   = (packed >> 20) & 0xF;
    float factor = switch (face) {
      case UP -> 1.0f; case DOWN -> 0.6f; default -> 0.8f;
    };
    int adjSky = (int)(sky * factor);
    return (block << 4) | (adjSky << 20);
  }
}
