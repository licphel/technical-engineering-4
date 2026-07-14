package com.hypothetic.ten4.registry;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.recipe.ComplexRecipeSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
  public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, Ten4.ID);
  public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, Ten4.ID);

  public static final DeferredHolder<RecipeType<?>, RecipeType<?>> PULVERIZER_TYPE = simpleType("pulverizer");
  public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> PULVERIZER_SERIALIZER = SERIALIZERS.register("pulverizer", () -> new ComplexRecipeSerializer(PULVERIZER_TYPE));

  public static DeferredHolder<RecipeType<?>, RecipeType<?>> simpleType(String id) {
    return TYPES.register(id, () -> RecipeType.simple(Ten4.id(id)));
  }

  public static void trigger() {
    // just trigger the class loader
  }
}
