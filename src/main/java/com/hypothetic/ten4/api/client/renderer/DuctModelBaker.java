package com.hypothetic.ten4.api.client.renderer;

import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.model.SimpleModelState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Quaternionf;

import java.util.*;

public class DuctModelBaker {
  private static final RandomSource RANDOM = RandomSource.create();

  private static final ModelState[] ROTS = {
      rotateX(270), rotateX(90), identity(), rotateY(180), rotateY(90), rotateY(270)
  };

  private final ResourceLocation coreLoc;
  private final Map<String, ResourceLocation> typeLocs = new LinkedHashMap<>();
  private final Map<TextureAtlasSprite, Map<String, List<BakedQuad>[]>> cache = new HashMap<>();

  public DuctModelBaker(ResourceLocation core, ResourceLocation part,
                        ResourceLocation pull, ResourceLocation push) {
    this.coreLoc = core;
    typeLocs.put("part", part);
    typeLocs.put("pull", pull);
    typeLocs.put("push", push);
  }

  private static Direction dir(String s) {
    return Direction.valueOf(s.toUpperCase());
  }

  private static ModelState identity() {
    return new SimpleModelState(Transformation.identity());
  }

  private static ModelState rotateY(float deg) {
    return new SimpleModelState(new Transformation(null, new Quaternionf().rotateY((float) Math.toRadians(deg)), null, null));
  }

  private static ModelState rotateX(float deg) {
    return new SimpleModelState(new Transformation(null, new Quaternionf().rotateX((float) Math.toRadians(deg)), null, null));
  }

  public List<BakedQuad> getPart(String name, TextureAtlasSprite sprite) {
    Map<String, List<BakedQuad>[]> typed = cache.computeIfAbsent(sprite, k -> new LinkedHashMap<>());
    if ("core".equals(name)) {
      List<BakedQuad>[] c = typed.get("core");
      if (c == null) {
        c = bakeAllDirections(coreLoc, sprite);
        typed.put("core", c);
      }
      return c[Direction.NORTH.ordinal()];
    }
    int idx = name.lastIndexOf('_');
    Direction d = dir(name.substring(0, idx));
    String type = name.substring(idx + 1);
    ResourceLocation loc = typeLocs.get(type);
    if (loc == null) {
      return List.of();
    }
    List<BakedQuad>[] dirs = typed.get(type);
    if (dirs == null) {
      dirs = bakeAllDirections(loc, sprite);
      typed.put(type, dirs);
    }
    return dirs[d.ordinal()];
  }

  @SuppressWarnings("unchecked")
  private List<BakedQuad>[] bakeAllDirections(ResourceLocation loc, TextureAtlasSprite sprite) {
    List<BakedQuad>[] result = (List<BakedQuad>[]) new List[6];
    ModelBakery bakery = Minecraft.getInstance().getModelManager().getModelBakery();
    UnbakedModel unbaked = bakery.getModel(loc);
    for (Direction d : Direction.values()) {
      ModelBaker baker = bakery.new ModelBakerImpl((mloc, mat) -> mat.sprite(), ModelResourceLocation.standalone(loc));
      BakedModel baked = unbaked.bake(baker, mat -> sprite, ROTS[d.ordinal()]);
      if (baked != null) {
        result[d.ordinal()] = baked.getQuads(null, null, RANDOM, ModelData.EMPTY, null);
      } else {
        result[d.ordinal()] = new ArrayList<>();
      }
    }
    return result;
  }
}
