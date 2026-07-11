package com.hypothetic.ten4.lib.recipe;

import com.google.gson.JsonObject;
import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.lib.util.TagHelper;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Collection;
import java.util.List;

public class RecipeEntry {
  public static final Codec<RecipeEntry> CODEC = new Codec<>() {
    @Override
    public <T> DataResult<Pair<RecipeEntry, T>> decode(DynamicOps<T> ops, T input) {
      try {
        com.google.gson.JsonElement el = ops.convertTo(JsonOps.INSTANCE, input);
        if (!el.isJsonObject()) {
          return DataResult.error(() -> "Not a JSON object: " + el);
        }
        return DataResult.success(Pair.of(fromJson(el.getAsJsonObject()), ops.empty()));
      } catch (Exception e) {
        return DataResult.error(() -> "Failed to decode RecipeEntry: " + e.getMessage());
      }
    }

    @Override
    public <T> DataResult<T> encode(RecipeEntry input, DynamicOps<T> ops, T prefix) {
      com.google.gson.JsonObject obj = new com.google.gson.JsonObject();
      switch (input.kind) {
        case ITEM -> {
          obj.addProperty("item", input.id.toString());
          if (input.count != 1) {
            obj.addProperty("count", input.count);
          }
        }
        case ITEM_TAG -> {
          obj.addProperty("item_tag", input.id.toString());
          if (input.count != 1) {
            obj.addProperty("count", input.count);
          }
        }
        case FLUID -> {
          obj.addProperty("fluid", input.id.toString());
          if (input.count != 0) {
            obj.addProperty("amount", input.count);
          }
        }
        case FLUID_TAG -> {
          obj.addProperty("fluid_tag", input.id.toString());
          if (input.count != 0) {
            obj.addProperty("amount", input.count);
          }
        }
      }
      if (input.chance != 1.0) {
        obj.addProperty("chance", input.chance);
      }
      return DataResult.success(JsonOps.INSTANCE.convertTo(ops, obj));
    }
  };

  private final Kind kind;
  private final ResourceLocation id;
  private final int count;
  private final double chance;
  private boolean isEmpty;
  private Collection<Item> matchItems;
  private TagKey<Item> itemTagKey;
  private Collection<Fluid> matchFluids;
  private TagKey<Fluid> fluidTagKey;
  private boolean resolved;
  private boolean additional;

  private RecipeEntry(Kind kind, ResourceLocation id, int count, double chance) {
    this.kind = kind;
    this.id = id;
    this.count = count;
    this.chance = chance;
  }

  public static RecipeEntry empty() {
    RecipeEntry r = new RecipeEntry(Kind.ITEM, Ten4.vanillaId("air"), 0, 1);
    r.isEmpty = true;
    return r;
  }

  public static RecipeEntry of(Kind kind, ResourceLocation id, int count, double chance) {
    RecipeEntry r = new RecipeEntry(kind, id, count, chance);
    r.resolve();
    return r;
  }

  public static RecipeEntry fromJson(JsonObject json) {
    if (json.has("item")) {
      ResourceLocation rl = ResourceLocation.parse(json.get("item").getAsString());
      int c = JsonParser.getIntOr(json, "count", 1);
      double ch = JsonParser.getFloatOr(json, "chance", 1.0F);
      return of(Kind.ITEM, rl, c, ch);
    }
    if (json.has("item_tag")) {
      ResourceLocation rl = ResourceLocation.parse(json.get("item_tag").getAsString());
      int c = JsonParser.getIntOr(json, "count", 1);
      double ch = JsonParser.getFloatOr(json, "chance", 1.0F);
      return of(Kind.ITEM_TAG, rl, c, ch);
    }
    if (json.has("fluid")) {
      ResourceLocation rl = ResourceLocation.parse(json.get("fluid").getAsString());
      int amt = JsonParser.getIntOr(json, "amount", 0);
      double ch = JsonParser.getFloatOr(json, "chance", 1.0F);
      return of(Kind.FLUID, rl, amt, ch);
    }
    if (json.has("fluid_tag")) {
      ResourceLocation rl = ResourceLocation.parse(json.get("fluid_tag").getAsString());
      int amt = JsonParser.getIntOr(json, "amount", 0);
      double ch = JsonParser.getFloatOr(json, "chance", 1.0F);
      return of(Kind.FLUID_TAG, rl, amt, ch);
    }
    return empty();
  }

  public static RecipeEntry fromNetwork(RegistryFriendlyByteBuf buf) {
    Kind kind = buf.readEnum(Kind.class);
    ResourceLocation id = buf.readResourceLocation();
    int count = buf.readInt();
    double chance = buf.readDouble();
    return of(kind, id, count, chance);
  }

  private static Item parseItem(String s) {
    return BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(s)).orElse(Items.AIR);
  }

  private static Fluid parseFluid(String s) {
    return BuiltInRegistries.FLUID.getOptional(ResourceLocation.parse(s)).orElse(Fluids.EMPTY);
  }

  public Kind kind() {
    return kind;
  }

  public ResourceLocation id() {
    return id;
  }

  public int count() {
    return count;
  }

  public double chance() {
    return chance;
  }

  public boolean allowAll() {
    return isEmpty;
  }

  public boolean isItem() {
    return kind.isItem();
  }

  public boolean isFluid() {
    return kind.isFluid();
  }

  public boolean isTag() {
    return kind.isTag();
  }

  public boolean isAdditional() {
    return additional;
  }

  private void resolve() {
    if (resolved || isEmpty) {
      return;
    }
    resolved = true;
    switch (kind) {
      case ITEM -> matchItems = List.of(parseItem(id.toString()));
      case ITEM_TAG -> {
        itemTagKey = TagHelper.keyItem(id.toString());
        matchItems = TagHelper.getItems(itemTagKey);
      }
      case FLUID -> matchFluids = List.of(parseFluid(id.toString()));
      case FLUID_TAG -> {
        fluidTagKey = TagHelper.keyFluid(id.toString());
        matchFluids = TagHelper.getFluids(fluidTagKey);
      }
    }
  }

  private void ensureResolved() {
    if (!resolved && !isEmpty) {
      resolve();
    }
  }

  public boolean test(ItemStack stack) {
    if (isEmpty) {
      return true;
    }
    if (!kind.isItem() || stack.isEmpty()) {
      return false;
    }
    if (stack.getCount() < count) {
      return false;
    }
    return containsItem(stack.getItem());
  }

  public boolean test(FluidStack stack) {
    if (isEmpty) {
      return true;
    }
    if (!kind.isFluid() || stack.isEmpty()) {
      return false;
    }
    if (stack.getAmount() < count) {
      return false;
    }
    return containsFluid(stack.getFluid());
  }

  public boolean containsItem(Item item) {
    ensureResolved();
    if (matchItems == null) {
      return false;
    }
    if (kind == Kind.ITEM_TAG && itemTagKey != null) {
      return TagHelper.containsItem(item, itemTagKey);
    }
    return matchItems.contains(item);
  }

  public boolean containsFluid(Fluid fluid) {
    ensureResolved();
    if (matchFluids == null) {
      return false;
    }
    if (kind == Kind.FLUID_TAG && fluidTagKey != null) {
      return TagHelper.containsFluid(fluid, fluidTagKey);
    }
    return matchFluids.contains(fluid);
  }

  public ItemStack genItem() {
    return Math.random() < chance ? symbolItem() : ItemStack.EMPTY;
  }

  public ItemStack symbolItem() {
    ensureResolved();
    if (!kind.isItem() || matchItems == null || matchItems.isEmpty()) {
      return ItemStack.EMPTY;
    }
    return new ItemStack(matchItems.iterator().next(), count);
  }

  public FluidStack genFluid() {
    return Math.random() < chance ? symbolFluid() : FluidStack.EMPTY;
  }

  public FluidStack symbolFluid() {
    ensureResolved();
    if (!kind.isFluid() || matchFluids == null || matchFluids.isEmpty()) {
      return FluidStack.EMPTY;
    }
    return new FluidStack(matchFluids.iterator().next(), count);
  }

  public List<ItemStack> itemStacks() {
    if (isEmpty) {
      return List.of(ItemStack.EMPTY);
    }
    ensureResolved();
    if (matchItems == null) {
      return List.of();
    }
    return matchItems.stream().map(i -> new ItemStack(i, count)).toList();
  }

  public List<FluidStack> fluidStacks() {
    if (isEmpty) {
      return List.of(FluidStack.EMPTY);
    }
    ensureResolved();
    if (matchFluids == null) {
      return List.of();
    }
    return matchFluids.stream().map(f -> new FluidStack(f, count)).toList();
  }

  public void writeTo(RegistryFriendlyByteBuf buf) {
    buf.writeEnum(kind);
    buf.writeResourceLocation(id);
    buf.writeInt(count);
    buf.writeDouble(chance);
  }

  @Override
  public String toString() {
    if (isEmpty) {
      return "Ingredient[ANY]";
    }
    return "Ingredient[" + kind + " " + id + " x" + count + (chance != 1.0 ? " @" + chance : "") + "]";
  }

  public enum Kind {
    ITEM,
    ITEM_TAG,
    FLUID,
    FLUID_TAG;

    public boolean isItem() {
      return this == ITEM || this == ITEM_TAG;
    }

    public boolean isFluid() {
      return this == FLUID || this == FLUID_TAG;
    }

    public boolean isTag() {
      return this == ITEM_TAG || this == FLUID_TAG;
    }
  }
}
