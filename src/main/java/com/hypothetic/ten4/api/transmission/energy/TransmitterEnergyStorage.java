package com.hypothetic.ten4.api.transmission.energy;

import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class TransmitterEnergyStorage implements IEnergyStorage {
  private final EnergyTransmitter transmitter;

  public TransmitterEnergyStorage(EnergyTransmitter transmitter) {
    this.transmitter = transmitter;
  }

  private @Nullable EnergyNetwork net() {
    return transmitter.getNetwork();
  }

  private long buf() {
    EnergyNetwork n = net();
    return n != null ? n.getBuffer() : transmitter.getBuffer();
  }

  private long cap() {
    EnergyNetwork n = net();
    return n != null ? n.getCapacity() : transmitter.getCapacity();
  }

  @Override
  public int receiveEnergy(int max, boolean sim) {
    max = (int) Math.min(max, transmitter.getThroughput());
    long space = cap() - buf();
    int toAdd = (int) Math.min(Math.min(space, max), Integer.MAX_VALUE);
    if (toAdd > 0 && !sim) {
      EnergyNetwork n = net();
      if (n != null) {
        n.setBuffer(n.getBuffer() + toAdd);
      } else {
        transmitter.setBuffer(transmitter.getBuffer() + toAdd);
      }
    }
    return toAdd;
  }

  @Override
  public int extractEnergy(int max, boolean sim) {
    max = (int) Math.min(max, transmitter.getThroughput());
    long cur = buf();
    int toExtract = (int) Math.min(Math.min(cur, max), Integer.MAX_VALUE);
    if (toExtract > 0 && !sim) {
      EnergyNetwork n = net();
      if (n != null) {
        n.setBuffer(n.getBuffer() - toExtract);
      } else {
        transmitter.setBuffer(transmitter.getBuffer() - toExtract);
      }
    }
    return toExtract;
  }

  @Override
  public int getEnergyStored() {
    return (int) Math.min(buf(), Integer.MAX_VALUE);
  }

  @Override
  public int getMaxEnergyStored() {
    return (int) Math.min(cap(), Integer.MAX_VALUE);
  }

  @Override
  public boolean canExtract() {
    return buf() > 0;
  }

  @Override
  public boolean canReceive() {
    return buf() < cap();
  }
}
