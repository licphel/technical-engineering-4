package com.hypothetic.ten4.lib.recipe;

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

public class ModRecipeSerializer implements RecipeSerializer<ModRecipe> {
  public static final int DEFAULT_TIME = 150;
  private static final Codec<List<CombinedIngredient>> INGREDIENT_LIST_CODEC =
      CombinedIngredient.CODEC.listOf();
  private final Supplier<RecipeType<?>> recipeType;
  private final MapCodec<ModRecipe> codec;
  private final StreamCodec<RegistryFriendlyByteBuf, ModRecipe> streamCodec;

  public ModRecipeSerializer(Supplier<RecipeType<?>> recipeType) {
    this.recipeType = recipeType;
    this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
        INGREDIENT_LIST_CODEC.fieldOf("inputs").forGetter(ModRecipe::inputs),
        INGREDIENT_LIST_CODEC.fieldOf("outputs").forGetter(ModRecipe::outputs),
        Codec.INT.optionalFieldOf("time", DEFAULT_TIME).forGetter(ModRecipe::time)
    ).apply(instance, (inputs, outputs, time) -> createRecipe(inputs, outputs, time, null)));
    this.streamCodec = StreamCodec.of(this::toNetwork, this::fromNetwork);
  }

  private static List<CombinedIngredient> readIngredientList(JsonObject json, String key) {
    List<CombinedIngredient> list = new ArrayList<>();
    JsonArray arr = GsonHelper.getAsJsonArray(json, key);
    for (JsonElement e : arr) {
      list.add(CombinedIngredient.fromJson(e.getAsJsonObject()));
    }
    return list;
  }

  @Override
  public MapCodec<ModRecipe> codec() {
    return codec;
  }

  @Override
  public StreamCodec<RegistryFriendlyByteBuf, ModRecipe> streamCodec() {
    return streamCodec;
  }

  public ModRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
    List<CombinedIngredient> inputs = readIngredientList(json, "inputs");
    List<CombinedIngredient> outputs = readIngredientList(json, "outputs");
    int time = JsonParser.getIntOr(json, "time", DEFAULT_TIME);
    return createRecipe(inputs, outputs, time, recipeId);
  }

  private void toNetwork(RegistryFriendlyByteBuf buf, ModRecipe recipe) {
    buf.writeVarInt(recipe.inputs.size());
    for (CombinedIngredient ing : recipe.inputs) {
      ing.writeTo(buf);
    }
    buf.writeVarInt(recipe.outputs.size());
    for (CombinedIngredient ing : recipe.outputs) {
      ing.writeTo(buf);
    }
    buf.writeVarInt(recipe.time);
  }

  private ModRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
    int inSize = buf.readVarInt();
    List<CombinedIngredient> inputs = new ArrayList<>();
    for (int i = 0; i < inSize; i++) {
      inputs.add(CombinedIngredient.fromNetwork(buf));
    }

    int outSize = buf.readVarInt();
    List<CombinedIngredient> outputs = new ArrayList<>();
    for (int i = 0; i < outSize; i++) {
      outputs.add(CombinedIngredient.fromNetwork(buf));
    }

    int time = buf.readVarInt();
    return createRecipe(inputs, outputs, time, null);
  }

  private ModRecipe createRecipe(List<CombinedIngredient> inputs, List<CombinedIngredient> outputs,
                                 int time, @Nullable ResourceLocation id) {
    ModRecipe recipe = new ModRecipe(
        id != null ? id : Ten4.id("dynamic"),
        inputs, outputs, time);
    recipe.serializer = this;
    recipe.recipeType = recipeType.get();
    return recipe;
  }
}
