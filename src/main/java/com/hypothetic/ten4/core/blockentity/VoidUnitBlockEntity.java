package com.hypothetic.ten4.core.blockentity;

import com.hypothetic.ten4.api.blockentity.ITickable;
import com.hypothetic.ten4.api.blockentity.device.AbstractDeviceBlockEntity;
import com.hypothetic.ten4.api.blockentity.device.DeviceInfo;
import com.hypothetic.ten4.api.capability.fluid.FluidTank;
import com.hypothetic.ten4.api.capability.fluid.TankOption;
import com.hypothetic.ten4.api.capability.item.ItemSlot;
import com.hypothetic.ten4.api.capability.item.SlotOption;
import com.hypothetic.ten4.api.container.sync.Syncer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class VoidUnitBlockEntity extends AbstractDeviceBlockEntity implements ITickable {
  public VoidUnitBlockEntity(BlockPos pos, BlockState state) {
    super(pos, state);
  }

  @Override
  protected DeviceInfo makeDeviceInfo() {
    return new DeviceInfo()
        .enableFluid()
        .enableItem()
        .setFluidThroughput(Integer.MAX_VALUE)
        .setItemThroughput(Integer.MAX_VALUE)
        .addTank(new FluidTank(TankOption.INPUT, Integer.MAX_VALUE))
        .addSlot(new ItemSlot(SlotOption.INPUT, Integer.MAX_VALUE));
  }

  @Override
  protected void registerAdditionalSyncFields(Syncer syncer) {
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
    return null;
  }

  @Override
  public void tick() {
    fluidInventory.getTank(0).setFluid(FluidStack.EMPTY);
    inventory.getSlot(0).setStack(ItemStack.EMPTY);
  }

  @Override
  public boolean isAbleToInteractFluid() {
    return false; // AVOID MANUAL MISTAKE OPERATION
  }
}
