package com.hypothetic.ten4.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hypothetic.ten4.api.client.gui.TextureRegion;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public final class ClientUtil {
  private static final LoadingCache<ResourceLocation, Optional<Dimension>> TEXTURE_SIZE_CACHE = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).maximumSize(128).build(new CacheLoader<>() {
    @Override
    public Optional<Dimension> load(ResourceLocation rl) {
      return readPngSize(rl);
    }
  });

  private ClientUtil() {
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

  public static @Nullable TextureRegion getFaceSprite(BlockState state, Direction face) {
    BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
    RandomSource rand = RandomSource.create();
    List<BakedQuad> quads = model.getQuads(state, face, rand, ModelData.EMPTY, null);
    if (!quads.isEmpty()) {
      return TextureRegion.ofSprite(quads.getFirst().getSprite());
    }
    quads = model.getQuads(state, null, rand, ModelData.EMPTY, null);
    if (!quads.isEmpty()) {
      return TextureRegion.ofSprite(quads.getFirst().getSprite());
    }
    return TextureRegion.ofSprite(model.getParticleIcon(ModelData.EMPTY));
  }

  public static int guiOriginX(int width, int xSize) {
    return (width - xSize) / 2;
  }

  public static int guiOriginY(int height, int ySize) {
    return (height - ySize) / 2;
  }

  private record Dimension(int width, int height) {
  }
}
