package com.hypothetic.ten4.datagen.recipe;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.recipe.Complex;
import com.hypothetic.ten4.api.recipe.ComplexRecipe;
import com.hypothetic.ten4.core.registry.ModRecipes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Generates pulverizing recipes that convert each log type to its corresponding planks.
 * Scans {@code #minecraft:logs} at datagen time, maps log name → planks name.
 */
public class SawingRecipeData extends RecipeProvider {

  public SawingRecipeData(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
    super(output, registries);
  }

  @Override
  protected void buildRecipes(RecipeOutput output) {
    var logsTag = BuiltInRegistries.ITEM.getOrCreateTag(ItemTags.LOGS);
    Set<Item> plankCache = new LinkedHashSet<>();

    for (Holder<Item> holder : logsTag) {
      Item log = holder.value();
      if (log == Items.AIR) continue;

      Item planks = findPlanks(log);
      if (planks == Items.AIR || !plankCache.add(planks)) continue;

      ResourceLocation logId = BuiltInRegistries.ITEM.getKey(log);
      String name = logId.getPath().replace("stripped_", "");

      // Remove wood-type suffix to get the base wood name
      for (String suffix : new String[]{"_log", "_wood", "_stem", "_hyphae", "_block"}) {
        if (name.endsWith(suffix)) {
          name = name.substring(0, name.length() - suffix.length());
          break;
        }
      }

      List<Complex> inputs = List.of(Complex.of(Complex.Kind.ITEM, logId, 1, 1.0));
      ResourceLocation plankId = BuiltInRegistries.ITEM.getKey(planks);
      List<Complex> outputs = List.of(
          Complex.of(Complex.Kind.ITEM, plankId, 4, 1.0),
          Complex.of(Complex.Kind.ITEM, plankId, 1, 0.5),
          Complex.of(Complex.Kind.ITEM, plankId, 1, 0.25)
      );

      ResourceLocation recipeId = Ten4.id("pulverizing/" + name + "_planks_from_log");
      ComplexRecipe recipe = new ComplexRecipe(recipeId, inputs, outputs, 200);
      recipe.setSerializer(ModRecipes.PULVERIZING_SER.get());
      recipe.setRecipeType(ModRecipes.PULVERIZING.get());
      output.accept(recipeId, recipe, null);
    }
  }

  /** Derive the plank item from a log item: oak_log → oak_planks, stripped_oak_log → oak_planks, etc. */
  private static Item findPlanks(Item log) {
    ResourceLocation id = BuiltInRegistries.ITEM.getKey(log);
    String path = id.getPath().replace("stripped_", "");
    for (String suffix : new String[]{"_log", "_wood", "_stem", "_hyphae", "_block"}) {
      if (path.endsWith(suffix)) {
        path = path.substring(0, path.length() - suffix.length()) + "_planks";
        break;
      }
    }
    return BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), path));
  }
}
