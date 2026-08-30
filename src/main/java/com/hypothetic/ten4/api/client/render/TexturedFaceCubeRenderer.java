package com.hypothetic.ten4.api.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.lighting.FlatQuadLighter;
import net.neoforged.neoforge.client.model.lighting.QuadLighter;
import net.neoforged.neoforge.client.model.lighting.SmoothQuadLighter;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class TexturedFaceCubeRenderer {
  private static final BlockColors BLOCK_COLORS = Minecraft.getInstance().getBlockColors();
  private static final SmoothQuadLighter aoLighter = new SmoothQuadLighter(BLOCK_COLORS);
  private static final FlatQuadLighter flatLighter = new FlatQuadLighter(BLOCK_COLORS);

  private final Map<Direction, TexturedFace> faces = new EnumMap<>(Direction.class);

  public TexturedFaceCubeRenderer set(Direction face, TexturedFace texturedFace) {
    faces.put(face, texturedFace);
    return this;
  }

  private QuadLighter prepareLighter() {
    return Minecraft.useAmbientOcclusion() ? aoLighter : flatLighter;
  }

  public void render(MultiBufferSource buffers, PoseStack pose, BlockState state, int light, int overlay) {
    render(buffers, pose, state, null, BlockPos.ZERO, light, overlay);
  }

  public void render(MultiBufferSource buffers, PoseStack pose, BlockState state, @Nullable BlockAndTintGetter level, BlockPos pos, int light, int overlay) {
    BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
    VertexConsumer vc = buffers.getBuffer(RenderType.translucent());
    RandomSource rand = RandomSource.create(42);

    QuadLighter lighter = null;
    if (level != null) {
      lighter = prepareLighter();
      lighter.setup(level, pos, state);
    }

    for (Direction face : Direction.values()) {
      TexturedFace tf = faces.get(face);
      if (tf == null || tf.isEmpty()) {
        continue;
      }
      List<BakedQuad> baseQuads = model.getQuads(state, face, rand, ModelData.EMPTY, null);
      if (baseQuads.isEmpty()) {
        continue;
      }
      BakedQuad base = baseQuads.getFirst();
      for (TexturedFace.Layer layer : tf.getLayers()) {
        pose.pushPose();
        pose.translate(face.getStepX() * layer.depth(), face.getStepY() * layer.depth(), face.getStepZ() * layer.depth());

        RetexturedBakedQuad quad = new RetexturedBakedQuad(base, layer.sprite());
        if (lighter != null) {
          lighter.computeLightingForQuad(quad);
          vc.putBulkData(pose.last(), quad, lighter.getComputedBrightness(),
              layer.r(), layer.g(), layer.b(), layer.a(), lighter.getComputedLightmap(), overlay, false);
        } else {
          float brightness = switch (face) {
            case UP, WEST -> 1.0F;
            case DOWN, EAST -> 0.62F;
            case NORTH, SOUTH -> 0.35F;
          };
          vc.putBulkData(
              pose.last(),
              quad,
              new float[] {brightness, brightness, brightness, brightness},
              layer.r(),
              layer.g(),
              layer.b(),
              layer.a(),
              new int[] {light, light, light, light},
              overlay,
              false);
        }
        pose.popPose();
      }
    }
  }
}
