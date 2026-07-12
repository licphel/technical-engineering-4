package com.hypothetic.ten4.registry;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.recipe.ModRecipe;
import com.hypothetic.ten4.api.recipe.ModRecipeSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
  public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, Ten4.ID);
  public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, Ten4.ID);

  public static final DeferredHolder<RecipeType<?>, RecipeType<?>> PULVERIZER_TYPE =
      TYPES.register("pulverizer", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Ten4.ID, "pulverizer")));
  public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> PULVERIZER_SER =
      SERIALIZERS.register("pulverizer", () -> new ModRecipeSerializer(PULVERIZER_TYPE));

  public static final DeferredHolder<RecipeType<?>, RecipeType<?>> COMPRESSOR_TYPE =
      TYPES.register("compressor", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Ten4.ID, "compressor")));
  public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> COMPRESSOR_SER =
      SERIALIZERS.register("compressor", () -> new ModRecipeSerializer(COMPRESSOR_TYPE));

  public static final DeferredHolder<RecipeType<?>, RecipeType<?>> PSIONICANT_TYPE =
      TYPES.register("psionicant", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Ten4.ID, "psionicant")));
  public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> PSIONICANT_SER =
      SERIALIZERS.register("psionicant", () -> new ModRecipeSerializer(PSIONICANT_TYPE));

  public static final DeferredHolder<RecipeType<?>, RecipeType<?>> INDUCTION_FURNACE_TYPE =
      TYPES.register("induction_furnace", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Ten4.ID, "induction_furnace")));
  public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> INDUCTION_FURNACE_SER =
      SERIALIZERS.register("induction_furnace", () -> new ModRecipeSerializer(INDUCTION_FURNACE_TYPE));

  public static final DeferredHolder<RecipeType<?>, RecipeType<?>> REFINER_TYPE =
      TYPES.register("refiner", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(Ten4.ID, "refiner")));
  public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> REFINER_SER =
      SERIALIZERS.register("refiner", () -> new ModRecipeSerializer(REFINER_TYPE));

  @SuppressWarnings("unchecked")
  public static RecipeType<ModRecipe> typeOf(DeferredHolder<RecipeType<?>, RecipeType<?>> holder) {
    return (RecipeType<ModRecipe>) holder.get();
  }

  public static void trigger() {
    // just trigger the class loader
  }
}
