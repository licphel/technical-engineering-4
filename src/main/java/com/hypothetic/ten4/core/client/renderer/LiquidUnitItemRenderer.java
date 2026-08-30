package com.hypothetic.ten4.core.client.renderer;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.client.render.TexturedFace;
import com.hypothetic.ten4.api.client.render.TexturedFaceCubeRenderer;
import com.hypothetic.ten4.core.item.LiquidUnitItem;
import com.hypothetic.ten4.util.ClientUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;

public class LiquidUnitItemRenderer extends BlockEntityWithoutLevelRenderer {
  public static final LiquidUnitItemRenderer INSTANCE = new LiquidUnitItemRenderer(
      Ten4.id("block/device/liquid_unit_underlay"),
      Ten4.id("block/device/liquid_unit_overlay"));
  private final ResourceLocation underlay;
  private final ResourceLocation overlay;

  public LiquidUnitItemRenderer(ResourceLocation underlay, ResourceLocation overlay) {
    super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    this.underlay = underlay;
    this.overlay = overlay;
  }

  @Override
  public void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
    if (!(stack.getItem() instanceof BlockItem bi)) {
      return;
    }
    BlockState state = bi.getBlock().defaultBlockState();

    TextureAtlasSprite underlaySprite = ClientUtil.getBlockSprite(this.underlay);
    TextureAtlasSprite overlaySprite = ClientUtil.getBlockSprite(this.overlay);

    FluidStack fluid = LiquidUnitItem.getStoredFluid(stack);
    TexturedFace face = new TexturedFace().layer(underlaySprite, -0.0002F);
    if (!fluid.isEmpty()) {
      IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid.getFluid());
      TextureAtlasSprite fluidSprite = ClientUtil.getBlockSprite(props.getStillTexture(fluid));
      face.layerTinted(fluidSprite, -0.0001F, props.getTintColor(fluid));
    }
    face.layer(overlaySprite, 0);

    new TexturedFaceCubeRenderer()
        .set(Direction.NORTH, face)
        .set(Direction.SOUTH, face)
        .set(Direction.WEST, face)
        .set(Direction.EAST, face)
        .set(Direction.UP, face)
        .set(Direction.DOWN, face)
        .render(buffers, pose, state, light, overlay);
  }
}
