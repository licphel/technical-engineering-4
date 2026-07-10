package com.hypothetic.ten4.lib.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class ComponentHelper {
  public static ChatFormatting GOLD = ChatFormatting.GOLD;
  public static ChatFormatting GREEN = ChatFormatting.GREEN;
  public static ChatFormatting RED = ChatFormatting.RED;

  private ComponentHelper() {}

  public static MutableComponent translated(String... ss) {
    if (ss.length == 0) {
      return Component.empty();
    }

    MutableComponent t1 = null;
    for (String s : ss) {
      if (t1 == null) {
        t1 = Component.translatable(s);
      } else {
        t1.append(Component.translatable(s));
      }
    }
    return t1;
  }

  public static MutableComponent make(String... ss) {
    if (ss.length == 0) {
      return Component.empty();
    }

    MutableComponent t1 = null;
    for (String s : ss) {
      if (t1 == null) {
        t1 = Component.literal(s);
      } else {
        t1.append(Component.literal(s));
      }
    }
    return t1;
  }

  public static MutableComponent translated(ChatFormatting color, String... ss) {
    return translated(ss).withStyle(color);
  }

  public static MutableComponent make(ChatFormatting color, String... ss) {
    return make(ss).withStyle(color);
  }
}
