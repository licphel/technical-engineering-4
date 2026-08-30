package com.hypothetic.ten4.core.blockentity;

import com.hypothetic.ten4.api.blockentity.IClickFluidInteractable;
import com.hypothetic.ten4.api.blockentity.ITickable;
import com.hypothetic.ten4.api.blockentity.device.AugmentableDeviceBlockEntity;
import com.hypothetic.ten4.api.blockentity.device.DeviceInfo;
import com.hypothetic.ten4.api.capability.fluid.FluidTank;
import com.hypothetic.ten4.api.capability.fluid.TankOption;
import com.hypothetic.ten4.api.capability.item.ItemSlot;
import com.hypothetic.ten4.api.capability.item.SlotOption;
import com.hypothetic.ten4.api.container.AugmentableContainerMenu;
import com.hypothetic.ten4.api.container.ContainerMenuLayout;
import com.hypothetic.ten4.api.container.sync.SyncedFluidStack;
import com.hypothetic.ten4.api.container.sync.Syncer;
import com.hypothetic.ten4.api.network.PacketDist;
import com.hypothetic.ten4.api.network.device.LiquidUnitFluidPayload;
import com.hypothetic.ten4.core.registry.ModMenus;
import com.hypothetic.ten4.core.registry.config.ModConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LiquidUnitBlockEntity extends AugmentableDeviceBlockEntity implements ITickable {
  public static final SyncedFluidStack TANK_0 = new SyncedFluidStack(0);
  private FluidStack clientFluid = FluidStack.EMPTY;
  private float clientScale;

  public LiquidUnitBlockEntity(BlockPos pos, BlockState state) {
    super(pos, state);
  }

  @Override
  protected DeviceInfo makeDeviceInfo() {
    int capacity = ModConfigs.COMMON.others.liquidUnitCapacity.get();
    return OtherTiers.LIQUID_UNIT.get()
        .addSlot(new ItemSlot(SlotOption.BOTH, 1).setValidator(this::isValidInput))
        .addSlot(new ItemSlot(SlotOption.BOTH, 1).setValidator(this::isValidInput))
        .addTank(new FluidTank(TankOption.BOTH, augTankLimiter(capacity)));
  }

  @Override
  protected void registerAdditionalSyncFields(Syncer syncer) {
    TANK_0.register(syncer);
  }

  @Override
  protected List<Integer> getComparatorSignalTanks() {
    return List.of(0);
  }

  @Override
  public boolean isValidInput(ItemStack stack) {
    return !isItemStrictInput() || stack.getCapability(Capabilities.FluidHandler.ITEM, null) != null;
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
    ContainerMenuLayout layout = new ContainerMenuLayout()
        .add(0, 44, 35)
        .add(1, 116, 35);
    return new AugmentableContainerMenu(ModMenus.LIQUID_UNIT.get(), containerId, playerInventory, this, layout);
  }

  @Override
  public void tick() {
    if (level == null || level.isClientSide()) {
      return;
    }

    if (isSignalEnabled()) {
      queuedPushPull();

      ItemStack input = inventory.getStackInSlot(0);
      ItemStack output = inventory.getStackInSlot(1);

      if (!input.isEmpty()) {
        IFluidHandlerItem itemFh = input.getCapability(Capabilities.FluidHandler.ITEM);
        if (itemFh != null) {
          FluidUtil.tryFluidTransfer(fluidInventory, itemFh, getFluidThroughput(), true);
        }
      }

      if (!output.isEmpty()) {
        IFluidHandlerItem itemFh = output.getCapability(Capabilities.FluidHandler.ITEM);
        if (itemFh != null) {
          FluidUtil.tryFluidTransfer(itemFh, fluidInventory, getFluidThroughput(), true);
        }
      }
    }

    synchronizeBasicData();
    TANK_0.sync(syncer, fluidInventory.getTank(0));

    // Broadcast the stored fluid to nearby players for the BER
    if (level.getGameTime() % 5 == 0) {
      if (level instanceof ServerLevel sl) {
        FluidTank tank = fluidInventory.getTank(0);
        float scale = tank.getCapacity() > 0 ? (float) tank.getFluidAmount() / tank.getCapacity() : 0F;
        PacketDist.sendToNearbyPlayers(sl,
            new LiquidUnitFluidPayload(worldPosition, tank.getFluid(), scale),
            worldPosition, 32D);
      }
    }
  }

  @Override
  public ItemStack createSelfStack() {
    assert level != null;

    ItemStack stack = super.createSelfStack();
    CompoundTag beTag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    CompoundTag tankTag = fluidInventory.createTag(level.registryAccess());
    tankTag.putInt("Capacity", fluidInventory.getTank(0).getCapacity());
    beTag.put("FluidTank", tankTag);
    stack.set(DataComponents.CUSTOM_DATA, CustomData.of(beTag));
    return stack;
  }

  @Override
  public void onPlaced(@Nullable LivingEntity entity, ItemStack stack) {
    super.onPlaced(entity, stack);
    if (level == null || level.isClientSide() || stack.isEmpty()) {
      return;
    }
    CustomData cd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
    if (cd.isEmpty()) {
      return;
    }
    fluidInventory.fromTag(cd.copyTag().getCompound("FluidTank"), level.registryAccess());
  }

  /** Client-only: fluid set by the network payload, read by the BER. */
  public FluidStack getClientFluid() {
    return clientFluid;
  }

  public float getClientScale() {
    return clientScale;
  }

  public void syncClientFluid(float scale, FluidStack fluid) {
    clientScale = scale;
    clientFluid = fluid;
  }
}
