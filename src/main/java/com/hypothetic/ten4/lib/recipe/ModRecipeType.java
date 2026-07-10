package com.hypothetic.ten4.lib.recipe;

import net.minecraft.world.item.crafting.RecipeType;

public class ModRecipeType<T extends ModRecipe> implements RecipeType<T> {
  private final String id;

  public ModRecipeType(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return id;
  }
}
