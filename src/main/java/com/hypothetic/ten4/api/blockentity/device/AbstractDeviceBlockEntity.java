package com.hypothetic.ten4.api.blockentity.device;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.ITranslatable;
import com.hypothetic.ten4.api.blockentity.ILootProvider;
import com.hypothetic.ten4.api.blockentity.RedstoneAwareBlockEntity;
import com.hypothetic.ten4.api.capability.energy.DirectionalEnergyStorage;
import com.hypothetic.ten4.api.capability.energy.EnergyQueue;
import com.hypothetic.ten4.api.capability.energy.IDirectionalEnergyProvider;
import com.hypothetic.ten4.api.capability.fluid.DirectionalFluidHandler;
import com.hypothetic.ten4.api.capability.fluid.FluidInventory;
import com.hypothetic.ten4.api.capability.fluid.FluidQueue;
import com.hypothetic.ten4.api.capability.fluid.IDirectionalFluidProvider;
import com.hypothetic.ten4.api.capability.item.DirectionalItemHandler;
import com.hypothetic.ten4.api.capability.item.IDirectionalItemProvider;
import com.hypothetic.ten4.api.capability.item.ItemInventory;
import com.hypothetic.ten4.api.capability.item.ItemQueue;
import com.hypothetic.ten4.api.container.sync.BuiltinSyncedFields;
import com.hypothetic.ten4.api.container.sync.Syncer;
import com.hypothetic.ten4.core.block.BuiltinBlockStates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class AbstractDeviceBlockEntity extends RedstoneAwareBlockEntity
    implements ILootProvider, ITranslatable, IDirectionalEnergyProvider, IDirectionalFluidProvider, IDirectionalItemProvider, MenuProvider {
  public static final ICapabilityProvider<AbstractDeviceBlockEntity, @Nullable Direction, IEnergyStorage> ENERGY = AbstractDeviceBlockEntity::getEnergyStorage;
  public static final ICapabilityProvider<AbstractDeviceBlockEntity, @Nullable Direction, IItemHandler> ITEM = AbstractDeviceBlockEntity::getItemHandler;
  public static final ICapabilityProvider<AbstractDeviceBlockEntity, @Nullable Direction, IFluidHandler> FLUID = AbstractDeviceBlockEntity::getFluidHandler;
  private static final int STRICT_INPUT_BIT = 1 << 24; // global strict-input flag in itemAutoFlags
  private static final int AUTO_EJECT_BIT = 1 << 25;
  private static final int AUTO_EXTRACT_BIT = 1 << 26;
  protected final Map<Direction, DirectionalEnergyStorage> energyHandlers = new HashMap<>();
  protected final Map<Direction, FaceMode> energyFaceConfig = new HashMap<>();
  protected final Queue<Direction> energyPushingQueue = new LinkedList<>();
  protected final Queue<Direction> energyPullingQueue = new LinkedList<>();
  protected final ItemInventory inventory;
  protected final Map<Direction, DirectionalItemHandler> itemHandlers = new HashMap<>();
  protected final Map<Direction, FaceMode> itemFaceConfig = new HashMap<>();
  protected final Queue<Direction> itemPushingQueue = new LinkedList<>();
  protected final Queue<Direction> itemPullingQueue = new LinkedList<>();
  protected final FluidInventory fluidInventory;
  protected final Map<Direction, DirectionalFluidHandler> fluidHandlers = new HashMap<>();
  protected final Map<Direction, FaceMode> fluidFaceConfig = new HashMap<>();
  protected final Queue<Direction> fluidPushingQueue = new LinkedList<>();
  protected final Queue<Direction> fluidPullingQueue = new LinkedList<>();
  protected final Syncer syncer = new Syncer();
  protected final DeviceInfo info;
  protected int energy = 0;
  protected boolean active;
  protected SignalMode sigMode = SignalMode.IGNORE;
  protected ComparatorMode comparatorMode = ComparatorMode.OFF;
  protected SecurityMode securityMode = SecurityMode.DISABLED;
  protected int energyAutoFlags;
  protected int itemAutoFlags;
  protected int fluidAutoFlags;
  private long lastPlay;
  private @Nullable UUID owner;
  public AbstractDeviceBlockEntity(BlockPos pos, BlockState state) {
    super(pos, state);

    registerAdditionalSyncFields(syncer);

    syncer.register(BuiltinSyncedFields.ENERGY_FACES);
    syncer.register(BuiltinSyncedFields.ITEM_FACES);
    syncer.register(BuiltinSyncedFields.FLUID_FACES);
    syncer.register(BuiltinSyncedFields.ACTIVE);
    syncer.register(BuiltinSyncedFields.SIG_MODE);
    syncer.register(BuiltinSyncedFields.COMPARATOR_MODE);
    syncer.register(BuiltinSyncedFields.SECURITY_MODE);
    syncer.register(BuiltinSyncedFields.ENERGY_AUTO_FLAGS);
    syncer.register(BuiltinSyncedFields.ITEM_AUTO_FLAGS);
    syncer.register(BuiltinSyncedFields.FLUID_AUTO_FLAGS);
    syncer.register(BuiltinSyncedFields.POWER);
    syncer.register(BuiltinSyncedFields.ENERGY_THROUGHPUT);
    syncer.register(BuiltinSyncedFields.ITEM_THROUGHPUT);
    syncer.register(BuiltinSyncedFields.FLUID_THROUGHPUT);

    syncer.seal();
    active = getBlockState().hasProperty(BuiltinBlockStates.ACTIVE)
        ? getBlockState().getValue(BuiltinBlockStates.ACTIVE) : false;

    inventory = new ItemInventory();
    inventory.setChangeListener(this::setChanged);
    inventory.setStillValidCheck(p -> level != null && level.getBlockEntity(worldPosition) == this);

    fluidInventory = new FluidInventory();

    info = makeDeviceInfo();
    info.slots.forEach(inventory::add);
    info.tanks.forEach(fluidInventory::add);

    initializeCapabilities();
  }

  public boolean isAutoEject(int type) {
    return switch (type) {
      case 0 -> (energyAutoFlags & AUTO_EJECT_BIT) != 0;
      case 1 -> (itemAutoFlags & AUTO_EJECT_BIT) != 0;
      default -> (fluidAutoFlags & AUTO_EJECT_BIT) != 0;
    };
  }

  public boolean isAutoExtract(int type) {
    return switch (type) {
      case 0 -> (energyAutoFlags & AUTO_EXTRACT_BIT) != 0;
      case 1 -> (itemAutoFlags & AUTO_EXTRACT_BIT) != 0;
      default -> (fluidAutoFlags & AUTO_EXTRACT_BIT) != 0;
    };
  }

  public void setAutoEject(int type, boolean v) {
    setGlobalFlag(type, AUTO_EJECT_BIT, v);
  }

  public void setAutoExtract(int type, boolean v) {
    setGlobalFlag(type, AUTO_EXTRACT_BIT, v);
  }

  private void setGlobalFlag(int type, int bit, boolean v) {
    switch (type) {
      case 0 -> energyAutoFlags = v ? (energyAutoFlags | bit) : (energyAutoFlags & ~bit);
      case 1 -> itemAutoFlags = v ? (itemAutoFlags | bit) : (itemAutoFlags & ~bit);
      default -> fluidAutoFlags = v ? (fluidAutoFlags | bit) : (fluidAutoFlags & ~bit);
    }
  }

  public @Nullable IEnergyStorage getEnergyStorage(@Nullable Direction side) {
    return !info.hasEnergy ? null : energyHandlers.get(side);
  }

  public @Nullable IItemHandler getItemHandler(@Nullable Direction side) {
    return !info.hasItem ? null : itemHandlers.get(side);
  }

  public @Nullable IFluidHandler getFluidHandler(@Nullable Direction side) {
    return !info.hasFluid ? null : fluidHandlers.get(side);
  }

  protected void initializeCapabilities() {
    for (Direction d : Direction.values()) {
      energyHandlers.put(d, new DirectionalEnergyStorage(this, d));
      itemHandlers.put(d, new DirectionalItemHandler(this, d));
      fluidHandlers.put(d, new DirectionalFluidHandler(this, d));
      energyFaceConfig.put(d, FaceMode.BIPASS);
      itemFaceConfig.put(d, FaceMode.BIPASS);
      fluidFaceConfig.put(d, FaceMode.BIPASS);
    }
    energyHandlers.put(null, new DirectionalEnergyStorage(this, null));
    itemHandlers.put(null, new DirectionalItemHandler(this, null));
    fluidHandlers.put(null, new DirectionalFluidHandler(this, null));
    energyFaceConfig.put(null, FaceMode.BIPASS);
    itemFaceConfig.put(null, FaceMode.BIPASS);
    fluidFaceConfig.put(null, FaceMode.BIPASS);
  }

  protected abstract DeviceInfo makeDeviceInfo();

  protected abstract void registerAdditionalSyncFields(Syncer syncer);

  public boolean isSignalEnabled() {
    if (level == null) {
      return false;
    }
    boolean hasSig = isRedstonePowered();
    return switch (sigMode) {
      case IGNORE -> true;
      case HIGH_LEVEL -> hasSig;
      case LOW_LEVEL -> !hasSig;
    };
  }

  public void setSigMode(SignalMode mode) {
    this.sigMode = mode;
    if (level != null) {
      level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
    }
  }

  public SecurityMode getSecurityMode() {
    return securityMode;
  }

  public @Nullable UUID getOwner() {
    return owner;
  }

  public void setSecurityMode(SecurityMode mode, @Nullable UUID playerId) {
    if (mode == SecurityMode.PRIVATE && owner == null && playerId != null) {
      owner = playerId;
    }
    this.securityMode = mode;
  }

  public ComparatorMode getComparatorMode() {
    return comparatorMode;
  }

  public void setComparatorMode(ComparatorMode comparatorMode) {
    this.comparatorMode = comparatorMode;
    if (level != null) {
      level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
    }
  }

  public void setRawEnergyAutoFlags(int v) {
    energyAutoFlags = v;
    rebuildEnergyQueues();
  }

  public void setRawItemAutoFlags(int v) {
    itemAutoFlags = v;
    rebuildItemQueues();
  }

  public void setRawFluidAutoFlags(int v) {
    fluidAutoFlags = v;
    rebuildFluidQueues();
  }

  @Override
  public int getEnergy() {
    if (energy >= getEnergyCapacity()) {
      return energy = getEnergyCapacity();
    }
    return energy;
  }

  @Override
  public void setEnergy(int e) {
    energy = Math.clamp(e, 0, getEnergyCapacity());
  }

  @Override
  public int getEnergyCapacity() {
    return info.energyCapacity;
  }

  @Override
  public int getEnergyThroughput() {
    return info.energyThroughput;
  }

  @Override
  public boolean canExtractEnergy(@Nullable Direction d) {
    return energyFaceConfig.get(d).canExtract();
  }

  @Override
  public boolean canReceiveEnergy(@Nullable Direction d) {
    return energyFaceConfig.get(d).canReceive();
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
    return fluidInventory;
  }

  @Override
  public int getFluidThroughput() {
    return info.fluidThroughput;
  }

  @Override
  public boolean canExtractFluid(@Nullable Direction d) {
    return fluidFaceConfig.get(d).canExtract();
  }

  @Override
  public boolean canReceiveFluid(@Nullable Direction d) {
    return fluidFaceConfig.get(d).canReceive();
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
  public int getItemThroughput() {
    return info.itemThroughput;
  }

  @Override
  public boolean canExtractItem(@Nullable Direction d) {
    return itemFaceConfig.get(d).canExtract();
  }

  @Override
  public boolean canReceiveItem(@Nullable Direction d) {
    return itemFaceConfig.get(d).canReceive();
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
    active = BuiltinBlockStates.toggleActive(this, active, a);
  }

  protected void queuedPushPull() {
    if (level == null || level.isClientSide()) {
      return;
    }

    if (this.getEnergyCapacity() > 0) {
      EnergyQueue.push(level, worldPosition, this);
      EnergyQueue.pull(level, worldPosition, this);
      setChanged();
    }
    if (inventory.getSlots() > 0) {
      ItemQueue.push(level, worldPosition, this);
      ItemQueue.pull(level, worldPosition, this);
      setChanged();
    }
    if (fluidInventory.getTanks() > 0) {
      FluidQueue.push(level, worldPosition, this);
      FluidQueue.pull(level, worldPosition, this);
      setChanged();
    }
  }

  public void setEnergyFaceMode(Direction side, FaceMode mode) {
    energyFaceConfig.put(side, mode);
    rebuildEnergyQueues();
  }

  public void setItemFaceMode(Direction side, FaceMode mode) {
    itemFaceConfig.put(side, mode);
    rebuildItemQueues();
  }

  public void setFluidFaceMode(Direction side, FaceMode mode) {
    fluidFaceConfig.put(side, mode);
    rebuildFluidQueues();
  }

  private void rebuildEnergyQueues() {
    energyPushingQueue.clear();
    energyPullingQueue.clear();
    if (!isAutoEject(0) && !isAutoExtract(0)) {
      return;
    }
    for (Direction d : Direction.values()) {
      if (energyFaceConfig.get(d).canExtract() && isAutoEject(0)) {
        energyPushingQueue.offer(d);
      }
      if (energyFaceConfig.get(d).canReceive() && isAutoExtract(0)) {
        energyPullingQueue.offer(d);
      }
    }
  }

  private void rebuildItemQueues() {
    itemPushingQueue.clear();
    itemPullingQueue.clear();
    if (!isAutoEject(1) && !isAutoExtract(1)) {
      return;
    }
    for (Direction d : Direction.values()) {
      if (itemFaceConfig.get(d).canExtract() && isAutoEject(1)) {
        itemPushingQueue.offer(d);
      }
      if (itemFaceConfig.get(d).canReceive() && isAutoExtract(1)) {
        itemPullingQueue.offer(d);
      }
    }
  }

  private void rebuildFluidQueues() {
    fluidPushingQueue.clear();
    fluidPullingQueue.clear();
    if (!isAutoEject(2) && !isAutoExtract(2)) {
      return;
    }
    for (Direction d : Direction.values()) {
      if (fluidFaceConfig.get(d).canExtract() && isAutoEject(2)) {
        fluidPushingQueue.offer(d);
      }
      if (fluidFaceConfig.get(d).canReceive() && isAutoExtract(2)) {
        fluidPullingQueue.offer(d);
      }
    }
  }

  protected List<Integer> getComparatorSignalSlots() {
    return Collections.emptyList();
  }

  protected List<Integer> getComparatorSignalTanks() {
    return Collections.emptyList();
  }

  @Override
  public boolean canConnectRedstone(@Nullable Direction side) {
    return sigMode != SignalMode.IGNORE;
  }

  public int getComparatorSignal() {
    return switch (comparatorMode) {
      case OUTPUT_ITEMS -> {
        List<Integer> sigSlots = getComparatorSignalTanks();
        if (sigSlots.isEmpty()) {
          yield 0;
        }

        float f = 0.0F;

        for (int i : sigSlots) {
          ItemStack itemstack = inventory.getItem(i);
          if (!itemstack.isEmpty()) {
            f += (float) itemstack.getCount() / (float) inventory.getSlotLimit(i);
          }
        }

        f /= (float) sigSlots.size();
        yield Mth.lerpDiscrete(f, 0, 15);
      }
      case OUTPUT_FLUID -> {
        List<Integer> sigTanks = getComparatorSignalTanks();
        if (sigTanks.isEmpty()) {
          yield 0;
        }

        float f = 0.0F;

        for (int i : sigTanks) {
          FluidStack s = fluidInventory.getFluidInTank(i);
          if (!s.isEmpty()) {
            f += (float) s.getAmount() / (float) fluidInventory.getTankCapacity(i);
          }
        }

        f /= (float) sigTanks.size();
        yield Mth.lerpDiscrete(f, 0, 15);
      }
      case ENERGY -> getEnergy() > 0 ? (int) (14L * getEnergy() / getEnergyCapacity()) + 1 : 0;
      case ACTIVE -> isActive() ? 15 : 0;
      case OFF -> 0;
    };
  }

  public boolean isValidInput(ItemStack stack) {
    return true;
  }

  public boolean isValidInput(FluidStack stack) {
    return true;
  }

  protected void synchronizeBasicData() {
    syncer.set(BuiltinSyncedFields.ENERGY_FACES, FaceModePacker.packFaces(energyFaceConfig));
    syncer.set(BuiltinSyncedFields.ITEM_FACES, FaceModePacker.packFaces(itemFaceConfig));
    syncer.set(BuiltinSyncedFields.FLUID_FACES, FaceModePacker.packFaces(fluidFaceConfig));
    syncer.set(BuiltinSyncedFields.SIG_MODE, sigMode.ordinal());
    syncer.set(BuiltinSyncedFields.ACTIVE, active);
    syncer.set(BuiltinSyncedFields.COMPARATOR_MODE, comparatorMode.ordinal());
    syncer.set(BuiltinSyncedFields.SECURITY_MODE, securityMode.ordinal());
    syncer.set(BuiltinSyncedFields.ENERGY_AUTO_FLAGS, energyAutoFlags);
    syncer.set(BuiltinSyncedFields.ITEM_AUTO_FLAGS, itemAutoFlags);
    syncer.set(BuiltinSyncedFields.FLUID_AUTO_FLAGS, fluidAutoFlags);
    syncer.set(BuiltinSyncedFields.POWER, getActualPower());
    syncer.set(BuiltinSyncedFields.ENERGY_THROUGHPUT, getEnergyThroughput());
    syncer.set(BuiltinSyncedFields.ITEM_THROUGHPUT, getItemThroughput());
    syncer.set(BuiltinSyncedFields.FLUID_THROUGHPUT, getFluidThroughput());
  }

  public Syncer getAttributes() {
    return syncer;
  }

  public ContainerData getContainerData() {
    return syncer.asContainerData();
  }

  public boolean isItemStrictInput() {
    return (itemAutoFlags & STRICT_INPUT_BIT) != 0;
  }

  public boolean isFluidStrictInput() {
    return (fluidAutoFlags & STRICT_INPUT_BIT) != 0;
  }

  protected boolean isEnergySufficient() {
    return getEnergy() >= getActualPower();
  }

  public int getActualPower() {
    return info.power;
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.loadAdditional(tag, reg);

    setEnergy(tag.getInt("Energy"));
    inventory.fromTag(tag.getCompound("Inventory"), reg);
    fluidInventory.fromTag(tag.getCompound("FluidTank"), reg);

    CompoundTag cfg = tag.getCompound("Configuration");
    int enf = cfg.getInt("EnergyFaceMasks");
    int itf = cfg.getInt("ItemFaceMasks");
    int flf = cfg.getInt("FluidFaceMasks");

    for (Direction d : Direction.values()) {
      setEnergyFaceMode(d, FaceModePacker.get(enf, d));
      setItemFaceMode(d, FaceModePacker.get(itf, d));
      setFluidFaceMode(d, FaceModePacker.get(flf, d));
    }

    sigMode = SignalMode.of(cfg.getInt("SigMode"));
    comparatorMode = ComparatorMode.of(cfg.getInt("ComparatorMode"));
    securityMode = cfg.contains("SecurityMode") ? SecurityMode.of(cfg.getInt("SecurityMode")) : SecurityMode.DISABLED;
    if (cfg.contains("Owner")) {
      owner = cfg.getUUID("Owner");
    }
    energyAutoFlags = cfg.getInt("EnergyAutoFlags");
    itemAutoFlags = cfg.getInt("ItemAutoFlags");
    fluidAutoFlags = cfg.getInt("FluidAutoFlags");

    rebuildEnergyQueues();
    rebuildItemQueues();
    rebuildFluidQueues();
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider reg) {
    super.saveAdditional(tag, reg);

    tag.putInt("Energy", getEnergy());
    tag.put("Inventory", inventory.createTag(reg));
    tag.put("FluidTank", fluidInventory.createTag(reg));

    CompoundTag cfg = new CompoundTag();
    cfg.putInt("EnergyFaceMasks", FaceModePacker.packFaces(energyFaceConfig));
    cfg.putInt("ItemFaceMasks", FaceModePacker.packFaces(itemFaceConfig));
    cfg.putInt("FluidFaceMasks", FaceModePacker.packFaces(fluidFaceConfig));
    cfg.putInt("SigMode", sigMode.ordinal());
    cfg.putInt("ComparatorMode", comparatorMode.ordinal());
    cfg.putInt("SecurityMode", securityMode.ordinal());
    if (owner != null) {
      cfg.putUUID("Owner", owner);
    }
    cfg.putInt("EnergyAutoFlags", energyAutoFlags);
    cfg.putInt("ItemAutoFlags", itemAutoFlags);
    cfg.putInt("FluidAutoFlags", fluidAutoFlags);
    tag.put("Configuration", cfg);
  }

  @Override
  public Component getDisplayName() {
    return createTranslation();
  }

  @Override
  public String createTranslationKey() {
    ResourceLocation id = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(getType());
    if (id == null) {
      Ten4.LOGGER.warn("Cannot find BlockEntityType: '{}'", getType());
      return "";
    }
    return Ten4.lang(id.getPath());
  }

  @Override
  public void getLoot(List<ItemStack> loot) {
    for (int i = 0; i < inventory.getSlots(); i++) {
      if (inventory.getStackInSlot(i).isEmpty()) {
        continue;
      }
      loot.add(inventory.getStackInSlot(i));
    }
  }

  @Override
  public abstract @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player);

  public void playSound(float interval, SoundEvent event) {
    if (level != null && level.getGameTime() - lastPlay >= (double) 20 * interval - 5.0) {
      level.playSound(null, worldPosition, event, SoundSource.BLOCKS, 1.0F, 1.0F);
      lastPlay = level.getGameTime();
    }
  }

  public void triggerSound() {
    onSoundPlay();
  }

  public void onSoundPlay() {
  }
}
