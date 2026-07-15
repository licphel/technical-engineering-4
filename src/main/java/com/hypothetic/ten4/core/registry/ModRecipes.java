package com.hypothetic.ten4.core.registry;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.recipe.ComplexRecipeSerializer;
import com.hypothetic.ten4.api.recipe.IComplexRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
  public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, Ten4.ID);
  public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, Ten4.ID);

  public static final DeferredHolder<RecipeType<?>, RecipeType<IComplexRecipe>> PULVERIZING = simpleType("pulverizing");
  public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<IComplexRecipe>> PULVERIZING_SER = SERIALIZERS.register("pulverizing", () -> new ComplexRecipeSerializer(PULVERIZING));

  public static DeferredHolder<RecipeType<?>, RecipeType<IComplexRecipe>> simpleType(String id) {
    return TYPES.register(id, () -> RecipeType.simple(Ten4.id(id)));
  }
}
