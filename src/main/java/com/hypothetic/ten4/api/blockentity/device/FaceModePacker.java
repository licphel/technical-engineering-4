package com.hypothetic.ten4.api.blockentity.device;

import net.minecraft.core.Direction;

import java.util.Map;

public final class FaceModePacker {
  private static final Direction[] ORDER = {Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
  private FaceModePacker() {
  }  private static final int BITS = 3, MASK = (1 << BITS) - 1;

  public static int shift(Direction d) {
    return switch (d) {
      case DOWN -> 0;
      case UP -> BITS;
      case NORTH -> BITS * 2;
      case SOUTH -> BITS * 3;
      case WEST -> BITS * 4;
      case EAST -> BITS * 5;
    };
  }

  public static FaceMode get(int packed, Direction d) {
    return FaceMode.of((packed >> shift(d)) & MASK);
  }

  public static int set(int packed, Direction d, FaceMode mode) {
    int s = shift(d);
    return (packed & ~(MASK << s)) | (mode.ordinal() << s);
  }

  public static int cycle(int packed, Direction d) {
    FaceMode cur = get(packed, d);
    FaceMode next = FaceMode.of((cur.ordinal() + 1) % FaceMode.values().length);
    return set(packed, d, next);
  }

  static int packFaces(Map<Direction, FaceMode> config) {
    int packed = 0;
    for (Direction d : Direction.values()) {
      packed = set(packed, d, config.getOrDefault(d, FaceMode.PASSIVE_BIPASS));
    }
    return packed;
  }
}
