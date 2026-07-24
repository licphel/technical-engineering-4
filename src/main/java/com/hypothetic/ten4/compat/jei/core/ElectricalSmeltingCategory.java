package com.hypothetic.ten4.compat.jei.core;

import com.hypothetic.ten4.compat.jei.ModRecipeCategory;
import com.hypothetic.ten4.core.registry.ModBlocks;
import com.hypothetic.ten4.util.RegistryUtil;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmeltingRecipe;

import java.util.Arrays;

public class ElectricalSmeltingCategory extends ModRecipeCategory<SmeltingRecipe> {
  public ElectricalSmeltingCategory(IGuiHelper helper, RecipeType<SmeltingRecipe> type) {
    super(helper, type, ModBlocks.SMELTER.get().asItem().getDefaultInstance());
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
  public void setRecipe(IRecipeLayoutBuilder builder, SmeltingRecipe recipe, IFocusGroup focuses) {
    NonNullList<Ingredient> inputs = recipe.getIngredients();
    builder.addInputSlot(15, 18)
        .addItemStacks(Arrays.stream(inputs.getFirst().getItems()).toList())
        .setStandardSlotBackground();
    builder.addOutputSlot(71, 18)
        .addItemStack(recipe.getResultItem(RegistryUtil.registryAccess()))
        .setOutputSlotBackground();
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, SmeltingRecipe recipe, IFocusGroup focuses) {
    super.createRecipeExtras(builder, recipe, focuses);

    addEnergyGauge(builder, 2, 2, false);
    addProgressGauge(builder, recipe.getCookingTime(), 38, 18);
  }
}
