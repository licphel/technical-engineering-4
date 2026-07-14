package com.hypothetic.ten4.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public final class DataComponentUtil {
  private DataComponentUtil() {
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
