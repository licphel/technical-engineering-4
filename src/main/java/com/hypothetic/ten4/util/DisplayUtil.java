package com.hypothetic.ten4.util;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.StringJoiner;

public final class DisplayUtil {
  private DisplayUtil() {
  }

  public static String compactInt(int value) {
    return compactInt(value, "%,d");
  }

  public static String compactInt(int value, String fmt) {
    return String.format(fmt, value);
  }

  public static Component forgeEnergy(int v, int mv) {
    return Component.literal(compactInt(v) + "FE / " + compactInt(mv) + "FE");
  }

  public static Component milliBucket(int v, int mv) {
    String v1 = compactInt(v);
    String v2 = compactInt(mv);

    /* Handle with milli-bucket and Bucket. commented out for convention
    if (v >= 1_000) {
      v1 = v1.substring(0, v1.length() - 1) + "B";
    } else {
      v1 = v1 + "mB";
    }
    if (mv >= 1_000) {
      v2 = v2.substring(0, v2.length() - 1) + "B";
    } else {
      v2 = v2 + "mB";
    }
    return Component.literal(v1 + " / " + v2);
     */

    return Component.literal(compactInt(v) + "mB / " + compactInt(mv) + "mB");
  }

  public static String blockPos(BlockPos b) {
    StringJoiner joiner = new StringJoiner(", ", "(", ")");
    b = b.offset(0, 0, 1);
    joiner.add(String.valueOf(b.getX()));
    joiner.add(String.valueOf(b.getY()));
    joiner.add(String.valueOf(b.getZ()));
    return joiner.toString();
  }
}
