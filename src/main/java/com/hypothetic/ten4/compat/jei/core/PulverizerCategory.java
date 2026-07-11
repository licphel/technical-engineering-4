package com.hypothetic.ten4.compat.jei.core;

import com.hypothetic.ten4.compat.jei.ModRecipeCategory;
import com.hypothetic.ten4.init.ModBlocks;
import com.hypothetic.ten4.lib.recipe.RecipeEntry;
import com.hypothetic.ten4.lib.recipe.ModRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;

import java.util.List;

public class PulverizerCategory extends ModRecipeCategory<ModRecipe> {
  public PulverizerCategory(IGuiHelper helper, RecipeType<ModRecipe> type) {
    super(helper, type, ModBlocks.PULVERIZER.get().asItem().getDefaultInstance());
  }

  @Override
  public int getWidth() {
    return 102;
  }

  @Override
  public int getHeight() {
    return 54;
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, ModRecipe recipe, IFocusGroup focuses) {
    List<RecipeEntry> inputs = recipe.inputs();
    addItemInput(builder, !inputs.isEmpty() ? inputs.getFirst() : RecipeEntry.empty(), 15, 18);

    List<RecipeEntry> outputs = recipe.outputs();
    addItemOutput(builder, !outputs.isEmpty() ? outputs.getFirst() : RecipeEntry.empty(), 62, 10);
    addItemOutput(builder, outputs.size() > 1 ? outputs.get(1) : RecipeEntry.empty(), 80, 10);
    addItemOutput(builder, outputs.size() > 2 ? outputs.get(2) : RecipeEntry.empty(), 62, 28);
    addItemOutput(builder, outputs.size() > 3 ? outputs.get(3) : RecipeEntry.empty(), 80, 28);
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, ModRecipe recipe, IFocusGroup focuses) {
    super.createRecipeExtras(builder, recipe, focuses);

    addEnergyGauge(builder, 2, 2, false);
    addProgressGauge(builder, recipe, 35, 18);
  }
}
