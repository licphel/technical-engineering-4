package com.hypothetic.ten4.api.recipe;

import com.hypothetic.ten4.util.TagHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class ModRecipe implements Recipe<RecipeInput> {
  final List<RecipeEntry> inputs;
  final List<RecipeEntry> outputs;
  final List<RecipeEntry> itemInputs;
  final List<RecipeEntry> fluidInputs;
  final List<RecipeEntry> itemOutputs;
  final List<RecipeEntry> fluidOutputs;
  final ResourceLocation id;
  final int time;

  RecipeSerializer<?> serializer;
  RecipeType<?> recipeType;

  public ModRecipe(ResourceLocation id, List<RecipeEntry> inputs, List<RecipeEntry> outputs, int time) {
    this.id = id;
    this.inputs = List.copyOf(inputs);
    this.outputs = List.copyOf(outputs);
    this.itemInputs = inputs.stream().filter(RecipeEntry::isItem).toList();
    this.fluidInputs = inputs.stream().filter(RecipeEntry::isFluid).toList();
    this.itemOutputs = outputs.stream().filter(RecipeEntry::isItem).toList();
    this.fluidOutputs = outputs.stream().filter(RecipeEntry::isFluid).toList();
    this.time = time;
  }

  public List<RecipeEntry> inputs() {
    return inputs;
  }

  public List<RecipeEntry> outputs() {
    return outputs;
  }

  public int time() {
    return time;
  }

  public List<RecipeEntry> itemInputs() {
    return itemInputs;
  }

  public List<RecipeEntry> fluidInputs() {
    return fluidInputs;
  }

  public List<RecipeEntry> itemOutputs() {
    return itemOutputs;
  }

  public List<RecipeEntry> fluidOutputs() {
    return fluidOutputs;
  }

  public int inputLimit(ItemStack stack) {
    for (RecipeEntry ing : inputs) {
      if (ing.containsItem(stack.getItem())) {
        return ing.count();
      }
    }
    return 0;
  }

  public int inputLimit(FluidStack stack) {
    for (RecipeEntry ing : inputs) {
      if (ing.containsFluid(stack.getFluid())) {
        return ing.count();
      }
    }
    return 0;
  }

  public List<ItemStack> generateItems() {
    List<ItemStack> result = new ArrayList<>();
    for (RecipeEntry ing : outputs) {
      if (ing.isItem()) {
        result.add(ing.genItem());
      }
    }
    return result;
  }

  public List<FluidStack> generateFluids() {
    List<FluidStack> result = new ArrayList<>();
    for (RecipeEntry ing : outputs) {
      if (ing.isFluid()) {
        result.add(ing.genFluid());
      }
    }
    return result;
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
    for (RecipeEntry ing : outputs) {
      if (ing.isItem()) {
        return ing.symbolItem();
      }
    }
    return ItemStack.EMPTY;
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    NonNullList<Ingredient> list = NonNullList.create();
    for (RecipeEntry ing : inputs) {
      if (ing.isItem() && !ing.allowAll()) {
        list.add(ing.isTag() ? Ingredient.of(TagHelper.keyItem(ing.id().toString())) : Ingredient.of(ing.itemStacks().stream()));
      }
    }
    return list;
  }

  @Override
  public boolean isSpecial() {
    return true;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return serializer;
  }

  @Override
  public RecipeType<?> getType() {
    return recipeType;
  }

  public ResourceLocation getId() {
    return id;
  }
}
