package com.hypothetic.ten4.core.client.renderer;

import com.hypothetic.ten4.api.client.render.TexturedFace;
import com.hypothetic.ten4.api.client.render.TexturedFaceCubeRenderer;
import com.hypothetic.ten4.core.blockentity.LiquidUnitBlockEntity;
import com.hypothetic.ten4.util.ClientUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class RenderLiquidUnitBlock implements BlockEntityRenderer<LiquidUnitBlockEntity> {
  private final ResourceLocation underlay;
  private final ResourceLocation overlay;

  public RenderLiquidUnitBlock(BlockEntityRendererProvider.Context ctx, ResourceLocation underlay, ResourceLocation overlay) {
    this.underlay = underlay;
    this.overlay = overlay;
  }

  @Override
  public void render(LiquidUnitBlockEntity be, float pt, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
    TextureAtlasSprite underlaySprite = ClientUtil.getBlockSprite(this.underlay);
    TextureAtlasSprite overlaySprite = ClientUtil.getBlockSprite(this.overlay);

    FluidStack fluid = be.getClientFluid();
    TexturedFace face = new TexturedFace().layer(underlaySprite, -0.0003F);
    if (!fluid.isEmpty()) {
      IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid.getFluid());
      TextureAtlasSprite fluidSprite = ClientUtil.getBlockSprite(props.getStillTexture(fluid));
      face.layerTinted(fluidSprite, -0.0002F, props.getTintColor(fluid));
    }
    face.layer(overlaySprite, -0.0001F);

    new TexturedFaceCubeRenderer()
        .set(Direction.NORTH, face)
        .set(Direction.SOUTH, face)
        .set(Direction.WEST, face)
        .set(Direction.EAST, face)
        .set(Direction.UP, face)
        .set(Direction.DOWN, face)
        .render(buffers, pose, be.getBlockState(), be.getLevel(), be.getBlockPos(), light, overlay);
  }
}
