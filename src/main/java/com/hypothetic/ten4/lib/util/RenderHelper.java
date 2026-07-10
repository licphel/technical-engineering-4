package com.hypothetic.ten4.lib.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public final class RenderHelper {
  private static final LoadingCache<ResourceLocation, Optional<Dimension>> TEXTURE_SIZE_CACHE = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).maximumSize(128).build(new CacheLoader<>() {
    @Override
    public Optional<Dimension> load(ResourceLocation rl) {
      return readPngSize(rl);
    }
  });

  private RenderHelper() {
  }

  private static Optional<Dimension> readPngSize(ResourceLocation rl) {
    try {
      Optional<Resource> res = Minecraft.getInstance().getResourceManager().getResource(rl);
      if (res.isEmpty()) {
        return Optional.empty();
      }
      try (InputStream in = res.get().open(); NativeImage image = NativeImage.read(in)) {
        return Optional.of(new Dimension(image.getWidth(), image.getHeight()));
      }
    } catch (IOException e) {
      return Optional.empty();
    }
  }

  public static int @Nullable [] getTextureSize(ResourceLocation rl) {
    Optional<Dimension> dim = TEXTURE_SIZE_CACHE.getUnchecked(rl);
    return dim.map(d -> new int[] {d.width, d.height}).orElse(null);
  }

  public static @Nullable TextureAtlasSprite getFluidTexture(Fluid fluid, boolean flowing) {
    if (fluid == Fluids.EMPTY) {
      return null;
    }
    IClientFluidTypeExtensions fluidType = IClientFluidTypeExtensions.of(fluid);
    ResourceLocation textureRl = flowing ? fluidType.getFlowingTexture() : fluidType.getStillTexture();
    return getBlockSprite(textureRl);
  }

  public static TextureAtlasSprite getBlockSprite(ResourceLocation rl) {
    return Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(rl);
  }

  private record Dimension(int width, int height) {
  }

  public static int guiOriginX(int width, int xSize) {
    return (width - xSize) / 2;
  }

  public static int guiOriginY(int height, int ySize) {
    return (height - ySize) / 2;
  }
}
