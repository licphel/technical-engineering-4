package com.hypothetic.ten4.api.client.renderer;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.internet.ItemDuctBlockEntity;
import com.hypothetic.ten4.api.transmission.ConnectionType;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RenderItemDuct extends RenderTransmitterBlock<ItemDuctBlockEntity> {
  private static final ResourceLocation MODEL_CORE = Ten4.id("block/connectable/connectable_core");
  private static final ResourceLocation MODEL_PART = Ten4.id("block/connectable/connectable_part");
  private static final ResourceLocation MODEL_PULL = Ten4.id("block/connectable/connectable_pull");
  private static final ResourceLocation MODEL_PUSH = Ten4.id("block/connectable/connectable_push");
  private final ResourceLocation tex;

  public RenderItemDuct(BlockEntityRendererProvider.Context ctx, ResourceLocation t) {
    super(ctx, MODEL_CORE, MODEL_PART, MODEL_PULL, MODEL_PUSH);
    tex = t;
  }

  @Override
  protected ResourceLocation coreTexture() {
    return tex;
  }

  @Override
  protected ResourceLocation sideTexture() {
    return tex;
  }

  @Override
  protected boolean hasConnection(ItemDuctBlockEntity be, Direction d) {
    return be.transmitter.getConnectionType(d) != ConnectionType.NONE;
  }

  @Override
  protected String partName(ItemDuctBlockEntity be, Direction d) {
    return switch (be.transmitter.getConnectionType(d)) {
      case PULL -> "pull";
      case PUSH -> "push";
      default -> "part";
    };
  }

  @Override
  public void render(ItemDuctBlockEntity be, float pt, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
    super.render(be, pt, pose, buffers, light, overlay);
    var entries = be.transmitter.getClientTransit();
    if (entries.isEmpty()) {
      return;
    }
    Player player = Minecraft.getInstance().player;
    if (player == null || !player.blockPosition().closerThan(be.getBlockPos(), 32)) {
      return;
    }

    float spd = be.transmitter.getSpeed();
    int halfLen = ItemTransmitter.DUCT_LENGTH / 2;
    for (TransitEntry e : entries) {
      ItemStack stack = e.stack;
      if (stack.isEmpty()) continue;

      // Mekanism formula: single direction based on which half of pipe
      float displayProg = Math.min(e.progress + spd * pt, ItemTransmitter.DUCT_LENGTH);
      Direction side = displayProg < halfLen
          ? Direction.values()[e.entryDir].getOpposite()  // 0~50%: coming FROM entry side
          : Direction.values()[e.direction];               // 50~100%: going TO exit side
      float t = displayProg / ItemTransmitter.DUCT_LENGTH - 0.5F; // -0.5 ~ +0.5
      double x = 0.5 + side.getStepX() * t;
      double y = 0.25 + side.getStepY() * t;
      double z = 0.5 + side.getStepZ() * t;

      pose.pushPose();
      pose.translate(x, y, z);
      pose.scale(0.75f, 0.75f, 0.75f);
      long tick = System.currentTimeMillis();
      pose.mulPose(Axis.YP.rotationDegrees((float) (tick / 20.0) % 360));
      Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, light, OverlayTexture.NO_OVERLAY, pose, buffers, be.getLevel(), 0);
      pose.popPose();
    }
  }
}
