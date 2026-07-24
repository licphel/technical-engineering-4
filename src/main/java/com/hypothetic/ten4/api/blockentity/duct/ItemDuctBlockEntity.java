package com.hypothetic.ten4.api.blockentity.duct;

import com.hypothetic.ten4.api.blockentity.ILootProvider;
import com.hypothetic.ten4.api.blockentity.ITickable;
import com.hypothetic.ten4.api.transmission.item.ItemTransmitter;
import com.hypothetic.ten4.api.transmission.item.ItemTransmitter.TransitEntry;
import com.hypothetic.ten4.api.transmission.item.TransmitterItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;

public class ItemDuctBlockEntity extends DuctBlockEntity<ItemTransmitter> implements ILootProvider, ITickable {
  private final EnumMap<Direction, TransmitterItemHandler> itemHandlers = new EnumMap<>(Direction.class);

  public ItemDuctBlockEntity(BlockPos pos, BlockState state, DuctInfo info) {
    super(pos, state, info);
    this.transmitter = new ItemTransmitter(this, info.throughput, info.bufferCapacity);
    for (Direction dir : Direction.values()) {
      itemHandlers.put(dir, new TransmitterItemHandler(transmitter, dir));
    }
  }

  public @Nullable IItemHandler getItemHandler(@Nullable Direction side) {
    return side != null ? itemHandlers.get(side) : null;
  }

  @Override
  public void tick() {
    if (level == null || level.isClientSide()) {
      return;
    }

    syncToClient();
  }

  @Override
  public void getLoot(List<ItemStack> loot, boolean shouldDropSelf) {
    TransitEntry e = transmitter.transitEntry;
    if (e != null && !e.stack.isEmpty()) {
      loot.add(e.stack.copy());
    }
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.loadAdditional(tag, reg);

    if (tag.contains("Transit", Tag.TAG_COMPOUND)) {
      CompoundTag c = tag.getCompound("Transit");

      TransitEntry e = new TransitEntry();
      e.id = c.getInt("Id");
      e.stack = ItemStack.parseOptional(reg, c.getCompound("I"));
      e.progress = c.getInt("P");
      e.exitSide = c.getByte("D");
      e.route = c.getByteArray("R");
      e.index = c.getInt("Ri");
      transmitter.transitEntry = e;
    }
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.saveAdditional(tag, reg);

    TransitEntry e = transmitter.transitEntry;
    if (e != null) {
      CompoundTag c = new CompoundTag();
      c.put("I", e.stack.save(reg, new CompoundTag()));
      c.putInt("P", e.progress);
      c.putByte("D", e.exitSide);
      c.putByteArray("R", e.route);
      c.putInt("Ri", e.index);
      tag.put("Transit", c);
    }
  }

  @Override
  protected void syncToClient() {
    transmitter.syncToTracking();
  }
}
