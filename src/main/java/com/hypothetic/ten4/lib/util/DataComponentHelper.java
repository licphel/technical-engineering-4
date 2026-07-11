package com.hypothetic.ten4.lib.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

import java.util.function.Consumer;

public final class DataComponentHelper {
  private DataComponentHelper() {
  }

  public static ItemStack[] merge(ItemStack i1, ItemStack i2) {
    SimpleContainer inv = new SimpleContainer(2);
    InvWrapper wrapper = new InvWrapper(inv);
    inv.setItem(0, i1.copy());
    ItemStack sr = wrapper.insertItem(0, i2.copy(), false);
    if (sr.isEmpty()) {
      return new ItemStack[] {inv.getItem(0)};
    }
    return new ItemStack[] {inv.getItem(0), sr};
  }

  public static void damage(ItemStack stack, Level world, int am) {
    if (world instanceof ServerLevel serverLevel) {
      Consumer<Item> breakHandler = (s) -> {
      };
      stack.hurtAndBreak(am, serverLevel, null, breakHandler);
    }
  }

  public static int getInt(ItemStack stack, String name) {
    CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
    if (data.isEmpty()) {
      return 0;
    }
    CompoundTag tag = data.copyTag();
    if (!tag.contains(name)) {
      return 0;
    }
    return tag.getInt(name);
  }

  public static void setInt(ItemStack stack, String name, int cr) {
    CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    nbt.putInt(name, cr);
    stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
  }

  public static void increaseInt(ItemStack stack, String name, int move) {
    setInt(stack, name, getInt(stack, name) + move);
  }

  public static double getDouble(ItemStack stack, String name) {
    CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
    if (data.isEmpty()) {
      return 0;
    }
    CompoundTag tag = data.copyTag();
    if (!tag.contains(name)) {
      return 0;
    }
    return tag.getDouble(name);
  }

  public static void setDouble(ItemStack stack, String name, double cr) {
    CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    nbt.putDouble(name, cr);
    stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
  }

  public static void increaseDouble(ItemStack stack, String name, double move) {
    setDouble(stack, name, getDouble(stack, name) + move);
  }
}
