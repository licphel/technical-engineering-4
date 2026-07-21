package com.hypothetic.ten4.compat.jei;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.client.ComponentedContainerScreen;
import com.hypothetic.ten4.api.client.components.GaugeFluid;
import com.hypothetic.ten4.api.client.components.UiComponent;
import com.hypothetic.ten4.api.client.components.UiTag;
import com.hypothetic.ten4.api.recipe.IComplexRecipe;
import com.hypothetic.ten4.compat.jei.core.*;
import com.hypothetic.ten4.core.client.screen.*;
import com.hypothetic.ten4.core.registry.ModBlocks;
import com.hypothetic.ten4.core.registry.ModRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {
  public static final RecipeType<IComplexRecipe> PULVERIZING = new RecipeType<>(Ten4.id("pulverizing"), IComplexRecipe.class);
  public static final RecipeType<IComplexRecipe> PRESSING = new RecipeType<>(Ten4.id("pressing"), IComplexRecipe.class);
  public static final RecipeType<SmeltingRecipe> ELECTRICAL_SMELTING = new RecipeType<>(Ten4.id("electrical_smelting"), SmeltingRecipe.class);
  public static final RecipeType<IComplexRecipe> REFINING = new RecipeType<>(Ten4.id("refining"), IComplexRecipe.class);
  public static final RecipeType<ItemStack> HEAT_GENERATING = new RecipeType<>(Ten4.id("heat_generating"), ItemStack.class);

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

    registry.addRecipeCategories(new PulverizingCategory(helper, PULVERIZING));
    registry.addRecipeCategories(new PressingCategory(helper, PRESSING));
    registry.addRecipeCategories(new ElectricalSmeltingCategory(helper, ELECTRICAL_SMELTING));
    registry.addRecipeCategories(new RefiningCategory(helper, REFINING));
    registry.addRecipeCategories(new HeatGeneratingCategory(helper, HEAT_GENERATING));
  }

  @Override
  public void registerRecipes(IRecipeRegistration registry) {
    Level level = Minecraft.getInstance().level;
    if (level == null) {
      return;
    }

    RecipeManager rm = level.getRecipeManager();
    register(registry, rm, ModRecipes.PULVERIZING.get(), PULVERIZING);
    register(registry, rm, ModRecipes.PRESSING.get(), PRESSING);
    register(registry, rm, net.minecraft.world.item.crafting.RecipeType.SMELTING, ELECTRICAL_SMELTING);
    register(registry, rm, ModRecipes.REFINING.get(), REFINING);

    // Heat generator: use vanilla smelting fuels
    List<ItemStack> fuels = new ArrayList<>();
    for (Item item : BuiltInRegistries.ITEM) {
      if (item.getDefaultInstance().getBurnTime(net.minecraft.world.item.crafting.RecipeType.SMELTING) > 0) {
        fuels.add(new ItemStack(item));
      }
    }
    registry.addRecipes(HEAT_GENERATING, fuels);
  }

  @Override
  public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
    registry.addRecipeCatalyst(new ItemStack(ModBlocks.PULVERIZER.get()), PULVERIZING);
    registry.addRecipeCatalyst(new ItemStack(ModBlocks.PRESS.get()), PRESSING);
    registry.addRecipeCatalyst(new ItemStack(ModBlocks.SMELTER.get()), ELECTRICAL_SMELTING);
    registry.addRecipeCatalyst(new ItemStack(ModBlocks.REFINER.get()), REFINING);
    registry.addRecipeCatalyst(new ItemStack(ModBlocks.HEAT_GENERATOR.get()), HEAT_GENERATING);
  }

  @Override
  public void registerGuiHandlers(IGuiHandlerRegistration registry) {
    registry.addGuiContainerHandler(PulverizerScreen.class, new RecipeClickDetector(PULVERIZING));
    registry.addGuiContainerHandler(PressScreen.class, new RecipeClickDetector(PRESSING));
    registry.addGuiContainerHandler(SmelterScreen.class, new RecipeClickDetector(ELECTRICAL_SMELTING));
    registry.addGuiContainerHandler(RefinerScreen.class, new RecipeClickDetector(REFINING));
    registry.addGuiContainerHandler(HeatGeneratorScreen.class, new RecipeClickDetector(HEAT_GENERATING));

    // General Handler
    registry.addGenericGuiContainerHandler(ComponentedContainerScreen.class, new GeneralDetector(registry));
  }
}
