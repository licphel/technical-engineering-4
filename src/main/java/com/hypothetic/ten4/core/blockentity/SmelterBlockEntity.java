package com.hypothetic.ten4.core.blockentity;

import com.hypothetic.ten4.api.blockentity.device.AbstractRecipeDeviceBlockEntity;
import com.hypothetic.ten4.api.blockentity.device.DeviceInfo;
import com.hypothetic.ten4.api.capability.item.ItemSlot;
import com.hypothetic.ten4.api.capability.item.SlotOption;
import com.hypothetic.ten4.api.container.AugmentableContainerMenu;
import com.hypothetic.ten4.api.container.ContainerMenuLayout;
import com.hypothetic.ten4.core.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SmelterBlockEntity extends AbstractRecipeDeviceBlockEntity<SingleRecipeInput, SmeltingRecipe> {
  public SmelterBlockEntity(BlockPos pos, BlockState state) {
    super(pos, state);
  }

  @Override
  protected boolean hasEnoughOutputSpace() {
    if (recipe == null || level == null) {
      return true;
    }
    ItemStack output = recipe.getResultItem(level.registryAccess());
    ItemSlot slot = inventory.getSlot(1);
    ItemStack preexist = slot.getStack();
    boolean itemEqual = ItemStack.isSameItemSameComponents(output, preexist);

    if (itemEqual) {
      return output.getCount() + preexist.getCount() <= slot.getSlotLimit();
    }

    return preexist.isEmpty();
  }

  @Override
  protected void finish() {
    assert recipe != null && level != null;
    ItemStack output = recipe.assemble(new SingleRecipeInput(inventory.getItem(0)), level.registryAccess());
    giveItemOutput(output);
    inventory.forceExtract(1, inputSlots, false);
  }

  @Override
  protected boolean doesRecipeMatch(SmeltingRecipe r) {
    if (level == null) {
      return false;
    }
    return r.matches(new SingleRecipeInput(inventory.getItem(0)), level);
  }

  @Override
  protected DeviceInfo makeDeviceInfo() {
    return new DeviceInfo()
        .enableEnergy()
        .enableItem()
        .addSlot(new ItemSlot(SlotOption.INPUT).setValidator(this::isValidInput))
        .addSlot(new ItemSlot(SlotOption.OUTPUT))
        .setPower(15)
        .setEnergyCapacity(10_000)
        .setEnergyThroughput(100)
        .setItemThroughput(1);
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int cid, Inventory inv, Player p) {
    ContainerMenuLayout layout = new ContainerMenuLayout()
        .add(0, 44, 35)
        .add(1, 116, 35);
    return new AugmentableContainerMenu(ModMenus.SMELTER.get(), cid, inv, this, layout);
  }

  @Override
  protected void initializeRecipeAutomation() {
    inputSlots.add(0);
    outputSlots.add(1);
  }

  @Override
  protected RecipeType<SmeltingRecipe> getRecipeType() {
    return RecipeType.SMELTING;
  }

  @Override
  protected int getRecipeTime(SmeltingRecipe r) {
    return r.getCookingTime();
  }
}
