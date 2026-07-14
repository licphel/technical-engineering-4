package com.hypothetic.ten4.api.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public interface IComplexRecipe extends Recipe<RecipeInput> {
  List<Complex> inputs();

  List<Complex> outputs();

  List<Complex> itemInputs();

  List<Complex> fluidInputs();

  List<Complex> itemOutputs();

  List<Complex> fluidOutputs();

  int time();

  List<ItemStack> generateItems();

  List<FluidStack> generateFluids();

  RecipeSerializer<?> getSerializer();

  RecipeType<?> getType();

  ResourceLocation getId();
}
