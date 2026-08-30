package com.hypothetic.ten4.core.item;

import com.hypothetic.ten4.core.registry.config.ModConfigs;
import com.hypothetic.ten4.util.DisplayUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class EnergyUnitItem extends BlockItem {
  public EnergyUnitItem(Block block, Properties properties) {
    super(block, properties);
  }

  public static int getStoredEnergy(ItemStack stack) {
    CustomData cd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
    if (cd.isEmpty()) {
      return 0;
    }
    return cd.copyTag().getInt("Energy");
  }

  public static void setStoredEnergy(ItemStack stack, int energy) {
    CompoundTag beTag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    beTag.putInt("Energy", energy);
    stack.set(DataComponents.CUSTOM_DATA, CustomData.of(beTag));
  }

  public static int getEnergyCapacity(ItemStack stack) {
    CustomData cd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
    if (!cd.isEmpty()) {
      int cap = cd.copyTag().getInt("EnergyCapacity");
      if (cap > 0) {
        return cap;
      }
    }
    return ModConfigs.COMMON.others.energyUnitCapacity.get();
  }

  @Override
  public boolean isBarVisible(ItemStack stack) {
    return getBarWidth(stack) > 0;
  }

  @Override
  public int getBarWidth(ItemStack stack) {
    int cap = getEnergyCapacity(stack);
    if (cap <= 0) {
      return 0;
    }
    return Math.round(13.0f * getStoredEnergy(stack) / cap);
  }

  @Override
  public int getBarColor(ItemStack stack) {
    return Mth.color(0.4F, 1.0F, 0.5F);
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> lst, TooltipFlag f) {
    super.appendHoverText(stack, ctx, lst, f);

    int energy = getStoredEnergy(stack);
    int ec = getEnergyCapacity(stack);

    if (energy > 0) {
      lst.add(DisplayUtil.forgeEnergy(energy, ec).copy().withStyle(ChatFormatting.GRAY));
    }
  }
}
