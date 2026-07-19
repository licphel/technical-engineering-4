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

public class RefiningCategory extends ModRecipeCategory<IComplexRecipe> {
  public RefiningCategory(IGuiHelper helper, RecipeType<IComplexRecipe> type) {
    super(helper, type, ModBlocks.REFINER.get().asItem().getDefaultInstance());
  }

  @Override
  public int getWidth() {
    return 133;
  }

  @Override
  public int getHeight() {
    return 54;
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, IComplexRecipe recipe, IFocusGroup focuses) {
    List<Complex> inputs = recipe.itemInputs();
    addItemInput(builder, !inputs.isEmpty() ? inputs.getFirst() : Complex.EMPTY, 40, 18);

    List<Complex> outputs = recipe.itemOutputs();
    addItemOutput(builder, !outputs.isEmpty() ? outputs.getFirst() : Complex.EMPTY, 92, 18);

    inputs = recipe.fluidInputs();
    addFluidInput(builder, !inputs.isEmpty() ? inputs.getFirst() : Complex.EMPTY, 17, 3);

    outputs = recipe.fluidOutputs();
    addFluidOutput(builder, !outputs.isEmpty() ? outputs.getFirst() : Complex.EMPTY, 114, 3);
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, IComplexRecipe recipe, IFocusGroup focuses) {
    super.createRecipeExtras(builder, recipe, focuses);

    addEnergyGauge(builder, 2, 2, false);
    addProgressGauge(builder, recipe.time(), 63, 18);
  }
}
