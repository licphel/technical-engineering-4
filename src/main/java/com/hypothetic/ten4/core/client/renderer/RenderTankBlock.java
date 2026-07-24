package com.hypothetic.ten4.core.client.renderer;

import com.hypothetic.ten4.core.blockentity.TankBlockEntity;
import com.hypothetic.ten4.util.ClientUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

public class RenderTankBlock implements BlockEntityRenderer<TankBlockEntity> {

    public RenderTankBlock(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(TankBlockEntity be, float pt, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        FluidStack fluid = be.getFluidInventory().getFluidInTank(0);
        if (fluid.isEmpty()) return;

        float capacity = be.getFluidInventory().getTankCapacity(0);
        float scale = fluid.getAmount() / capacity;
        if (scale <= 0) return;

        IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid.getFluid());
        TextureAtlasSprite sprite = ClientUtil.getBlockSprite(props.getStillTexture(fluid));
        if (sprite == null) return;

        int color = props.getTintColor(fluid);
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;

        VertexConsumer vc = buffers.getBuffer(RenderType.translucent());
        Matrix4f mat = pose.last().pose();

        // Tank interior — matches Mekanism's bounds
        float minX = 0.135f, maxX = 0.865f;
        float minZ = 0.135f, maxZ = 0.865f;
        float minY = 0.12375f;
        float maxY = minY + 0.75225f * scale;

        // Top + bottom
        float tu0 = sprite.getU(minX * 16), tu1 = sprite.getU(maxX * 16);
        float tv0 = sprite.getV(minZ * 16), tv1 = sprite.getV(maxZ * 16);
        quad(vc, mat, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, minX, maxY, minZ, tu0, tv0, tu1, tv1, r, g, b, light, overlay);
        quad(vc, mat, maxX, minY, maxZ, minX, minY, maxZ, minX, minY, minZ, maxX, minY, minZ, tu0, tv0, tu1, tv1, r, g, b, light, overlay);

        // Sides
        float su0 = sprite.getU(minX * 16), su1 = sprite.getU(maxX * 16);
        float sv0 = sprite.getV(minY * 16), sv1 = sprite.getV(maxY * 16);
        quad(vc, mat, minX, maxY, maxZ, minX, minY, maxZ, maxX, minY, maxZ, maxX, maxY, maxZ, su0, sv1, su1, sv0, r, g, b, light, overlay);
        quad(vc, mat, maxX, maxY, minZ, maxX, minY, minZ, minX, minY, minZ, minX, maxY, minZ, su0, sv1, su1, sv0, r, g, b, light, overlay);

        float wu0 = sprite.getU(minZ * 16), wu1 = sprite.getU(maxZ * 16);
        quad(vc, mat, minX, maxY, minZ, minX, minY, minZ, minX, minY, maxZ, minX, maxY, maxZ, wu0, sv1, wu1, sv0, r, g, b, light, overlay);
        quad(vc, mat, maxX, maxY, maxZ, maxX, minY, maxZ, maxX, minY, minZ, maxX, maxY, minZ, wu0, sv1, wu1, sv0, r, g, b, light, overlay);
    }

    private static void quad(VertexConsumer vc, Matrix4f mat,
                              float x0, float y0, float z0, float x1, float y1, float z1,
                              float x2, float y2, float z2, float x3, float y3, float z3,
                              float u0, float v0, float u1, float v1,
                              float r, float g, float b, int light, int overlay) {
        vc.addVertex(mat, x0, y0, z0).setUv(u0, v0).setColor(r, g, b, 1f).setLight(light).setOverlay(overlay);
        vc.addVertex(mat, x1, y1, z1).setUv(u0, v1).setColor(r, g, b, 1f).setLight(light).setOverlay(overlay);
        vc.addVertex(mat, x2, y2, z2).setUv(u1, v1).setColor(r, g, b, 1f).setLight(light).setOverlay(overlay);
        vc.addVertex(mat, x3, y3, z3).setUv(u1, v0).setColor(r, g, b, 1f).setLight(light).setOverlay(overlay);
    }
}
