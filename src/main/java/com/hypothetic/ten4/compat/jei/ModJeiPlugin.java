package com.hypothetic.ten4.compat.jei;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.client.ComponentedContainerScreen;
import com.hypothetic.ten4.api.recipe.ComplexRecipe;
import com.hypothetic.ten4.api.recipe.IComplexRecipe;
import com.hypothetic.ten4.compat.jei.core.HeatGeneratorCategory;
import com.hypothetic.ten4.compat.jei.core.PulverizerCategory;
import com.hypothetic.ten4.core.client.screen.HeatGeneratorScreen;
import com.hypothetic.ten4.core.client.screen.PulverizerScreen;
import com.hypothetic.ten4.registry.ModBlocks;
import com.hypothetic.ten4.registry.ModRecipes;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {
  public static final RecipeType<IComplexRecipe> PULVERIZER =
      new RecipeType<>(Ten4.id("pulverizer"), ComplexRecipe.class);
  public static final RecipeType<ItemStack> HEAT_GENERATOR =
      new RecipeType<>(Ten4.id("heat_generator"), ItemStack.class);

  private static void register(IRecipeRegistration registry,
                               RecipeManager rm,
                               DeferredHolder<?, ?> holder,
                               RecipeType<IComplexRecipe> jeiType) {
    net.minecraft.world.item.crafting.RecipeType<IComplexRecipe> type =
        (net.minecraft.world.item.crafting.RecipeType<IComplexRecipe>) holder.get();
    List<RecipeHolder<IComplexRecipe>> holders = rm.getAllRecipesFor(type);
    List<IComplexRecipe> recipes = holders.stream().map(RecipeHolder::value).toList();
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
