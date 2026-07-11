package com.hypothetic.ten4.lib.capability.internet;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/** Interface that any cable/pipe BE must implement to participate in a transmitter network. */
public interface ITransmitterTile {
  BlockPos getBlockPos();
  Level getLevel();
  boolean isInvalid();
  boolean isLoaded();
  void sendUpdatePacket();
  void setChanged();
  void notifyTileChange();
}
