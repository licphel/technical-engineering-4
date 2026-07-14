package com.hypothetic.ten4.api.blockentity.transmission;

import com.hypothetic.ten4.api.ILootProvider;
import com.hypothetic.ten4.api.ITickable;
import com.hypothetic.ten4.api.transmission.item.TransmitterItemHandler;
import com.hypothetic.ten4.api.transmission.item.ItemTransmitter;
import com.hypothetic.ten4.api.transmission.item.ItemTransmitter.TransitEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public abstract class ItemDuctBlockEntity extends DuctBlockEntity<ItemTransmitter> implements ILootProvider, ITickable {
  private final IItemHandler itemHandler;

  public ItemDuctBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
    this.transmitter = new ItemTransmitter(this, info.throughput, info.bufferCapacity);
    this.itemHandler = new TransmitterItemHandler(transmitter);
  }

  public @Nullable IItemHandler getItemHandler(@Nullable Direction side) {
    return itemHandler;
  }

  @Override
  public void tick() {
    if (level == null) {
      return;
    }

    if (level.isClientSide()) {
      transmitter.onUpdateClient(level);
    }
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

    if (tag.contains("Transit", Tag.TAG_LIST)) {
      for (Tag t : tag.getList("Transit", Tag.TAG_COMPOUND)) {
        CompoundTag c = (CompoundTag) t;
        TransitEntry e = new TransitEntry();
        e.id = c.getInt("Id");
        e.stack = ItemStack.parseOptional(reg, c.getCompound("I"));
        e.progress = c.getInt("P");
        e.exitSide = c.getByte("D");
        if (c.contains("R")) {
          e.route = c.getByteArray("R");
        }
        e.index = c.getInt("Ri");
        transmitter.getTransitMap().put(c.getInt("Id"), e);
      }
    }
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.saveAdditional(tag, reg);

    if (!transmitter.getTransitMap().isEmpty()) {
      ListTag list = new ListTag();
      for (Map.Entry<Integer, TransitEntry> e : transmitter.getTransitMap().entrySet()) {
        CompoundTag c = new CompoundTag();
        c.putInt("Id", e.getKey());
        c.put("I", e.getValue().stack.save(reg, new CompoundTag()));
        c.putInt("P", e.getValue().progress);
        c.putByte("D", e.getValue().exitSide);
        if (e.getValue().route != null) {
          c.putByteArray("R", e.getValue().route);
        }
        c.putInt("Ri", e.getValue().index);
        list.add(c);
      }
      tag.put("Transit", list);
    }
  }

  @Override
  protected void syncToClient() {
  }
}
