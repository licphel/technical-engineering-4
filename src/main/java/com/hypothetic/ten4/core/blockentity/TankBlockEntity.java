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
import com.hypothetic.ten4.core.item.TankItem;
import com.hypothetic.ten4.core.registry.ModMenus;
import com.hypothetic.ten4.core.registry.config.ModConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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

public class TankBlockEntity extends AugmentableDeviceBlockEntity implements ITickable {
  public static final SyncedFluidStack TANK_0 = new SyncedFluidStack(0);

  public TankBlockEntity(BlockPos pos, BlockState state) {
    super(pos, state);
  }

  @Override
  protected DeviceInfo makeDeviceInfo() {
    int capacity = ModConfigs.COMMON.others.tankCapacity.get();
    return OtherTiers.TANK.get()
        .addSlot(new ItemSlot(SlotOption.BOTH).setValidator(this::isValidInput))
        .addSlot(new ItemSlot(SlotOption.BOTH).setValidator(this::isValidInput))
        .addTank(new FluidTank(TankOption.BOTH, capacity));
  }

  @Override
  public boolean isValidInput(ItemStack stack) {
    return !isItemStrictInput() || stack.getCapability(Capabilities.FluidHandler.ITEM, null) != null;
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
  public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
    ContainerMenuLayout layout = new ContainerMenuLayout()
        .add(0, 44, 35)
        .add(1, 116, 35);
    return new AugmentableContainerMenu(ModMenus.TANK.get(), containerId, playerInventory, this, layout);
  }

  @Override
  public void tick() {
    if (level == null || level.isClientSide()) return;

    if (isSignalEnabled()) {
      queuedPushPull();

      ItemStack input  = inventory.getStackInSlot(1);
      ItemStack output = inventory.getStackInSlot(0);
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

      if (!input.isEmpty() && output.isEmpty() && !isItemStrictInput()) {
        IFluidHandlerItem itemFh = input.getCapability(Capabilities.FluidHandler.ITEM);
        if (itemFh != null) {
          FluidStack left = itemFh.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
          if (left.isEmpty()) {
            inventory.setStackInSlot(1, itemFh.getContainer());
            inventory.setStackInSlot(0, ItemStack.EMPTY);
          }
        }
      }
    }

    synchronizeBasicData();
    TANK_0.sync(syncer, fluidInventory.getTank(0));
  }

  public ItemStack createDropStack() {
    if (level == null || level.isClientSide()) {
      return ItemStack.EMPTY;
    }
    ItemStack stack = new ItemStack(getBlockState().getBlock().asItem());
    FluidStack fluid = fluidInventory.getFluidInTank(0);
    if (!fluid.isEmpty()) {
      TankItem.setStoredFluid(stack, fluid, level.registryAccess());
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
