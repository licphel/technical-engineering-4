package com.hypothetic.ten4.api.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomBakedModel implements BakedModel {
  BakedModel bakedModel;

  public CustomBakedModel(BakedModel bakedModel) {
    this.bakedModel = bakedModel;
  }

  @Override
  public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
    return bakedModel.getQuads(blockState, direction, randomSource);
  }

  @Override
  public boolean useAmbientOcclusion() {
    return bakedModel.useAmbientOcclusion();
  }

  @Override
  public boolean isGui3d() {
    return bakedModel.isGui3d();
  }

  @Override
  public boolean usesBlockLight() {
    return bakedModel.usesBlockLight();
  }

  @Override
  public boolean isCustomRenderer() {
    return true;
  }

  @Override
  public TextureAtlasSprite getParticleIcon() {
    return bakedModel.getParticleIcon();
  }

  @Override
  public ItemOverrides getOverrides() {
    return bakedModel.getOverrides();
  }

  @Override
  public BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
    bakedModel.getTransforms().getTransform(transformType).apply(applyLeftHandTransform, poseStack);
    return this;
  }
}
