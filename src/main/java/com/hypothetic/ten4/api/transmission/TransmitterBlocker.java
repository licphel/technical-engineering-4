package com.hypothetic.ten4.api.transmission;

import net.minecraft.core.Direction;

public interface TransmitterBlocker {
  TransmitterBlocker OPEN = side -> false;
  TransmitterBlocker CLOSED = side -> true;

  boolean isBlocked(Direction side);
}
