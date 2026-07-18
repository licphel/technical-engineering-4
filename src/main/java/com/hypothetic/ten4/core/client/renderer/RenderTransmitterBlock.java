package com.hypothetic.ten4.core.client.renderer;

import com.hypothetic.ten4.api.transmission.ConnectionType;
import com.hypothetic.ten4.api.transmission.ITransmitterProvider;
import com.hypothetic.ten4.core.block.BuiltinBlockStates;
import com.hypothetic.ten4.core.registry.config.ModConfigs;
import com.hypothetic.ten4.util.ClientUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.lighting.FlatQuadLighter;
import net.neoforged.neoforge.client.model.lighting.QuadLighter;
import net.neoforged.neoforge.client.model.lighting.SmoothQuadLighter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntSupplier;

public abstract class RenderTransmitterBlock<BE extends BlockEntity> implements BlockEntityRenderer<BE> {
  private static final BlockColors BLOCK_COLORS = Minecraft.getInstance().getBlockColors();
  private static final TextureAtlasSprite OVERLAY = ClientUtil.getBlockSprite(ResourceLocation.withDefaultNamespace("block/white_concrete"));
  public static IntSupplier LOD_DISTANCE = ModConfigs.CLIENT.render.ductLODDistance::get;

  protected final DuctModelBaker baker;
  private final SmoothQuadLighter aoLighter = new SmoothQuadLighter(BLOCK_COLORS);
  private final FlatQuadLighter flatLighter = new FlatQuadLighter(BLOCK_COLORS);
  private final TextureAtlasSprite[] loadedTextures;
  private TextureAtlasSprite sprite;
  private boolean drawInside = true;

  protected RenderTransmitterBlock(BlockEntityRendererProvider.Context ctx,
                                   ResourceLocation core,
                                   ResourceLocation part,
                                   ResourceLocation pull,
                                   ResourceLocation push,
                                   ResourceLocation... textures) {
    this.baker = new DuctModelBaker(core, part, pull, push);
    this.loadedTextures = Arrays.stream(textures).map(ClientUtil::getBlockSprite).toArray(TextureAtlasSprite[]::new);
  }

  public RenderTransmitterBlock<BE> setOpaque() {
    drawInside = false;
    return this;
  }

  protected ConnectionType getCT(BE be, Direction side) {
    ITransmitterProvider provider = (ITransmitterProvider) be;
    return provider.getTransmitter().getConnectionType(side);
  }

  protected String getPartName(BE be, Direction side) {
    return switch (getCT(be, side)) {
      case PULL -> "pull";
      case PUSH -> "push";
      default -> "part";
    };
  }

  protected TextureAtlasSprite getSprite(BE be) {
    BlockState state = be.getBlockState();
    if (state.getValue(BuiltinBlockStates.ACTIVE)) {
      return loadedTextures[1];
    }
    return loadedTextures[0];
  }

  protected boolean shouldRenderLess(BE be) {
    Player player = Minecraft.getInstance().player;
    return player == null || player.blockPosition().distManhattan(be.getBlockPos()) > LOD_DISTANCE.getAsInt();
  }

  @Override
  public void render(BE be, float pt, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
    if (be.getLevel() == null) {
      return;
    }

    TextureAtlasSprite cs = getSprite(be);

    // Setup per-vertex AO lighting once per frame for this block entity
    QuadLighter lighter = prepareLighter(be);
    BlockPos pos = be.getBlockPos();
    lighter.setup(be.getLevel(), pos, be.getBlockState());

    renderBody(lighter, be, pose, buffers, cs, 1.0F, 1.0F, 1.0F, 1.0F, overlay);
    if (drawInside) {
      renderContents(be, pt, pose, buffers, cs, light, overlay);
    }

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
      renderBody(lighter, be, pose, buffers, OVERLAY, cr, cg, cb, 0.45F, overlay);
      pose.popPose();
    }
  }

  private QuadLighter prepareLighter(BE be) {
    return Minecraft.useAmbientOcclusion() ? aoLighter : flatLighter;
  }

  protected int bodyColor(BE be) {
    DyeColor c = getDyeColor(be);
    return c == null ? 0xFFFFFF : c.getFireworkColor();
  }

  @Nullable
  protected DyeColor getDyeColor(BE be) {
    if (be instanceof ITransmitterProvider provider) {
      provider.getTransmitter();
      return provider.getTransmitter().getColor();
    }
    return null;
  }

  protected void renderBody(QuadLighter lighter, BE be, PoseStack pose, MultiBufferSource buffers, TextureAtlasSprite cs,
                            float r, float g, float b, float a, int overlay) {
    VertexConsumer vc = buffers.getBuffer(a == 1 ? RenderType.cutout() : RenderType.translucent());
    PoseStack.Pose entry = pose.last();
    List<String> names = new ArrayList<>(6);

    // Core: skip faces where connections exist (avoid overlap with parts)
    for (BakedQuad q : baker.getPart("core", cs)) {
      if (getCT(be, q.getDirection()) == ConnectionType.NONE) {
        lighter.computeLightingForQuad(q);
        vc.putBulkData(entry, q, lighter.getComputedBrightness(), r, g, b, a,
            lighter.getComputedLightmap(), overlay, false);
      }
    }

    // Parts per direction
    for (Direction d : Direction.values()) {
      if (getCT(be, d) == ConnectionType.NONE) {
        continue;
      }
      names.clear();
      names.add(d.getSerializedName() + "_" + getPartName(be, d));
      drawParts(lighter, entry, vc, cs, names, r, g, b, a, overlay);
    }
  }

  protected void renderContents(BE be, float pt, PoseStack pose, MultiBufferSource buffers,
                                TextureAtlasSprite cs, int light, int overlay) {
  }

  private void drawParts(QuadLighter lighter, PoseStack.Pose entry, VertexConsumer vc, TextureAtlasSprite sprite,
                         List<String> partNames, float r, float g, float b, float a, int overlay) {
    for (String name : partNames) {
      for (BakedQuad q : baker.getPart(name, sprite)) {
        lighter.computeLightingForQuad(q);
        vc.putBulkData(entry, q, lighter.getComputedBrightness(), r, g, b, a,
            lighter.getComputedLightmap(), overlay, false);
      }
    }
  }
}
