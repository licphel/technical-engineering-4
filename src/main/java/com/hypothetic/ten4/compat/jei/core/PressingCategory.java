package com.hypothetic.ten4.compat.jei.core;

import com.hypothetic.ten4.api.recipe.Complex;
import com.hypothetic.ten4.api.recipe.IComplexRecipe;
import com.hypothetic.ten4.compat.jei.ModRecipeCategory;
import com.hypothetic.ten4.core.registry.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;

import java.util.List;

public class PressingCategory extends ModRecipeCategory<IComplexRecipe> {
  public PressingCategory(IGuiHelper helper, RecipeType<IComplexRecipe> type) {
    super(helper, type, ModBlocks.PRESS.get().asItem().getDefaultInstance());
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
    addItemInput(builder, !inputs.isEmpty() ? inputs.getFirst() : Complex.EMPTY, 15, 6);
    addItemInput(builder, inputs.size() > 1 ? inputs.get(1) : Complex.EMPTY, 15, 32);

    List<Complex> outputs = recipe.outputs();
    addItemOutput(builder, !outputs.isEmpty() ? outputs.getFirst() : Complex.EMPTY, 71, 18);
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, IComplexRecipe recipe, IFocusGroup focuses) {
    super.createRecipeExtras(builder, recipe, focuses);

    addEnergyGauge(builder, 2, 2, false);
    addProgressGauge(builder, recipe.time(), 38, 18);
  }
}
