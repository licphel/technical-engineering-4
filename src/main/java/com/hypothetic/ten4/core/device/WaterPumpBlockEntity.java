package com.hypothetic.ten4.core.device;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.ITickable;
import com.hypothetic.ten4.api.blockentity.device.AugmentableDeviceBlockEntity;
import com.hypothetic.ten4.api.blockentity.device.ComparatorMode;
import com.hypothetic.ten4.api.blockentity.device.DeviceInfo;
import com.hypothetic.ten4.api.capability.fluid.FluidTank;
import com.hypothetic.ten4.api.capability.fluid.TankOption;
import com.hypothetic.ten4.api.container.ContainerMenu;
import com.hypothetic.ten4.api.container.ContainerMenuLayout;
import com.hypothetic.ten4.api.container.sync.BuiltinSyncedFields;
import com.hypothetic.ten4.api.container.sync.SyncedFluidStack;
import com.hypothetic.ten4.api.container.sync.Syncer;
import com.hypothetic.ten4.registry.ModBlockEntities;
import com.hypothetic.ten4.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.wrappers.BucketPickupHandlerWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class WaterPumpBlockEntity extends AugmentableDeviceBlockEntity implements ITickable {
  public static final SyncedFluidStack TANK_0 = new SyncedFluidStack(0);

  public WaterPumpBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.WATER_PUMP.get(), pos, state);
  }

  @Override
  protected DeviceInfo makeDeviceInfo() {
    return new DeviceInfo()
        .enableEnergy()
        .enableFluid()
        .setEnergyCapacity(10_000)
        .addTank(new FluidTank(TankOption.OUTPUT, 20_000))
        .setEnergyThroughput(100)
        .setFluidThroughput(1000)
        .setPower(5);
  }

  @Override
  protected void registerAdditionalSyncFields(Syncer syncer) {
    syncer.register(BuiltinSyncedFields.ENERGY);
    syncer.register(BuiltinSyncedFields.MAX_ENERGY);
    TANK_0.register(syncer);
  }

  @Override
  public void tick() {
    if (level == null || level.isClientSide()) {
      return;
    }

    setActive(false);

    if (isSignalEnabled()) {
      queuedPushPull();

      if (isEnergySufficient()) {
        for (Direction side : Direction.values()) {
          BlockPos pos1 = worldPosition.relative(side);
          FluidStack fluidStack = FluidStack.EMPTY;
          FluidState fluidState = level.getFluidState(pos1);

          if (!fluidState.isEmpty() && fluidState.isSource()) {
            fluidStack = new FluidStack(fluidState .getType(), 1000);

            BlockState state = level.getBlockState(pos1);
            Block block = state.getBlock();
            IFluidHandler targetFluidHandler = null;
            if (block instanceof BucketPickup pickup) {
              targetFluidHandler = new BucketPickupHandlerWrapper(null, pickup, level, pos1);
            } else {
              IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos1, side.getOpposite());
              if (fluidHandler != null) {
                targetFluidHandler = fluidHandler;
              }
            }

            FluidStack old = fluidInventory.getFluidInTank(0);
            int rem = fluidInventory.getTank(0).getSpace();
            if (targetFluidHandler != null && rem >= 1000) { // We only drain a full bucket
              FluidStack stack = targetFluidHandler.drain(rem, IFluidHandler.FluidAction.SIMULATE);
              if (old.isEmpty() || FluidStack.isSameFluidSameComponents(old, stack)) {
                targetFluidHandler.drain(rem, IFluidHandler.FluidAction.EXECUTE);
                fluidInventory.forceFill(stack, List.of(0), IFluidHandler.FluidAction.EXECUTE);
              }
            }
          }

          if (!fluidStack.isEmpty()) {
            fluidInventory.forceFill(fluidStack, List.of(0), IFluidHandler.FluidAction.EXECUTE);
            setActive(true);
            setChanged();
            setEnergy(getEnergy() - getActualPower());
          }
        }
      }
    }

    syncer.set(BuiltinSyncedFields.ENERGY, getEnergy());
    syncer.set(BuiltinSyncedFields.MAX_ENERGY, getEnergyCapacity());
    synchronizeBasicData();
    TANK_0.sync(syncer, fluidInventory.getTank(0));
  }

  @Override
  protected List<Integer> getComparatorSignalTanks() {
    return List.of(0);
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
    return new ContainerMenu(ModMenus.WATER_PUMP.get(), containerId, playerInventory, this, new ContainerMenuLayout());
  }

  @Override
  public String getInfoLangKey() {
    return Ten4.getLangKey("water_pump.desc");
  }
}
