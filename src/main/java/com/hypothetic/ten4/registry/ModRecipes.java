package com.hypothetic.ten4.registry;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.recipe.ComplexRecipeSerializer;
import com.hypothetic.ten4.api.recipe.IComplexRecipe;
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
      SERIALIZERS.register("pulverizer", () -> new ComplexRecipeSerializer(PULVERIZER_TYPE));
  
  @SuppressWarnings("unchecked")
  public static RecipeType<IComplexRecipe> typeOf(DeferredHolder<RecipeType<?>, RecipeType<?>> holder) {
    return (RecipeType<IComplexRecipe>) holder.get();
  }

  public static void trigger() {
    // just trigger the class loader
  }
}
