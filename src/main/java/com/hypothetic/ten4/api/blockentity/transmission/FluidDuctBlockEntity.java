package com.hypothetic.ten4.api.blockentity.transmission;

import com.hypothetic.ten4.api.blockentity.ITickable;
import com.hypothetic.ten4.api.network.duct.DuctFluidPayload;
import com.hypothetic.ten4.api.transmission.fluid.FluidNetwork;
import com.hypothetic.ten4.api.transmission.fluid.FluidTransmitter;
import com.hypothetic.ten4.api.transmission.fluid.TransmitterFluidHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public abstract class FluidDuctBlockEntity extends DuctBlockEntity<FluidTransmitter> implements ITickable {
  private final IFluidHandler fluidHandler;
  private int tickCount;

  public FluidDuctBlockEntity(BlockPos pos, BlockState state) {
    super(pos, state);
    this.transmitter = new FluidTransmitter(this, info.bufferCapacity, info.throughput);
    this.fluidHandler = new TransmitterFluidHandler(transmitter);
  }

  @Override
  public void tick() {
    if (level == null || level.isClientSide()) {
      return;
    }
    if (tickCount++ % 5 == 0) {
      syncToClient();
    }
  }

  public @Nullable IFluidHandler getFluidHandler(@Nullable Direction side) {
    return fluidHandler;
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.loadAdditional(tag, reg);

    if (tag.contains("Buffer")) {
      transmitter.setBuffer(FluidStack.parseOptional(reg, tag.getCompound("Buffer")));
    }
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.saveAdditional(tag, reg);

    if (!transmitter.getBuffer().isEmpty()) {
      tag.put("Buffer", transmitter.getBuffer().save(reg, new CompoundTag()));
    }
  }

  @Override
  protected void syncToClient() {
    if (level instanceof ServerLevel sl) {
      FluidNetwork net = transmitter.getNetwork();
      float scale = net != null ? net.currentScale
          : (transmitter.getCapacity() > 0 ? (float) transmitter.getBufferAmount() / transmitter.getCapacity() : 0);
      FluidStack fluid = net != null ? net.getFluid() : transmitter.getBuffer();
      PacketDistributor.sendToPlayersTrackingChunk(sl, sl.getChunkAt(worldPosition).getPos(),
          new DuctFluidPayload(worldPosition, scale, fluid));
    }
  }
}
