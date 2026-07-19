package com.hypothetic.ten4.api.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hypothetic.ten4.util.JsonUtil;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class Complex {
  public static Complex EMPTY = of(Kind.ITEM, ResourceLocation.parse("minecraft:air"), 0, 0, true);
  public static final Codec<Complex> CODEC = new Codec<>() {
    @Override
    public <T> DataResult<Pair<Complex, T>> decode(DynamicOps<T> ops, T input) {
      try {
        JsonElement el = ops.convertTo(JsonOps.INSTANCE, input);
        if (!el.isJsonObject()) {
          return DataResult.error(() -> "Not a JSON object: " + el);
        }
        return DataResult.success(Pair.of(fromJson(el.getAsJsonObject()), ops.empty()));
      } catch (Exception e) {
        return DataResult.error(() -> "Failed to decode Complex: " + e.getMessage());
      }
    }

    @Override
    public <T> DataResult<T> encode(Complex input, DynamicOps<T> ops, T prefix) {
      JsonObject obj = new JsonObject();
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
  private List<Item> itemTagContents = new ArrayList<>();
  private @Nullable TagKey<Item> itemTagKey;
  private List<Fluid> fluidTagContents = new ArrayList<>();
  private @Nullable TagKey<Fluid> fluidTagKey;
  private final boolean isCatalyst;

  private Complex(Kind kind, ResourceLocation id, int count, double chance, boolean isCatalyst) {
    this.kind = kind;
    this.id = id;
    this.count = count;
    this.chance = chance;
    this.isCatalyst = isCatalyst;

    switch (kind) {
      case ITEM -> itemTagContents = List.of(BuiltInRegistries.ITEM.get(id));
      case ITEM_TAG -> {
        itemTagKey = TagKey.create(Registries.ITEM, id);
        BuiltInRegistries.ITEM.getTagOrEmpty(itemTagKey).forEach(s -> itemTagContents.add(s.value()));
      }
      case FLUID -> fluidTagContents = List.of(BuiltInRegistries.FLUID.get(id));
      case FLUID_TAG -> {
        fluidTagKey = TagKey.create(Registries.FLUID, id);
        BuiltInRegistries.FLUID.getTagOrEmpty(fluidTagKey).forEach(s -> fluidTagContents.add(s.value()));
      }
    }
  }

  public static Complex of(Kind kind, ResourceLocation id, int count, double chance, boolean catalyst) {
    return new Complex(kind, id, count, chance, catalyst);
  }

  public static Complex of(Kind kind, ResourceLocation id, int count, double chance) {
    return new Complex(kind, id, count, chance, false);
  }

  public static Complex of(Kind kind, ResourceLocation id, int count) {
    return new Complex(kind, id, count, 1.0F, false);
  }

  public static Complex fromJson(JsonObject json) {
    int c = JsonUtil.getIntOr(json, "count", 1);
    int amt = JsonUtil.getIntOr(json, "amount", 1);
    double ch = JsonUtil.getFloatOr(json, "chance", 1.0F);
    boolean cat = JsonUtil.getBooleanOr(json, "catalyst", false);

    if (json.has("item")) {
      ResourceLocation rl = ResourceLocation.parse(json.get("item").getAsString());
      return of(Kind.ITEM, rl, c, ch, cat);
    }
    if (json.has("item_tag")) {
      ResourceLocation rl = ResourceLocation.parse(json.get("item_tag").getAsString());
      return of(Kind.ITEM_TAG, rl, c, ch, cat);
    }
    if (json.has("fluid")) {
      ResourceLocation rl = ResourceLocation.parse(json.get("fluid").getAsString());
      return of(Kind.FLUID, rl, amt, ch, cat);
    }
    if (json.has("fluid_tag")) {
      ResourceLocation rl = ResourceLocation.parse(json.get("fluid_tag").getAsString());
      return of(Kind.FLUID_TAG, rl, amt, ch, cat);
    }
    return EMPTY;
  }

  public static Complex streamingDecode(RegistryFriendlyByteBuf buf) {
    Kind kind = buf.readEnum(Kind.class);
    ResourceLocation id = buf.readResourceLocation();
    int count = buf.readInt();
    double chance = buf.readDouble();
    boolean reusable = buf.readBoolean();
    return of(kind, id, count, chance, reusable);
  }

  public void streamingEncode(RegistryFriendlyByteBuf buf) {
    buf.writeEnum(kind);
    buf.writeResourceLocation(id);
    buf.writeInt(count);
    buf.writeDouble(chance);
    buf.writeBoolean(isCatalyst);
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

  public boolean isEmpty() {
    return chance == 0 || count == 0;
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

  public boolean test(ItemStack stack) {
    if (isEmpty()) {
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
    if (isEmpty()) {
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
    if (itemTagContents == null) {
      return false;
    }
    if (kind == Kind.ITEM_TAG && itemTagKey != null) {
      return item.getDefaultInstance().is(itemTagKey);
    }
    return itemTagContents.contains(item);
  }

  public boolean containsFluid(Fluid fluid) {
    if (fluidTagContents == null) {
      return false;
    }
    if (kind == Kind.FLUID_TAG && fluidTagKey != null) {
      return fluid.defaultFluidState().is(fluidTagKey);
    }
    return fluidTagContents.contains(fluid);
  }

  public ItemStack genItem() {
    return Math.random() < chance ? symbolItem() : ItemStack.EMPTY;
  }

  public ItemStack symbolItem() {
    if (!kind.isItem() || itemTagContents.isEmpty()) {
      return ItemStack.EMPTY;
    }
    return new ItemStack(itemTagContents.getFirst(), count);
  }

  public FluidStack genFluid() {
    return Math.random() < chance ? symbolFluid() : FluidStack.EMPTY;
  }

  public FluidStack symbolFluid() {
    if (!kind.isFluid() || fluidTagContents.isEmpty()) {
      return FluidStack.EMPTY;
    }
    return new FluidStack(fluidTagContents.getFirst(), count);
  }

  public List<ItemStack> itemStacks() {
    return itemTagContents.stream().map(i -> new ItemStack(i, count)).toList();
  }

  public List<FluidStack> fluidStacks() {
    return fluidTagContents.stream().map(f -> new FluidStack(f, count)).toList();
  }

  public boolean isCatalyst() {
    return isCatalyst;
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
