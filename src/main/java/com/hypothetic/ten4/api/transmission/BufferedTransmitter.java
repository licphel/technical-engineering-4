package com.hypothetic.ten4.api.transmission;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Mekanism-style transmitter for energy/fluid — instant transfer, no per-cable progress.
 * Each transmitter holds a local buffer only when orphaned; when networked it delegates to the network.
 */
public abstract class BufferedTransmitter<AC, NET extends DynamicBufferedNetwork<AC, NET, BUF, T>, BUF, T extends BufferedTransmitter<AC, NET, BUF, T>>
    extends Transmitter<AC, NET, T> {

  protected BufferedTransmitter(ITransmitterProvider tile) { super(tile); }

  public abstract long getCapacity();

  /** Release local buffer when joining a network — network calls {@code absorbBuffer}. */
  public abstract BUF releaseShare();

  /** Get the acceptor capability at the given position. */
  @Nullable
  public abstract AC getAcceptor(Direction side, Level level, BlockPos targetPos);
}
