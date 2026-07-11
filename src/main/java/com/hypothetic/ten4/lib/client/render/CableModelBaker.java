package com.hypothetic.ten4.lib.client.render;

import com.hypothetic.ten4.Ten4;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.model.SimpleModelState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.StandaloneGeometryBakingContext;
import org.joml.Quaternionf;

import java.util.*;

public class CableModelBaker {
  // Baking context: no AO, no block light — same as Mekanism
  private static final IGeometryBakingContext BAKE_CTX = StandaloneGeometryBakingContext.builder()
      .withGui3d(false).withUseBlockLight(false).withUseAmbientOcclusion(false)
      .build(Ten4.id("transmitter"));

  private static final RandomSource RANDOM = RandomSource.create();

  private static final ModelState[] ROTS = {
      rotateX(270), rotateX(90), identity(), rotateY(180), rotateY(90), rotateY(270)
  };

  private final ResourceLocation coreLoc;
  private final Map<String, ResourceLocation> typeLocs = new LinkedHashMap<>();
  private final Map<TextureAtlasSprite, Map<String, List<BakedQuad>[]>> cache = new HashMap<>();

  public CableModelBaker(ResourceLocation core, ResourceLocation part,
                          ResourceLocation pull, ResourceLocation push) {
    this.coreLoc = core;
    typeLocs.put("part", part);
    typeLocs.put("pull", pull);
    typeLocs.put("push", push);
  }

  public List<BakedQuad> getPart(String name, TextureAtlasSprite sprite) {
    Map<String, List<BakedQuad>[]> typed = cache.get(sprite);
    if (typed == null) { typed = new LinkedHashMap<>(); cache.put(sprite, typed); }
    if ("core".equals(name)) {
      List<BakedQuad>[] c = typed.get("core");
      if (c == null) { c = bakeAllDirections(coreLoc, sprite); typed.put("core", c); }
      return c[Direction.NORTH.ordinal()];
    }
    int idx = name.lastIndexOf('_');
    Direction d = dir(name.substring(0, idx));
    String type = name.substring(idx + 1);
    ResourceLocation loc = typeLocs.get(type);
    if (loc == null) return List.of();
    List<BakedQuad>[] dirs = typed.get(type);
    if (dirs == null) { dirs = bakeAllDirections(loc, sprite); typed.put(type, dirs); }
    return dirs[d.ordinal()];
  }


  private List<BakedQuad>[] bakeAllDirections(ResourceLocation loc, TextureAtlasSprite sprite) {
    List<BakedQuad>[] result = new List[6];
    ModelBakery bakery = Minecraft.getInstance().getModelManager().getModelBakery();
    UnbakedModel unbaked = bakery.getModel(loc);
    if (unbaked == null) { for (int i = 0; i < 6; i++) result[i] = List.of(); return result; }
    for (Direction d : Direction.values()) {
      ModelBaker baker = bakery.new ModelBakerImpl((mloc, mat) -> mat.sprite(), ModelResourceLocation.standalone(loc));
      BakedModel baked = unbaked.bake(baker, mat -> sprite, ROTS[d.ordinal()]);
      result[d.ordinal()] = baked.getQuads(null, null, RANDOM, ModelData.EMPTY, null);
    }
    return result;
  }

  private static Direction dir(String s) { return Direction.valueOf(s.toUpperCase()); }
  private static ModelState identity() { return new SimpleModelState(Transformation.identity()); }
  private static ModelState rotateY(float deg) { return new SimpleModelState(new Transformation(null, new Quaternionf().rotateY((float)Math.toRadians(deg)), null, null)); }
  private static ModelState rotateX(float deg) { return new SimpleModelState(new Transformation(null, new Quaternionf().rotateX((float)Math.toRadians(deg)), null, null)); }
}
