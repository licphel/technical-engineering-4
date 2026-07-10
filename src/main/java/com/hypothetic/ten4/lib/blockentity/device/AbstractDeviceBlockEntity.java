package com.hypothetic.ten4.lib.blockentity.device;

import com.hypothetic.ten4.lib.block.BuiltinBlockStates;
import com.hypothetic.ten4.lib.blockentity.FaceMode;
import com.hypothetic.ten4.lib.blockentity.IDropContent;
import com.hypothetic.ten4.lib.blockentity.SignalMode;
import com.hypothetic.ten4.lib.capability.energy.DirectionalEnergyStorage;
import com.hypothetic.ten4.lib.capability.energy.EnergyQueue;
import com.hypothetic.ten4.lib.capability.energy.IDirectionalEnergyProvider;
import com.hypothetic.ten4.lib.capability.fluid.DirectionalFluidHandler;
import com.hypothetic.ten4.lib.capability.fluid.FluidQueue;
import com.hypothetic.ten4.lib.capability.fluid.FluidTanks;
import com.hypothetic.ten4.lib.capability.fluid.IDirectionalFluidProvider;
import com.hypothetic.ten4.lib.capability.item.DirectionalItemHandler;
import com.hypothetic.ten4.lib.capability.item.IDirectionalItemProvider;
import com.hypothetic.ten4.lib.capability.item.ItemInventory;
import com.hypothetic.ten4.lib.capability.item.ItemQueue;
import com.hypothetic.ten4.lib.container.sync.Syncer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class AbstractDeviceBlockEntity extends BlockEntity
    implements IDropContent, IDirectionalEnergyProvider, IDirectionalFluidProvider, IDirectionalItemProvider, MenuProvider {
  public static final int BASE_ENERGY_CAPACITY = 10_000;
  public static final int BASE_FLUID_CAPACITY = 10_000;
  public static final int BASE_ITEM_TRANSPORTATION = 1;
  public static final int BASE_ENERGY_TRANSPORTATION = 1_000;
  public static final int BASE_FLUID_TRANSPORTATION = 1_000;
  public static final int AUGMENT_CAPACITY = 4;

  public static final ICapabilityProvider<AbstractDeviceBlockEntity, @Nullable Direction, IEnergyStorage> ENERGY =
      AbstractDeviceBlockEntity::getEnergyStorage;
  public static final ICapabilityProvider<AbstractDeviceBlockEntity, @Nullable Direction, IItemHandler> ITEM =
      AbstractDeviceBlockEntity::getItemHandler;
  public static final ICapabilityProvider<AbstractDeviceBlockEntity, @Nullable Direction, IFluidHandler> FLUID =
      AbstractDeviceBlockEntity::getFluidHandler;

  protected final Map<Direction, DirectionalEnergyStorage> energyHandlers = new HashMap<>();
  protected final Map<Direction, FaceMode> energyFaceConfig = new HashMap<>();
  protected final Queue<Direction> energyPushingQueue = new LinkedList<>();
  protected final Queue<Direction> energyPullingQueue = new LinkedList<>();
  protected final ItemInventory inventory = new ItemInventory();
  protected final Map<Direction, DirectionalItemHandler> itemHandlers = new HashMap<>();
  protected final Map<Direction, FaceMode> itemFaceConfig = new HashMap<>();
  protected final Queue<Direction> itemPushingQueue = new LinkedList<>();
  protected final Queue<Direction> itemPullingQueue = new LinkedList<>();
  protected final FluidTanks fluidTanks = new FluidTanks();
  protected final Map<Direction, DirectionalFluidHandler> fluidHandlers = new HashMap<>();
  protected final Map<Direction, FaceMode> fluidFaceConfig = new HashMap<>();
  protected final Queue<Direction> fluidPushingQueue = new LinkedList<>();
  protected final Queue<Direction> fluidPullingQueue = new LinkedList<>();
  protected final Syncer syncer = new Syncer();
  protected int efficiency = getBasicEfficiency();
  protected int energyStored = 0;
  protected int energyCapacity = getEnergyCapacity();
  protected int maxExtractEnergy = BASE_ENERGY_TRANSPORTATION;
  protected int maxReceiveEnergy = BASE_ENERGY_TRANSPORTATION;
  protected int itemMaxExtract = BASE_ITEM_TRANSPORTATION;
  protected int itemMaxReceive = BASE_ITEM_TRANSPORTATION;
  protected int fluidMaxExtract = BASE_FLUID_TRANSPORTATION;
  protected int fluidMaxReceive = BASE_FLUID_TRANSPORTATION;
  protected boolean active;
  protected SignalMode sigMode = SignalMode.IGNORE;

  public AbstractDeviceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  public @Nullable IEnergyStorage getEnergyStorage(@Nullable Direction side) {
    return energyHandlers.get(side);
  }

  public @Nullable IItemHandler getItemHandler(@Nullable Direction side) {
    return itemHandlers.get(side);
  }

  public @Nullable IFluidHandler getFluidHandler(@Nullable Direction side) {
    return fluidHandlers.get(side);
  }

  @Override
  public void onLoad() {
    super.onLoad();
    initializeCapabilities();
    setupStorage();
    initAttributes(syncer);
    syncer.seal();
    active = getBlockState().hasProperty(BuiltinBlockStates.ACTIVE)
        ? getBlockState().getValue(BuiltinBlockStates.ACTIVE) : false;
  }

  protected void initializeCapabilities() {
    inventory.setChangeListener(this::setChanged);
    inventory.setStillValidCheck(p -> level != null && level.getBlockEntity(worldPosition) == this);

    for (Direction d : Direction.values()) {
      energyHandlers.put(d, new DirectionalEnergyStorage(this, d));
      itemHandlers.put(d, new DirectionalItemHandler(this, d));
      fluidHandlers.put(d, new DirectionalFluidHandler(this, d));
      energyFaceConfig.put(d, FaceMode.PASSIVE_BOTH);
      itemFaceConfig.put(d, FaceMode.PASSIVE_BOTH);
      fluidFaceConfig.put(d, FaceMode.PASSIVE_BOTH);
    }
    energyHandlers.put(null, new DirectionalEnergyStorage(this, null));
    itemHandlers.put(null, new DirectionalItemHandler(this, null));
    fluidHandlers.put(null, new DirectionalFluidHandler(this, null));
    energyFaceConfig.put(null, FaceMode.PASSIVE_BOTH);
    itemFaceConfig.put(null, FaceMode.PASSIVE_BOTH);
    fluidFaceConfig.put(null, FaceMode.PASSIVE_BOTH);
  }

  protected abstract void initAttributes(Syncer syncer);

  protected abstract void setupStorage();

  public boolean isSignalEnabled() {
    if (level == null) {
      return false;
    }
    boolean hasSig = level.hasNeighborSignal(worldPosition);
    return switch (sigMode) {
      case IGNORE -> true;
      case HIGH -> hasSig;
      case LOW -> !hasSig;
    };
  }

  @Override
  public int getEnergy() {
    if (energyStored >= energyCapacity) {
      return energyStored = energyCapacity;
    }
    return energyStored;
  }

  @Override
  public void setEnergy(int e) {
    energyStored = Math.min(e, energyCapacity);
  }

  @Override
  public int getMaxEnergy() {
    return energyCapacity;
  }

  @Override
  public int getMaxEnergyExtract(@Nullable Direction d) {
    return energyFaceConfig.get(d).canExtract() ? maxExtractEnergy : 0;
  }

  @Override
  public int getMaxEnergyReceive(@Nullable Direction d) {
    return energyFaceConfig.get(d).canReceive() ? maxReceiveEnergy : 0;
  }

  @Override
  public Queue<Direction> getEnergyPushingCycle() {
    return energyPushingQueue;
  }

  @Override
  public Queue<Direction> getEnergyPullingCycle() {
    return energyPullingQueue;
  }

  @Override
  public IFluidHandler getTanks() {
    return fluidTanks;
  }

  @Override
  public int getMaxFluidExtract(@Nullable Direction d) {
    return fluidFaceConfig.get(d).canExtract() ? fluidMaxExtract : 0;
  }

  @Override
  public int getMaxFluidReceive(@Nullable Direction d) {
    return fluidFaceConfig.get(d).canReceive() ? fluidMaxReceive : 0;
  }

  @Override
  public Queue<Direction> getFluidPushingCycle() {
    return fluidPushingQueue;
  }

  @Override
  public Queue<Direction> getFluidPullingCycle() {
    return fluidPullingQueue;
  }

  @Override
  public IItemHandler getInventory() {
    return inventory;
  }

  @Override
  public Container getContainer() {
    return inventory;
  }

  @Override
  public int getMaxItemExtract(@Nullable Direction d) {
    return itemFaceConfig.get(d).canExtract() ? itemMaxExtract : 0;
  }

  @Override
  public int getMaxItemReceive(@Nullable Direction d) {
    return itemFaceConfig.get(d).canReceive() ? itemMaxReceive : 0;
  }

  @Override
  public Queue<Direction> getItemPushingCycle() {
    return itemPushingQueue;
  }

  @Override
  public Queue<Direction> getItemPullingCycle() {
    return itemPullingQueue;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean a) {
    if (level == null) {
      return;
    }

    if (active != a) {
      active = a;
      BlockState state = getBlockState();
      if (getBlockState().hasProperty(BuiltinBlockStates.ACTIVE)) {
        level.setBlockAndUpdate(worldPosition, state.setValue(BuiltinBlockStates.ACTIVE, a));
      }
      setChanged();
    }
  }

  protected void queuedPushPull() {
    if (level == null || level.isClientSide()) {
      return;
    }
    if (getMaxEnergy() > 0) {
      EnergyQueue.push(level, worldPosition, this);
      EnergyQueue.pull(level, worldPosition, this);
    }
    if (inventory.getSlots() > 0) {
      ItemQueue.push(level, worldPosition, this);
      ItemQueue.pull(level, worldPosition, this);
    }
    if (fluidTanks.getTanks() > 0) {
      FluidQueue.push(level, worldPosition, this);
      FluidQueue.pull(level, worldPosition, this);
    }
  }

  public void setEnergyFaceMode(Direction side, FaceMode mode) {
    energyPushingQueue.remove(side);
    energyPullingQueue.remove(side);

    if (Objects.requireNonNull(mode) == FaceMode.ACTIVE_EXTRACT) {
      if (!energyPushingQueue.contains(side)) {
        energyPushingQueue.offer(side);
      }
    } else if (mode == FaceMode.ACTIVE_RECEIVE) {
      if (!energyPullingQueue.contains(side)) {
        energyPullingQueue.offer(side);
      }
    }

    energyFaceConfig.put(side, mode);
  }

  public void setItemFaceMode(Direction side, FaceMode mode) {
    itemPushingQueue.remove(side);
    itemPullingQueue.remove(side);

    if (Objects.requireNonNull(mode) == FaceMode.ACTIVE_EXTRACT) {
      if (!itemPushingQueue.contains(side)) {
        itemPushingQueue.offer(side);
      }
    } else if (mode == FaceMode.ACTIVE_RECEIVE) {
      if (!itemPullingQueue.contains(side)) {
        itemPullingQueue.offer(side);
      }
    }

    itemFaceConfig.put(side, mode);
  }

  public void setFluidFaceMode(Direction side, FaceMode mode) {
    fluidPushingQueue.remove(side);
    fluidPullingQueue.remove(side);

    if (Objects.requireNonNull(mode) == FaceMode.ACTIVE_EXTRACT) {
      if (!fluidPushingQueue.contains(side)) {
        fluidPushingQueue.offer(side);
      }
    } else if (mode == FaceMode.ACTIVE_RECEIVE) {
      if (!fluidPullingQueue.contains(side)) {
        fluidPullingQueue.offer(side);
      }
    }

    fluidFaceConfig.put(side, mode);
  }

  public Syncer getAttributes() {
    return syncer;
  }

  public ContainerData getContainerData() {
    return syncer.asContainerData();
  }

  protected boolean isEnergySufficient() {
    return getEnergy() >= getEfficiency();
  }

  public int getEfficiency() {
    return efficiency;
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.loadAdditional(tag, reg);
    setEnergy(tag.getInt("Energy"));
    inventory.fromTag(tag.getCompound("Inventory"), reg);
    fluidTanks.fromTag(tag.getCompound("FluidTank"), reg);
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.saveAdditional(tag, reg);
    tag.putInt("Energy", getEnergy());
    tag.put("Inventory", inventory.createTag(reg));
    tag.put("FluidTank", fluidTanks.createTag(reg));
  }

  public abstract int getBasicEfficiency();

  @Override
  public Component getDisplayName() {
    return getBlockState().getBlock().getName();
  }

  public int getEnergyCapacity() {
    return BASE_ENERGY_CAPACITY;
  }

  @Override
  public void getLoot(List<ItemStack> loot) {
    for (int i = 0; i < inventory.getSlots(); i++) {
      if(inventory.getStackInSlot(i).isEmpty()) {
        continue;
      }
      loot.add(inventory.getStackInSlot(i));
    }
  }

  @Override
  public abstract @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player);
}
