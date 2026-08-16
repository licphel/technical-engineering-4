package com.hypothetic.ten4.core.client.renderer;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.core.blockentity.LiquidUnitBlockEntity;
import com.hypothetic.ten4.util.ClientUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Mekanism-style: the shell is rendered by the engine (MODEL render shape, so it
 * gets ambient occlusion automatically). This BER only draws the internal fluid,
 * baked from the shared inner_core model with the fluid sprite — UVs are handled
 * by the model (FaceBakery divides the 0-16 model UVs by 16).
 */
public class RenderLiquidUnitBlock implements BlockEntityRenderer<LiquidUnitBlockEntity> {

  private static final ResourceLocation INNER_CORE = Ten4.id("block/connectable/inner_core");
  private static final ResourceLocation INNER_PART = Ten4.id("block/connectable/inner_part");

  private final DuctModelBaker innerBaker = new DuctModelBaker(INNER_CORE, INNER_PART, INNER_PART, INNER_PART);

  public RenderLiquidUnitBlock(BlockEntityRendererProvider.Context ctx) {
  }

  @Override
  public void render(LiquidUnitBlockEntity be, float pt, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
    FluidStack fluid = be.getClientFluid();
    float scale = be.getClientScale();
    if (fluid.isEmpty() || scale <= 0) return;

    IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid.getFluid());
    TextureAtlasSprite fs = ClientUtil.getBlockSprite(props.getStillTexture(fluid));
    if (fs == null) return;

    int color = props.getTintColor(fluid);
    float r = ((color >> 16) & 0xFF) / 255f;
    float g = ((color >> 8) & 0xFF) / 255f;
    float b = (color & 0xFF) / 255f;
    float alpha = Math.min(scale, 0.8f);

    VertexConsumer vc = buffers.getBuffer(RenderType.translucent());
    for (BakedQuad q : innerBaker.getPart("core", fs)) {
      vc.putBulkData(pose.last(), q, r, g, b, alpha, light, overlay, true);
    }
  }
}