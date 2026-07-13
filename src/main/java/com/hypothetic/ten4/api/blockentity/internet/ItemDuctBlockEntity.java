package com.hypothetic.ten4.api.blockentity.internet;

import com.hypothetic.ten4.api.ILootProvider;
import com.hypothetic.ten4.api.ITickable;
import com.hypothetic.ten4.api.transmission.ConnectionType;
import com.hypothetic.ten4.api.transmission.ITransmitterProvider;
import com.hypothetic.ten4.api.transmission.Transmitter;
import com.hypothetic.ten4.api.transmission.TransmitterNetworkRegistry;
import com.hypothetic.ten4.api.transmission.item.ItemTransmitter;
import com.hypothetic.ten4.api.transmission.item.ItemTransmitter.TransitEntry;
import com.hypothetic.ten4.api.network.duct.DuctConnectionPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class ItemDuctBlockEntity extends BlockEntity implements ITransmitterProvider, ILootProvider, ITickable {
  public static final int SLOTS_COPPER = 1;
  public static final int CAP_COPPER = 1;
  public static final int TPB_COPPER = 20;

  public final ItemTransmitter transmitter;

  public ItemDuctBlockEntity(BlockPos pos, BlockState state, BlockEntityType<?> type, int ticksPerBlock, int slots, int slotCapacity) {
    super(type, pos, state);
    this.transmitter = new ItemTransmitter(this, ticksPerBlock, slots, slotCapacity);
  }

  @Override
  public void tick() {
    if (level == null) return;
    if (level.isClientSide()) {
      transmitter.onUpdateClient(level);
    }
    // Server: no periodic transit sync — client runs autonomously
  }

  @Override
  public boolean isInvalid() {
    return isRemoved();
  }

  @Override
  public boolean isLoaded() {
    return level != null;
  }

  @Override
  public void sendUpdatePacket() {
    if (level instanceof ServerLevel sl) {
      setChanged();
      var t = transmitter;
      ConnectionType[] types = new ConnectionType[6];
      for (int i = 0; i < 6; i++) {
        types[i] = t.getConnectionTypeRaw(Direction.values()[i]);
      }
      PacketDistributor.sendToPlayersTrackingChunk(sl, sl.getChunkAt(worldPosition).getPos(),
          new DuctConnectionPayload(worldPosition, t.getTransmitterConnections(), t.getAcceptorConnections(), types, t.getColor()));
    }
  }

  @Override
  public void notifyTileChange() {
    if (level != null) {
      setChanged();
      level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
    }
  }

  @Override
  public Transmitter<?, ?, ?> getTransmitter() {
    return transmitter;
  }

  @Override
  public void getLoot(List<ItemStack> loot) {
    for (TransitEntry e : transmitter.getTransit()) {
      if (!e.stack.isEmpty()) {
        loot.add(e.stack.copy());
      }
    }
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.loadAdditional(tag, reg);
    transmitter.read(reg, tag.getCompound("Transmitter"));

    if (tag.contains("Transit", Tag.TAG_LIST)) {
      for (Tag t : tag.getList("Transit", Tag.TAG_COMPOUND)) {
        CompoundTag c = (CompoundTag) t;
        TransitEntry e = new TransitEntry();
        e.id = c.getInt("Id");
        e.stack = ItemStack.parseOptional(reg, c.getCompound("I"));
        e.progress = c.getInt("P");
        e.direction = c.getByte("D");
        if (c.contains("R")) {
          e.route = c.getByteArray("R");
        }
        e.routeIdx = c.getInt("Ri");
        transmitter.getTransitMap().put(c.getInt("Id"), e);
      }
    }
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.saveAdditional(tag, reg);
    tag.put("Transmitter", transmitter.write(reg, new CompoundTag()));

    if (!transmitter.getTransitMap().isEmpty()) {
      ListTag list = new ListTag();
      for (var e : transmitter.getTransitMap().entrySet()) {
        CompoundTag c = new CompoundTag();
        c.putInt("Id", e.getKey());
        c.put("I", e.getValue().stack.save(reg, new CompoundTag()));
        c.putInt("P", e.getValue().progress);
        c.putByte("D", e.getValue().direction);
        if (e.getValue().route != null) {
          c.putByteArray("R", e.getValue().route);
        }
        c.putInt("Ri", e.getValue().routeIdx);
        list.add(c);
      }
      tag.put("Transit", list);
    }
  }

  @Override
  public BlockPos getBlockPos() {
    return worldPosition;
  }

  @Override
  public CompoundTag getUpdateTag(HolderLookup.Provider reg) {
    return transmitter.getReducedUpdateTag(reg, saveWithoutMetadata(reg));
  }

  @Override
  public void setRemoved() {
    super.setRemoved();
    if (level != null && !level.isClientSide()) {
      transmitter.remove();
      TransmitterNetworkRegistry.onTransmitterRemoved(transmitter);
    }
  }

  @Override
  public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider reg) {
    super.handleUpdateTag(tag, reg);
    transmitter.handleUpdateTag(tag, reg);
  }

  @Override
  public void onChunkUnloaded() {
    super.onChunkUnloaded();
    if (level != null && !level.isClientSide()) {
      transmitter.validateAndTakeShare();
      TransmitterNetworkRegistry.onTransmitterRemoved(transmitter);
    }
  }

  @Override
  public void onLoad() {
    super.onLoad();
    if (level != null && !level.isClientSide()) {
      transmitter.refreshConnections();
      TransmitterNetworkRegistry.joinNetwork(transmitter);
    }
  }
}
