package com.hypothetic.ten4.core.blockentity.device;

import com.hypothetic.ten4.api.blockentity.device.ComplexRecipeDeviceBlockEntity;
import com.hypothetic.ten4.api.blockentity.device.DeviceInfo;
import com.hypothetic.ten4.api.capability.fluid.FluidTank;
import com.hypothetic.ten4.api.capability.fluid.TankOption;
import com.hypothetic.ten4.api.capability.item.ItemSlot;
import com.hypothetic.ten4.api.capability.item.SlotOption;
import com.hypothetic.ten4.api.container.AugmentableContainerMenu;
import com.hypothetic.ten4.api.container.ContainerMenuLayout;
import com.hypothetic.ten4.api.container.sync.SyncedFluidStack;
import com.hypothetic.ten4.api.container.sync.Syncer;
import com.hypothetic.ten4.api.recipe.IComplexRecipe;
import com.hypothetic.ten4.core.registry.ModMenus;
import com.hypothetic.ten4.core.registry.ModRecipes;
import com.hypothetic.ten4.core.registry.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RefinerBlockEntity extends ComplexRecipeDeviceBlockEntity {
  public static final SyncedFluidStack TANK_0 = new SyncedFluidStack(0);
  public static final SyncedFluidStack TANK_1 = new SyncedFluidStack(1);

  public RefinerBlockEntity(BlockPos pos, BlockState state) {
    super(pos, state);
  }

  @Override
  protected void registerAdditionalSyncFields(Syncer syncer) {
    super.registerAdditionalSyncFields(syncer);
    TANK_0.register(syncer);
    TANK_1.register(syncer);
  }

  @Override
  public void tick() {
    super.tick();

    if (level != null && !level.isClientSide()) {
      TANK_0.sync(syncer, fluidInventory.getTank(0));
      TANK_1.sync(syncer, fluidInventory.getTank(1));
    }
  }

  @Override
  protected RecipeType<IComplexRecipe> getRecipeType() {
    return ModRecipes.REFINING.get();
  }

  @Override
  protected void initializeRecipeAutomation() {
    inputSlots.add(0);
    outputSlots.add(1);
    inputTanks.add(0);
    outputTanks.add(1);
  }

  @Override
  protected DeviceInfo makeDeviceInfo() {
    return DeviceTiers.REFINER.get()
        .addSlot(new ItemSlot(SlotOption.INPUT).setValidator(this::isValidInput))
        .addSlot(new ItemSlot(SlotOption.OUTPUT))
        .addTank(new FluidTank(TankOption.INPUT, 10_000).setValidator(this::isValidInput))
        .addTank(new FluidTank(TankOption.OUTPUT, 10_000));
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int cid, Inventory inv, Player p) {
    ContainerMenuLayout layout = new ContainerMenuLayout()
        .add(0, 57, 35)
        .add(1, 111, 35);
    return new AugmentableContainerMenu(ModMenus.REFINER.get(), cid, inv, this, layout);
  }

  @Override
  public void onSoundPlay() {
    playSound(1.0F, ModSoundEvents.DEVICE_NOISE_2.get());
  }
}
