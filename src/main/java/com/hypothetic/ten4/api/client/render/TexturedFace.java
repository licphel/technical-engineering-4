package com.hypothetic.ten4.api.client.render;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.ArrayList;
import java.util.List;

public class TexturedFace {
  private final List<Layer> layers = new ArrayList<>();

  public TexturedFace() {
  }

  public TexturedFace(TextureAtlasSprite sprite) {
    layer(sprite, 0f);
  }

  public TexturedFace layer(TextureAtlasSprite sprite, float depth) {
    layers.add(new Layer(sprite, depth, 1, 1, 1, 1));
    return this;
  }

  public TexturedFace layerTinted(TextureAtlasSprite sprite, float depth, int color) {
    float r = ((color >> 16) & 0xFF) / 255f;
    float g = ((color >> 8) & 0xFF) / 255f;
    float b = (color & 0xFF) / 255f;
    layers.add(new Layer(sprite, depth, r, g, b, 1));
    return this;
  }

  public boolean isEmpty() {
    return layers.isEmpty();
  }

  public List<Layer> getLayers() {
    return layers;
  }

  public record Layer(TextureAtlasSprite sprite, float depth, float r, float g, float b, float a) {
  }
}
