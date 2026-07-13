package com.hypothetic.ten4.api.transmission;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface ITransmitterProvider {
  BlockPos getBlockPos();

  @Nullable Level getLevel();

  boolean isInvalid();

  boolean isLoaded();

  void sendUpdatePacket();

  void setChanged();

  void notifyTileChange();

  /** Force immediate transit-data sync after items move. */
  default void sendTransitSync() {}

  /** Force immediate fluid-data sync after buffer changes. */
  default void sendFluidSync() {}

  @Nullable Transmitter<?, ?, ?> getTransmitter();
}
