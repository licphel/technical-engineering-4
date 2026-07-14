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

  void notifyChanges();

  Transmitter<?, ?, ?> getTransmitter();
}
