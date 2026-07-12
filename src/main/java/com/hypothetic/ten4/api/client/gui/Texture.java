package com.hypothetic.ten4.api.client.gui;

import com.hypothetic.ten4.util.RenderHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public record Texture(ResourceLocation resource, int width, int height) {
  static final ResourceLocation BLOCK_ATLAS = ResourceLocation.withDefaultNamespace("textures/atlas/blocks.png");

  public static @Nullable Texture of(ResourceLocation rl) {
    int[] size = RenderHelper.getTextureSize(rl);
    return size != null ? new Texture(rl, size[0], size[1]) : null;
  }

  static Texture blockAtlas() {
    int[] size = RenderHelper.getTextureSize(BLOCK_ATLAS);
    int w = size != null ? size[0] : 256;
    int h = size != null ? size[1] : 256;
    return new Texture(BLOCK_ATLAS, w, h);
  }
}
