package com.hypothetic.ten4.compat.jei;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.compat.jei.core.HeatGeneratorCategory;
import com.hypothetic.ten4.compat.jei.core.PulverizerCategory;
import com.hypothetic.ten4.core.device.HeatGeneratorScreen;
import com.hypothetic.ten4.core.device.PulverizerScreen;
import com.hypothetic.ten4.registry.ModBlocks;
import com.hypothetic.ten4.registry.ModRecipes;
import com.hypothetic.ten4.api.client.ComponentScreen;
import com.hypothetic.ten4.api.recipe.ModRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {
  public static final RecipeType<ModRecipe> PULVERIZER =
      new RecipeType<>(Ten4.id("pulverizer"), ModRecipe.class);
  public static final RecipeType<ItemStack> HEAT_GENERATOR =
      new RecipeType<>(Ten4.id("heat_generator"), ItemStack.class);

  private static void register(IRecipeRegistration registry,
                               RecipeManager rm,
                               DeferredHolder<?, ?> holder,
                               RecipeType<ModRecipe> jeiType) {
    net.minecraft.world.item.crafting.RecipeType<ModRecipe> type =
        (net.minecraft.world.item.crafting.RecipeType<ModRecipe>) holder.get();
    List<RecipeHolder<ModRecipe>> holders = rm.getAllRecipesFor(type);
    List<ModRecipe> recipes = holders.stream().map(RecipeHolder::value).toList();
    registry.addRecipes(jeiType, recipes);
  }

  @Override
  public ResourceLocation getPluginUid() {
    return Ten4.id("jei_plugin");
  }

  @Override
  public void registerCategories(IRecipeCategoryRegistration registry) {
    IGuiHelper helper = registry.getJeiHelpers().getGuiHelper();

    registry.addRecipeCategories(new PulverizerCategory(helper, PULVERIZER));
    registry.addRecipeCategories(new HeatGeneratorCategory(helper, HEAT_GENERATOR));
  }

  @Override
  public void registerRecipes(IRecipeRegistration registry) {
    Level level = Minecraft.getInstance().level;
    if (level == null) {
      return;
    }

    RecipeManager rm = level.getRecipeManager();
    register(registry, rm, ModRecipes.PULVERIZER_TYPE, PULVERIZER);

    // Heat generator: use vanilla smelting fuels
    List<ItemStack> fuels = new ArrayList<>();
    for (Item item : BuiltInRegistries.ITEM) {
      if (item.getDefaultInstance().getBurnTime(net.minecraft.world.item.crafting.RecipeType.SMELTING) > 0) {
        fuels.add(new ItemStack(item));
      }
    }
    registry.addRecipes(HEAT_GENERATOR, fuels);
  }

  @Override
  public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
    registry.addRecipeCatalyst(new ItemStack(ModBlocks.PULVERIZER.get()), PULVERIZER);
    registry.addRecipeCatalyst(new ItemStack(ModBlocks.HEAT_GENERATOR.get()), HEAT_GENERATOR);
  }

  @Override
  public void registerGuiHandlers(IGuiHandlerRegistration registry) {
    registry.addRecipeClickArea(PulverizerScreen.class, 68, 35, 22, 16, PULVERIZER);
    registry.addRecipeClickArea(HeatGeneratorScreen.class, 80, 36, 14, 14, HEAT_GENERATOR);

    // Excluded areas
    registry.addGenericGuiContainerHandler(ComponentScreen.class, new IGuiContainerHandler<>() {
      @Override
      public List<Rect2i> getGuiExtraAreas(AbstractContainerScreen<?> containerScreen) {
        if (containerScreen instanceof ComponentScreen<?> cs) {
          return cs.getComponentAreas();
        }
        return Collections.emptyList();
      }
    });
  }
}
