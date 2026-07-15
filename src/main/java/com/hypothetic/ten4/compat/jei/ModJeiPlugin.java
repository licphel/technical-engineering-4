package com.hypothetic.ten4.compat.jei;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.client.ComponentedContainerScreen;
import com.hypothetic.ten4.api.recipe.ComplexRecipe;
import com.hypothetic.ten4.api.recipe.IComplexRecipe;
import com.hypothetic.ten4.compat.jei.core.HeatGeneratorCategory;
import com.hypothetic.ten4.compat.jei.core.PulverizerCategory;
import com.hypothetic.ten4.compat.jei.core.SmelterCategory;
import com.hypothetic.ten4.core.client.screen.HeatGeneratorScreen;
import com.hypothetic.ten4.core.client.screen.PulverizerScreen;
import com.hypothetic.ten4.core.client.screen.SmelterScreen;
import com.hypothetic.ten4.core.registry.ModBlocks;
import com.hypothetic.ten4.core.registry.ModRecipes;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {
  public static final RecipeType<IComplexRecipe> PULVERIZER = new RecipeType<>(Ten4.id("pulverizer"), ComplexRecipe.class);
  public static final RecipeType<SmeltingRecipe> SMELTER = new RecipeType<>(Ten4.id("smelter"), SmeltingRecipe.class);
  public static final RecipeType<ItemStack> HEAT_GENERATOR = new RecipeType<>(Ten4.id("heat_generator"), ItemStack.class);

  private static <I extends RecipeInput, R extends Recipe<I>,
      T extends net.minecraft.world.item.crafting.RecipeType<R>>
  void register(IRecipeRegistration registry,
                RecipeManager rm,
                T type,
                RecipeType<R> jeiType) {
    List<RecipeHolder<R>> holders = rm.getAllRecipesFor(type);
    List<R> recipes = holders.stream().map(RecipeHolder::value).toList();
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
    registry.addRecipeCategories(new SmelterCategory(helper, SMELTER));
    registry.addRecipeCategories(new HeatGeneratorCategory(helper, HEAT_GENERATOR));
  }

  @Override
  public void registerRecipes(IRecipeRegistration registry) {
    Level level = Minecraft.getInstance().level;
    if (level == null) {
      return;
    }

    RecipeManager rm = level.getRecipeManager();
    register(registry, rm, ModRecipes.PULVERIZING.get(), PULVERIZER);
    register(registry, rm, net.minecraft.world.item.crafting.RecipeType.SMELTING, SMELTER);

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
    registry.addRecipeCatalyst(new ItemStack(ModBlocks.SMELTER.get()), SMELTER);
  }

  @Override
  public void registerGuiHandlers(IGuiHandlerRegistration registry) {
    registry.addRecipeClickArea(PulverizerScreen.class, 68, 35, 22, 16, PULVERIZER);
    registry.addRecipeClickArea(HeatGeneratorScreen.class, 80, 36, 14, 14, HEAT_GENERATOR);
    registry.addRecipeClickArea(SmelterScreen.class, 75, 35, 22, 16, SMELTER);

    // Excluded areas
    registry.addGenericGuiContainerHandler(ComponentedContainerScreen.class, new IGuiContainerHandler<>() {
      @Override
      public List<Rect2i> getGuiExtraAreas(AbstractContainerScreen<?> containerScreen) {
        if (containerScreen instanceof ComponentedContainerScreen<?> cs) {
          return cs.getComponentAreas();
        }
        return Collections.emptyList();
      }
    });
  }
}
