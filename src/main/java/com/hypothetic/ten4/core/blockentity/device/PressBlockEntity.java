package com.hypothetic.ten4.core.blockentity.device;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.device.ComplexRecipeDeviceBlockEntity;
import com.hypothetic.ten4.api.blockentity.device.DeviceInfo;
import com.hypothetic.ten4.api.capability.item.ItemSlot;
import com.hypothetic.ten4.api.capability.item.SlotOption;
import com.hypothetic.ten4.api.container.AugmentableContainerMenu;
import com.hypothetic.ten4.api.container.ContainerMenuLayout;
import com.hypothetic.ten4.api.recipe.IComplexRecipe;
import com.hypothetic.ten4.core.registry.ModMenus;
import com.hypothetic.ten4.core.registry.ModRecipes;
import com.hypothetic.ten4.core.registry.ModSoundEvents;
import com.hypothetic.ten4.datagen.tag.ItemTagData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PressBlockEntity extends ComplexRecipeDeviceBlockEntity {
  public PressBlockEntity(BlockPos pos, BlockState state) {
    super(pos, state);
  }

  @Override
  protected DeviceInfo makeDeviceInfo() {
    return DeviceTiers.PRESS.get()
        .addSlot(new ItemSlot(SlotOption.INPUT).setValidator(s -> isValidInput(s) && !s.is(ItemTagData.DIES)))
        .addSlot(new ItemSlot(SlotOption.INPUT).setValidator(s -> s.is(ItemTagData.DIES)))
        .addSlot(new ItemSlot(SlotOption.OUTPUT));
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int cid, Inventory inv, Player p) {
    ContainerMenuLayout layout = new ContainerMenuLayout()
        .add(0, 44, 22)
        .add(1, 44, 48)
        .add(2, 116, 35);
    return new AugmentableContainerMenu(ModMenus.PRESS.get(), cid, inv, this, layout);
  }

  @Override
  public void onSoundPlay() {
    playSound(1.0F, ModSoundEvents.DEVICE_NOISE_1.get());
  }

  @Override
  protected RecipeType<IComplexRecipe> getRecipeType() {
    return ModRecipes.PRESSING.get();
  }

  @Override
  protected void initializeRecipeAutomation() {
    inputSlots.add(0);
    inputSlots.add(1);
    outputSlots.add(2);
  }
}
