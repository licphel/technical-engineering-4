package com.hypothetic.ten4.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;

public final class Util {
  public static final Runnable RUN_NOTHING = () -> {
  };
  static RandomSource RANDOM = RandomSource.create();

  private Util() {
  }

  public static String regNameOf(Block block) {
    return Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)).getPath();
  }

  public static String regNameOf(Item item) {
    return Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)).getPath();
  }

  @SuppressWarnings("unchecked")
  public static <T> @Nullable T randomInCollection(Collection<T> col) {
    if (col.isEmpty()) {
      return null;
    }
    Object[] items = col.toArray();
    return (T) net.minecraft.Util.getRandom(items, RANDOM);
  }

  public static ItemStack shrinkWithRemainder(ItemStack stack) {
    if (stack.hasCraftingRemainingItem()) {
      return stack.getCraftingRemainingItem().copyWithCount(1);
    } else if (!stack.isEmpty()) {
      Item item = stack.getItem();
      stack.shrink(1);

      if (stack.isEmpty()) {
        return stack.getCraftingRemainingItem().copyWithCount(1);
      }
    }

    return stack.copy();
  }

  public static float lerp(float x, float y, float pt) {
    return x + (y - x) * pt;
  }
}
