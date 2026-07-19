package com.hypothetic.ten4.api.blockentity.duct;

import com.hypothetic.ten4.api.blockentity.ITickable;
import com.hypothetic.ten4.core.client.renderer.RenderTransmitterBlock;
import com.hypothetic.ten4.api.network.PacketDist;
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
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class FluidDuctBlockEntity extends DuctBlockEntity<FluidTransmitter> implements ITickable {
  private final Map<Direction, TransmitterFluidHandler> fluidHandlers = new EnumMap<>(Direction.class);

  public FluidDuctBlockEntity(BlockPos pos, BlockState state, DuctInfo info) {
    super(pos, state, info);
    this.transmitter = new FluidTransmitter(this, info.bufferCapacity, info.throughput);
    for (Direction dir : Direction.values()) {
      fluidHandlers.put(dir, new TransmitterFluidHandler(transmitter, dir));
    }
  }

  @Override
  public void tick() {
    if (level == null || level.isClientSide()) {
      return;
    }

    if (level.getGameTime() % 5 == 0) {
      syncToClient();
    }
  }

  public @Nullable IFluidHandler getFluidHandler(@Nullable Direction side) {
    return side != null ? fluidHandlers.get(side) : null;
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
    FluidNetwork net = transmitter.getNetwork();
    if (net != null) {
      FluidStack buf = net.getFluid();
      if (!buf.isEmpty()) {
        int share = Math.max(1, buf.getAmount() / net.size());
        tag.put("Buffer", buf.copyWithAmount(share).save(reg, new CompoundTag()));
      }
    } else if (!transmitter.getBuffer().isEmpty()) {
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
      PacketDist.sendToNearbyPlayers(sl, new DuctFluidPayload(worldPosition, scale, fluid), getBlockPos(),
          RenderTransmitterBlock.LOD_DISTANCE.getAsInt());
    }
  }
}
