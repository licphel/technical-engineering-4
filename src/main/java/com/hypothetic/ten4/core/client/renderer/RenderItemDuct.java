package com.hypothetic.ten4.core.client.renderer;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.duct.ItemDuctBlockEntity;
import com.hypothetic.ten4.api.transmission.item.ItemTransmitter;
import com.hypothetic.ten4.api.transmission.item.ItemTransmitter.TransitEntry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RenderItemDuct extends RenderTransmitterBlock<ItemDuctBlockEntity> {
  private static final ResourceLocation MODEL_CORE = Ten4.id("block/connectable/connectable_core");
  private static final ResourceLocation MODEL_PART = Ten4.id("block/connectable/connectable_part");
  private static final ResourceLocation MODEL_PULL = Ten4.id("block/connectable/connectable_pull");
  private static final ResourceLocation MODEL_PUSH = Ten4.id("block/connectable/connectable_push");

  public RenderItemDuct(BlockEntityRendererProvider.Context ctx, ResourceLocation textureName) {
    super(ctx, MODEL_CORE, MODEL_PART, MODEL_PULL, MODEL_PUSH,
        textureName,
        ResourceLocation.parse(textureName + "_active"));
  }

  @Override
  public void render(ItemDuctBlockEntity be, float pt, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
    super.render(be, pt, pose, buffers, light, overlay);
    TransitEntry e = be.transmitter.syncedEntry;
    if (e == null || be.getLevel() == null) {
      return;
    }
    ItemStack stack = e.stack;
    if (stack.isEmpty()) {
      return;
    }

    if (shouldRenderLess(be)) {
      return;
    }

    // Extrapolate from last sync using actual elapsed game ticks
    float speed = be.transmitter.getSpeed();
    float elapsed = be.getLevel().getGameTime() - be.transmitter.lastSyncTick + pt;
    float displayProg = be.transmitter.clientProgress + speed * elapsed;

    // Two-axis: entry axis (first half) + exit axis (second half).
    // Each clamped independently so item stays in-pipe even if extrapolation overshoots.
    float t = displayProg / ItemTransmitter.DUCT_LENGTH;
    Direction entryDir = Direction.values()[e.entrySide];
    Direction exitDir = Direction.values()[e.exitSide];

    float entryOff = Math.clamp(0.5F - t, 0, 0.5F);  // 0.5 at t=0, 0 at t>=0.5
    float exitOff = Math.clamp(t - 0.5F, 0, 0.5F);   // 0 at t<=0.5, 0.5 at t=1

    double x = 0.5 + entryDir.getStepX() * entryOff + exitDir.getStepX() * exitOff;
    double y = 0.35 + entryDir.getStepY() * entryOff + exitDir.getStepY() * exitOff;
    double z = 0.5 + entryDir.getStepZ() * entryOff + exitDir.getStepZ() * exitOff;

    pose.pushPose();
    pose.translate(x, y, z);
    pose.scale(0.75f, 0.75f, 0.75f);
    long tick = System.currentTimeMillis();
    pose.mulPose(Axis.YP.rotationDegrees((long) (tick / 20.0) % 360));
    Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, light, OverlayTexture.NO_OVERLAY, pose, buffers, be.getLevel(), 0);
    pose.popPose();
  }
}
