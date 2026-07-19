package com.hypothetic.ten4.util;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ItemStackUtil {
  private static final RandomSource RANDOM = RandomSource.create();

  private ItemStackUtil() {
  }

  public static ItemStack shrinkWithRemainder(ItemStack stack, int consumed) {
    if (stack.hasCraftingRemainingItem()) {
      return stack.getCraftingRemainingItem().copy();
    } else if (!stack.isEmpty()) {
      Item item = stack.getItem();
      stack.shrink(consumed);

      if (stack.isEmpty()) {
        return stack.getCraftingRemainingItem().copy();
      }
    }

    return stack.copy();
  }
}
