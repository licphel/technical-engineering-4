package com.hypothetic.ten4.api.container.sync;

import com.hypothetic.ten4.api.capability.fluid.FluidTank;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.fluids.FluidStack;

public final class SyncedFluidStack {
  private final SyncedField<Integer> amount;
  private final SyncedField<Integer> fluidId;
  private final SyncedField<Integer> capacity;

  public SyncedFluidStack(int tank) {
    this.amount = SyncedField.ofInt("tank_amount_" + tank);
    this.fluidId = SyncedField.ofInt("tank_fluid_id_" + tank);
    this.capacity = SyncedField.ofInt("tank_capacity_" + tank);
  }

  public void register(Syncer syncer) {
    syncer.register(amount);
    syncer.register(fluidId);
    syncer.register(capacity);
  }

  public void sync(Syncer syncer, FluidTank tank) {
    syncer.set(amount, tank.getFluidAmount());
    syncer.set(fluidId, BuiltInRegistries.FLUID.getId(tank.getFluid().getFluid()));
    syncer.set(capacity, tank.getCapacity());
  }

  public FluidStack decode(SyncedFieldReader reader) {
    return new FluidStack(BuiltInRegistries.FLUID.byId(reader.getInt(fluidId)), reader.getInt(amount));
  }

  public SyncedField<Integer> getCapacityField() {
    return capacity;
  }
}
