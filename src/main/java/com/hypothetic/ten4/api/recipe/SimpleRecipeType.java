package com.hypothetic.ten4.api.recipe;

import net.minecraft.world.item.crafting.RecipeType;

public class SimpleRecipeType<T extends ComplexRecipe> implements RecipeType<T> {
  private final String id;

  public SimpleRecipeType(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return id;
  }
}
