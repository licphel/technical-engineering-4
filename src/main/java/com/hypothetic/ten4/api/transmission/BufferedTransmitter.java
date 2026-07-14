package com.hypothetic.ten4.api.transmission;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class BufferedTransmitter<AC, NET extends BufferedNetwork<AC, NET, BUF, T>, BUF, T extends BufferedTransmitter<AC, NET, BUF, T>>
    extends Transmitter<AC, NET, T> {
  public long bufferCapacity;
  public long throughput;

  protected BufferedTransmitter(ITransmitterProvider tile, long bufferCapacity, long throughput) {
    super(tile);
    this.bufferCapacity = bufferCapacity;
    this.throughput = throughput;
  }

  public long getCapacity() {
    return bufferCapacity;
  }

  public long getThroughput() {
    return throughput;
  }

  public abstract BUF releaseShare();

  @Nullable
  public abstract AC getAcceptor(Direction side, Level level, BlockPos targetPos);
}
