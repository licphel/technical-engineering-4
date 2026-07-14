package com.hypothetic.ten4.api.client.renderer;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.transmission.ItemDuctBlockEntity;
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
    if (e == null) {
      return;
    }
    ItemStack stack = e.stack;
    if (stack.isEmpty()) {
      return;
    }

    if (shouldRenderLess(be)) {
      return;
    }

    float spd = be.transmitter.getSpeed();
    int halfLen = ItemTransmitter.DUCT_LENGTH / 2;

    // Lerp between last synced progress and current
    int prev = be.transmitter.lastSyncedProgress;
    float displayProg;
    if (prev >= 0) {
      displayProg = prev + (e.progress - prev) * pt;
    } else {
      displayProg = e.progress + spd * pt;
    }
    displayProg = Math.clamp(displayProg, 0, ItemTransmitter.DUCT_LENGTH);

    Direction side = displayProg < halfLen
        ? Direction.values()[e.entrySide].getOpposite()
        : Direction.values()[e.exitSide];
    float t = displayProg / ItemTransmitter.DUCT_LENGTH - 0.5F;
    // Clamp to pipe interior so items don't fly out
    t = Math.clamp(t, -0.3F, 0.3F);
    double x = 0.5 + side.getStepX() * t;
    double y = 0.35 + side.getStepY() * t;
    double z = 0.5 + side.getStepZ() * t;

    pose.pushPose();
    pose.translate(x, y, z);
    pose.scale(0.75f, 0.75f, 0.75f);
    long tick = System.currentTimeMillis();
    pose.mulPose(Axis.YP.rotationDegrees((long) (tick / 20.0) % 360));
    Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, light, OverlayTexture.NO_OVERLAY, pose, buffers, be.getLevel(), 0);
    pose.popPose();
  }
}
