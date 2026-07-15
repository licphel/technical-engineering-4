package com.hypothetic.ten4.compat.jei.core;

import com.hypothetic.ten4.api.recipe.Complex;
import com.hypothetic.ten4.api.recipe.IComplexRecipe;
import com.hypothetic.ten4.compat.jei.ModRecipeCategory;
import com.hypothetic.ten4.registry.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;

import java.util.List;

public class PulverizerCategory extends ModRecipeCategory<IComplexRecipe> {
  public PulverizerCategory(IGuiHelper helper, RecipeType<IComplexRecipe> type) {
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
  public void setRecipe(IRecipeLayoutBuilder builder, IComplexRecipe recipe, IFocusGroup focuses) {
    List<Complex> inputs = recipe.inputs();
    addItemInput(builder, !inputs.isEmpty() ? inputs.getFirst() : Complex.EMPTY, 15, 18);

    List<Complex> outputs = recipe.outputs();
    addItemOutput(builder, !outputs.isEmpty() ? outputs.getFirst() : Complex.EMPTY, 62, 10);
    addItemOutput(builder, outputs.size() > 1 ? outputs.get(1) : Complex.EMPTY, 80, 10);
    addItemOutput(builder, outputs.size() > 2 ? outputs.get(2) : Complex.EMPTY, 62, 28);
    addItemOutput(builder, outputs.size() > 3 ? outputs.get(3) : Complex.EMPTY, 80, 28);
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, IComplexRecipe recipe, IFocusGroup focuses) {
    super.createRecipeExtras(builder, recipe, focuses);

    addEnergyGauge(builder, 2, 2, false);
    addProgressGauge(builder, recipe.time(), 35, 18);
  }
}
