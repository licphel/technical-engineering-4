package com.hypothetic.ten4.compat.jei.core;

import com.hypothetic.ten4.compat.jei.ModRecipeCategory;
import com.hypothetic.ten4.core.registry.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.item.ItemStack;

public class HeatGeneratingCategory extends ModRecipeCategory<ItemStack> {
  public HeatGeneratingCategory(IGuiHelper helper, RecipeType<ItemStack> type) {
    super(helper, type, ModBlocks.HEAT_GENERATOR.get().asItem().getDefaultInstance());
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
  public void setRecipe(IRecipeLayoutBuilder builder, ItemStack fuel, IFocusGroup focuses) {
    builder.addInputSlot(15, 18)
        .addItemStack(fuel)
        .setStandardSlotBackground();
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, ItemStack fuel, IFocusGroup focuses) {
    super.createRecipeExtras(builder, fuel, focuses);

    addEnergyGauge(builder, 75, 2, true);
    addFuelGauge(builder, fuel.getBurnTime(net.minecraft.world.item.crafting.RecipeType.SMELTING), 45, 19);
  }
}
