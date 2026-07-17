package com.hypothetic.ten4.api.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class ComplexRecipe implements IComplexRecipe {
  final List<Complex> inputs;
  final List<Complex> outputs;
  final List<Complex> itemInputs;
  final List<Complex> fluidInputs;
  final List<Complex> itemOutputs;
  final List<Complex> fluidOutputs;
  final ResourceLocation id;
  final int time;

  RecipeSerializer<?> serializer;
  RecipeType<?> recipeType;

  public ComplexRecipe(ResourceLocation id, List<Complex> inputs, List<Complex> outputs, int time) {
    this.id = id;
    this.inputs = List.copyOf(inputs);
    this.outputs = List.copyOf(outputs);
    this.itemInputs = inputs.stream().filter(Complex::isItem).toList();
    this.fluidInputs = inputs.stream().filter(Complex::isFluid).toList();
    this.itemOutputs = outputs.stream().filter(Complex::isItem).toList();
    this.fluidOutputs = outputs.stream().filter(Complex::isFluid).toList();
    this.time = time;
  }

  @Override
  public List<Complex> inputs() {
    return inputs;
  }

  @Override
  public List<Complex> outputs() {
    return outputs;
  }

  @Override
  public List<Complex> itemInputs() {
    return itemInputs;
  }

  @Override
  public List<Complex> fluidInputs() {
    return fluidInputs;
  }

  @Override
  public List<Complex> itemOutputs() {
    return itemOutputs;
  }

  @Override
  public List<Complex> fluidOutputs() {
    return fluidOutputs;
  }

  @Override
  public int time() {
    return time;
  }

  @Override
  public List<ItemStack> generateItems() {
    List<ItemStack> result = new ArrayList<>();
    for (Complex ing : outputs) {
      if (ing.isItem()) {
        result.add(ing.genItem());
      }
    }
    return result;
  }

  @Override
  public List<FluidStack> generateFluids() {
    List<FluidStack> result = new ArrayList<>();
    for (Complex ing : outputs) {
      if (ing.isFluid()) {
        result.add(ing.genFluid());
      }
    }
    return result;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return serializer;
  }

  @Override
  public RecipeType<?> getType() {
    return recipeType;
  }

  @Override
  public ResourceLocation getId() {
    return id;
  }

  public void setSerializer(RecipeSerializer<?> serializer) {
    this.serializer = serializer;
  }

  public void setRecipeType(RecipeType<?> recipeType) {
    this.recipeType = recipeType;
  }

  @Deprecated
  @Override
  public boolean matches(RecipeInput inv, Level level) {
    return true; // handled elsewhere
  }

  @Deprecated
  @Override
  public ItemStack assemble(RecipeInput inv, HolderLookup.Provider registries) {
    return ItemStack.EMPTY; // handled elsewhere
  }

  @Override
  public boolean canCraftInDimensions(int w, int h) {
    return true;
  }

  @Override
  public ItemStack getResultItem(HolderLookup.Provider registries) {
    for (Complex ing : outputs) {
      if (ing.isItem()) {
        return ing.symbolItem();
      }
    }
    return ItemStack.EMPTY;
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    NonNullList<Ingredient> list = NonNullList.create();
    for (Complex ing : inputs) {
      if (ing.isItem() && !ing.isEmpty()) {
        list.add(ing.isTag() ?
            Ingredient.of(TagKey.create(Registries.ITEM, ing.id())) :
            Ingredient.of(ing.itemStacks().stream()));
      }
    }
    return list;
  }

  @Override
  public boolean isSpecial() {
    return true;
  }
}
