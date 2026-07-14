package com.hypothetic.ten4.util;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ItemStackUtil {
  private static final RandomSource RANDOM = RandomSource.create();

  private ItemStackUtil() {
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
}
