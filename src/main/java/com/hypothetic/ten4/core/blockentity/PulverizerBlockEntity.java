package com.hypothetic.ten4.core.blockentity;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.device.DeviceInfo;
import com.hypothetic.ten4.api.blockentity.device.RecipeDeviceBlockEntity;
import com.hypothetic.ten4.api.capability.item.ItemSlot;
import com.hypothetic.ten4.api.capability.item.SlotOption;
import com.hypothetic.ten4.api.container.AugmentableContainerMenu;
import com.hypothetic.ten4.api.container.ContainerMenuLayout;
import com.hypothetic.ten4.api.recipe.IComplexRecipe;
import com.hypothetic.ten4.registry.ModMenus;
import com.hypothetic.ten4.registry.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PulverizerBlockEntity extends RecipeDeviceBlockEntity {
  public PulverizerBlockEntity(BlockPos pos, BlockState state) {
    super(pos, state);
  }

  @Override
  protected DeviceInfo makeDeviceInfo() {
    return new DeviceInfo()
        .enableEnergy()
        .enableItem()
        .addSlot(new ItemSlot(SlotOption.INPUT).setValidator(this::isValidInput))
        .addSlot(new ItemSlot(SlotOption.OUTPUT))
        .addSlot(new ItemSlot(SlotOption.OUTPUT))
        .addSlot(new ItemSlot(SlotOption.OUTPUT))
        .addSlot(new ItemSlot(SlotOption.OUTPUT))
        .setPower(15)
        .setEnergyCapacity(10_000)
        .setEnergyThroughput(100)
        .setItemThroughput(1);
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable(Ten4.lang("pulverizer"));
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int cid, Inventory inv, Player p) {
    ContainerMenuLayout layout = new ContainerMenuLayout()
        .add(0, 44, 35)
        .add(1, 98, 26)
        .add(2, 116, 26)
        .add(3, 98, 44)
        .add(4, 116, 44);
    return new AugmentableContainerMenu(ModMenus.PULVERIZER.get(), cid, inv, this, layout);
  }

  @Override
  public String createTranslationKey() {
    return Ten4.lang("pulverizer.desc");
  }

  @Override
  protected void initializeRecipeAutomation() {
    inputSlots.add(0);
    outputSlots.add(1);
    outputSlots.add(2);
    outputSlots.add(3);
    outputSlots.add(4);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected RecipeType<IComplexRecipe> getRecipeType() {
    return (RecipeType<IComplexRecipe>) ModRecipes.PULVERIZER_TYPE.get();
  }
}
