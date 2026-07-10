package com.hypothetic.ten4.lib.capability.energy;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class DirectionalEnergyStorage implements IEnergyStorage {
  private final IDirectionalEnergyProvider host;
  private final @Nullable Direction side;

  public DirectionalEnergyStorage(IDirectionalEnergyProvider host, @Nullable Direction side) {
    this.host = host;
    this.side = side;
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    if (!canReceive()) {
      return 0;
    }
    int v = Math.min(host.getMaxEnergy() - host.getEnergy(), Math.min(maxReceive, host.getMaxEnergyReceive(side)));
    if (v <= 0) {
      return 0;
    }
    if (!simulate) {
      host.setEnergy(host.getEnergy() + v);
    }
    return v;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    if (!canExtract()) {
      return 0;
    }
    int v = Math.min(host.getEnergy(), Math.min(maxExtract, host.getMaxEnergyExtract(side)));
    if (v <= 0) {
      return 0;
    }
    if (!simulate) {
      host.setEnergy(host.getEnergy() - v);
    }
    return v;
  }

  @Override
  public int getEnergyStored() {
    return host.getEnergy();
  }

  @Override
  public int getMaxEnergyStored() {
    return host.getMaxEnergy();
  }

  @Override
  public boolean canExtract() {
    return host.canExtractEnergy(side);
  }

  @Override
  public boolean canReceive() {
    return host.canReceiveEnergy(side);
  }
}
