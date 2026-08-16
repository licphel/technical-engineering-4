package com.hypothetic.ten4.core.item;

import com.hypothetic.ten4.util.RegistryUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.FluidStack;

public class LiquidUnitItem extends BlockItem {
  public LiquidUnitItem(Block block, Properties properties) {
    super(block, properties);
  }

  @Override
  public int getBarColor(ItemStack stack) {
    return super.getBarColor(stack);
  }

  @Override
  public int getBarWidth(ItemStack stack) {
    FluidStack fs = getStoredFluid(stack, RegistryUtil.registryAccess());
    int amt = fs.getAmount();
    return amt / 10000;
  }

  public static void setStoredFluid(ItemStack stack, FluidStack fluid, HolderLookup.Provider provider) {
    CompoundTag beTag = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag();
    CompoundTag tankTag = new CompoundTag();
    if (!fluid.isEmpty()) {
      tankTag.put("Fluid", fluid.saveOptional(provider));
    }
    beTag.put("FluidTank", tankTag);
    stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(beTag));
  }

  public static FluidStack getStoredFluid(ItemStack stack, HolderLookup.Provider provider) {
    CustomData cd = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
    if (cd.isEmpty()) {
      return FluidStack.EMPTY;
    }
    CompoundTag beTag = cd.copyTag();
    if (!beTag.contains("FluidTank")) {
      return FluidStack.EMPTY;
    }
    CompoundTag tankTag = beTag.getCompound("FluidTank");
    if (tankTag.contains("Fluid")) {
      return FluidStack.parseOptional(provider, tankTag.getCompound("Fluid"));
    }
    return FluidStack.EMPTY;
  }
}
