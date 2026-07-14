package com.hypothetic.ten4.core.blockentity;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.device.DeviceInfo;
import com.hypothetic.ten4.api.blockentity.device.SimpleGeneratorBlockEntity;
import com.hypothetic.ten4.api.capability.item.ItemSlot;
import com.hypothetic.ten4.api.capability.item.SlotOption;
import com.hypothetic.ten4.api.container.AugmentableContainerMenu;
import com.hypothetic.ten4.api.container.ContainerMenuLayout;
import com.hypothetic.ten4.registry.ModBlockEntities;
import com.hypothetic.ten4.registry.ModMenus;
import com.hypothetic.ten4.util.ItemStackUtil;
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
  protected DeviceInfo makeDeviceInfo() {
    return new DeviceInfo()
        .enableEnergy()
        .enableItem()
        .addSlot(new ItemSlot(SlotOption.INPUT).setValidator(this::isValidInput))
        .setPower(20)
        .setEnergyCapacity(10_000)
        .setEnergyThroughput(100)
        .setItemThroughput(1);
  }

  @Override
  public boolean isValidInput(ItemStack stack) {
    return !strictInput || stack.getBurnTime(RecipeType.SMELTING) > 0;
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
  public String createTranslationKey() {
    return Ten4.getLangKey("heat_generator.desc");
  }

  @Override
  public int tryFueling(boolean sim) {
    ItemStack stack = inventory.getItem(0);
    int burn = stack.getBurnTime(RecipeType.SMELTING);
    if (!sim && burn > 0) {
      inventory.setItem(0, ItemStackUtil.shrinkWithRemainder(stack));
    }
    return burn;
  }
}
