package com.hypothetic.ten4.compat.jei.core;

import com.hypothetic.ten4.api.recipe.Complex;
import com.hypothetic.ten4.api.recipe.IComplexRecipe;
import com.hypothetic.ten4.compat.jei.JeiModCategory;
import com.hypothetic.ten4.core.registry.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;

import java.util.List;

public class FuelGeneratingCategory extends JeiModCategory<IComplexRecipe> {
  public FuelGeneratingCategory(IGuiHelper helper, RecipeType<IComplexRecipe> type) {
    super(helper, type, ModBlocks.FUEL_GENERATOR.get().asItem().getDefaultInstance());
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
    addFluidInput(builder, !inputs.isEmpty() ? inputs.getFirst() : Complex.EMPTY, 15, 3);
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, IComplexRecipe recipe, IFocusGroup focuses) {
    super.createRecipeExtras(builder, recipe, focuses);

    addEnergyGauge(builder, 75, 2, true);
    addFuelGauge(builder, recipe.time(), 45, 19);
  }
}
