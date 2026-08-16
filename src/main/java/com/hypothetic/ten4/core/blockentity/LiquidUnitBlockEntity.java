package com.hypothetic.ten4.core.blockentity;

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
import com.hypothetic.ten4.core.item.LiquidUnitItem;
import com.hypothetic.ten4.core.registry.ModMenus;
import com.hypothetic.ten4.core.registry.config.ModConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LiquidUnitBlockEntity extends AugmentableDeviceBlockEntity implements ITickable {
  public static final SyncedFluidStack LIQUID_UNIT_0 = new SyncedFluidStack(0);

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
  public boolean isValidInput(ItemStack stack) {
    return !isItemStrictInput() || stack.getCapability(Capabilities.FluidHandler.ITEM, null) != null;
  }

  @Override
  protected void registerAdditionalSyncFields(Syncer syncer) {
    LIQUID_UNIT_0.register(syncer);
  }

  @Override
  protected List<Integer> getComparatorSignalTanks() {
    return List.of(0);
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
    if (level == null || level.isClientSide()) return;

    if (isSignalEnabled()) {
      queuedPushPull();

      ItemStack input = inventory.getStackInSlot(0);
      ItemStack output = inventory.getStackInSlot(1);
      IFluidHandler tank = fluidInventory;
      FluidStack tankFluid = tank.getFluidInTank(0);

      if (!input.isEmpty()) {
        IFluidHandlerItem itemFh = input.getCapability(Capabilities.FluidHandler.ITEM);
        if (itemFh != null) {
          FluidStack sim = itemFh.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
          if (!sim.isEmpty() && (tankFluid.isEmpty() || FluidStack.isSameFluidSameComponents(tankFluid, sim))) {
            int space = Math.min(
                tank.getTankCapacity(0) - tank.getFluidInTank(0).getAmount(), Integer.MAX_VALUE);
            int toMove = Math.min(sim.getAmount(), space);
            if (toMove > 0) {
              FluidStack drained = itemFh.drain(toMove, IFluidHandler.FluidAction.EXECUTE);
              if (!drained.isEmpty()) {
                tank.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                inventory.setStackInSlot(0, itemFh.getContainer());
                tankFluid = tank.getFluidInTank(0);
              }
            }
          }
        }
      }

      if (!output.isEmpty()) {
        IFluidHandlerItem itemFh = output.getCapability(Capabilities.FluidHandler.ITEM);
        if (itemFh != null && !tankFluid.isEmpty()) {
          int filled = itemFh.fill(tankFluid.copy(), IFluidHandler.FluidAction.EXECUTE);
          if (filled > 0) {
            tank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
            inventory.setStackInSlot(1, itemFh.getContainer());
          }
        }
      }
    }

    synchronizeBasicData();
    LIQUID_UNIT_0.sync(syncer, fluidInventory.getTank(0));

    // Broadcast fluid to nearby players every 5 ticks for the BER (duct-style)
    if (level.getGameTime() % 5 == 0) {
      if (level instanceof ServerLevel sl) {
        FluidTank tank = fluidInventory.getTank(0);
        float scale = tank.getCapacity() > 0
            ? (float) tank.getFluidAmount() / tank.getCapacity() : 0F;
        PacketDist.sendToNearbyPlayers(sl,
            new LiquidUnitFluidPayload(worldPosition, scale, tank.getFluid()),
            worldPosition, 32D);
      }
    }
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

  public ItemStack createDropStack() {
    if (level == null || level.isClientSide()) {
      return ItemStack.EMPTY;
    }
    ItemStack stack = new ItemStack(getBlockState().getBlock().asItem());
    FluidStack fluid = fluidInventory.getFluidInTank(0);
    if (!fluid.isEmpty()) {
      LiquidUnitItem.setStoredFluid(stack, fluid, level.registryAccess());
    }
    return stack;
  }

  @Override
  public void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider reg) {
    super.loadAdditional(tag, reg);
  }

  @Override
  public void getLoot(List<ItemStack> loot, boolean shouldDropSelf) {
    if (shouldDropSelf) {
      loot.add(createDropStack());
    }
  }
}
