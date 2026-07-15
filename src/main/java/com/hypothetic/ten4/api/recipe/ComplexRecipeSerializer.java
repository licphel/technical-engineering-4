package com.hypothetic.ten4.api.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hypothetic.ten4.Ten4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ComplexRecipeSerializer implements RecipeSerializer<IComplexRecipe> {
  public static final int DEFAULT_TIME = 150;
  private static final Codec<List<Complex>> INGREDIENT_LIST_CODEC =
      Complex.CODEC.listOf();
  private final Supplier<RecipeType<IComplexRecipe>> recipeType;
  private final MapCodec<IComplexRecipe> codec;
  private final StreamCodec<RegistryFriendlyByteBuf, IComplexRecipe> streamCodec;

  public ComplexRecipeSerializer(Supplier<RecipeType<IComplexRecipe>> recipeType) {
    this.recipeType = recipeType;
    this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
        INGREDIENT_LIST_CODEC.fieldOf("inputs").forGetter(IComplexRecipe::inputs),
        INGREDIENT_LIST_CODEC.fieldOf("outputs").forGetter(IComplexRecipe::outputs),
        Codec.INT.optionalFieldOf("time", DEFAULT_TIME).forGetter(IComplexRecipe::time)
    ).apply(instance, (inputs, outputs, time) -> createRecipe(inputs, outputs, time, null)));
    this.streamCodec = StreamCodec.of(this::toNetwork, this::fromNetwork);
  }

  private static List<Complex> readIngredientList(JsonObject json, String key) {
    List<Complex> list = new ArrayList<>();
    JsonArray arr = GsonHelper.getAsJsonArray(json, key);
    for (JsonElement e : arr) {
      list.add(Complex.fromJson(e.getAsJsonObject()));
    }
    return list;
  }

  @Override
  public MapCodec<IComplexRecipe> codec() {
    return codec;
  }

  @Override
  public StreamCodec<RegistryFriendlyByteBuf, IComplexRecipe> streamCodec() {
    return streamCodec;
  }

  private void toNetwork(RegistryFriendlyByteBuf buf, IComplexRecipe recipe) {
    buf.writeVarInt(recipe.inputs().size());
    for (Complex ing : recipe.inputs()) {
      ing.encode(buf);
    }
    buf.writeVarInt(recipe.outputs().size());
    for (Complex ing : recipe.outputs()) {
      ing.encode(buf);
    }
    buf.writeVarInt(recipe.time());
  }

  private IComplexRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
    int inSize = buf.readVarInt();
    List<Complex> inputs = new ArrayList<>();
    for (int i = 0; i < inSize; i++) {
      inputs.add(Complex.decodeNetwork(buf));
    }

    int outSize = buf.readVarInt();
    List<Complex> outputs = new ArrayList<>();
    for (int i = 0; i < outSize; i++) {
      outputs.add(Complex.decodeNetwork(buf));
    }

    int time = buf.readVarInt();
    return createRecipe(inputs, outputs, time, null);
  }

  private IComplexRecipe createRecipe(List<Complex> inputs, List<Complex> outputs,
                                      int time, @Nullable ResourceLocation id) {
    ComplexRecipe recipe = new ComplexRecipe(
        id != null ? id : Ten4.id("dynamic"),
        inputs, outputs, time);
    recipe.serializer = this;
    recipe.recipeType = recipeType.get();
    return recipe;
  }
}
