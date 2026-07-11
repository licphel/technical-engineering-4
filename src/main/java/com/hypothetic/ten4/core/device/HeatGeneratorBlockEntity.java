package com.hypothetic.ten4.core.device;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.init.ModBlockEntities;
import com.hypothetic.ten4.init.ModMenus;
import com.hypothetic.ten4.lib.blockentity.device.SimpleGeneratorBlockEntity;
import com.hypothetic.ten4.lib.capability.item.ItemSlot;
import com.hypothetic.ten4.lib.capability.item.SlotOption;
import com.hypothetic.ten4.lib.container.AugmentableContainerMenu;
import com.hypothetic.ten4.lib.container.ContainerMenuLayout;
import com.hypothetic.ten4.lib.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class HeatGeneratorBlockEntity extends SimpleGeneratorBlockEntity {
  public HeatGeneratorBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.HEAT_GENERATOR.get(), pos, state);
  }

  @Override
  protected void setupStorage() {
    inventory.add(new ItemSlot(SlotOption.INPUT).setValidator(this::isValidInput));
  }

  @Override
  public boolean isValidInput(ItemStack stack) {
    return !strictInput || stack.getBurnTime(RecipeType.SMELTING) > 0;
  }

  @Override
  public int getBasicEfficiency() {
    return 30;
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable(Ten4.getLangKey("heat_generator"));
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int cid, Inventory inv, Player p) {
    ContainerMenuLayout layout = new ContainerMenuLayout()
        .add(0, 44, 35);
    return new AugmentableContainerMenu(ModMenus.HEAT_GENERATOR.get(), cid, inv, this, layout);
  }

  @Override
  public String getInfoLangKey() {
    return Ten4.getLangKey("heat_generator.desc");
  }

  @Override
  public int tryFueling(boolean sim) {
    ItemStack stack = inventory.getItem(0);
    int burn = stack.getBurnTime(RecipeType.SMELTING);
    if (!sim && burn > 0) {
      inventory.setItem(0, Util.shrinkWithRemainder(stack));
    }
    return burn;
  }
}
