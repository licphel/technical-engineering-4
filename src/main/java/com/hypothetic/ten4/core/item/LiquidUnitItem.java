package com.hypothetic.ten4.core.item;

import com.hypothetic.ten4.core.registry.config.ModConfigs;
import com.hypothetic.ten4.util.DisplayUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class LiquidUnitItem extends BlockItem {
  public LiquidUnitItem(Block block, Properties properties) {
    super(block, properties);
  }

  public static FluidStack getStoredFluid(ItemStack stack) {
    CustomData cd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
    if (cd.isEmpty()) {
      return FluidStack.EMPTY;
    }
    ListTag fluids = cd.copyTag().getCompound("FluidTank").getList("Fluids", CompoundTag.TAG_COMPOUND);
    if (fluids.isEmpty()) {
      return FluidStack.EMPTY;
    }
    return parseFluid(fluids.getCompound(0).getCompound("Fluid"));
  }

  public static void setStoredFluid(ItemStack stack, FluidStack fluid) {
    CompoundTag beTag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    CompoundTag tankTag = beTag.getCompound("FluidTank");
    ListTag fluids = tankTag.getList("Fluids", CompoundTag.TAG_COMPOUND);
    if (fluids.isEmpty()) {
      CompoundTag entry = new CompoundTag();
      entry.putInt("Tank", 0);
      fluids.add(entry);
    }
    if (fluid.isEmpty()) {
      fluids.getCompound(0).remove("Fluid");
    } else {
      CompoundTag fluidTag = new CompoundTag();
      fluidTag.putString("id", BuiltInRegistries.FLUID.getKey(fluid.getFluid()).toString());
      fluidTag.putInt("amount", fluid.getAmount());
      fluids.getCompound(0).put("Fluid", fluidTag);
    }
    tankTag.put("Fluids", fluids);
    beTag.put("FluidTank", tankTag);
    stack.set(DataComponents.CUSTOM_DATA, CustomData.of(beTag));
  }

  public static int getCapacity(ItemStack stack) {
    CustomData cd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
    if (!cd.isEmpty()) {
      int cap = cd.copyTag().getCompound("FluidTank").getInt("Capacity");
      if (cap > 0) {
        return cap;
      }
    }
    return ModConfigs.COMMON.others.liquidUnitCapacity.get();
  }

  public static void setCapacity(ItemStack stack, int capacity) {
    CompoundTag beTag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    CompoundTag tankTag = beTag.getCompound("FluidTank");
    tankTag.putInt("Capacity", capacity);
    beTag.put("FluidTank", tankTag);
    stack.set(DataComponents.CUSTOM_DATA, CustomData.of(beTag));
  }

  private static FluidStack parseFluid(CompoundTag tag) {
    if (!tag.contains("id")) {
      return FluidStack.EMPTY;
    }
    ResourceLocation id = ResourceLocation.tryParse(tag.getString("id"));
    if (id == null) {
      return FluidStack.EMPTY;
    }
    Registry<Fluid> fluids = BuiltInRegistries.FLUID;
    if (!fluids.containsKey(id)) {
      return FluidStack.EMPTY;
    }
    Fluid fluid = fluids.get(id);
    if (fluid == Fluids.EMPTY) {
      return FluidStack.EMPTY;
    }
    int amount = tag.getInt("amount");
    if (amount <= 0) {
      return FluidStack.EMPTY;
    }
    return new FluidStack(fluid, amount);
  }

  @Override
  public boolean isBarVisible(ItemStack stack) {
    return getBarWidth(stack) > 0;
  }

  @Override
  public int getBarWidth(ItemStack stack) {
    int cap = getCapacity(stack);
    if (cap <= 0) {
      return 0;
    }
    return Math.round(13.0F * getStoredFluid(stack).getAmount() / cap);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public int getBarColor(ItemStack stack) {
    FluidStack fs = getStoredFluid(stack);
    Level level = Minecraft.getInstance().level;
    if (level == null) {
      return 0xFFFFFFFF;
    }
    int tint = IClientFluidTypeExtensions.of(fs.getFluid()).getTintColor();
    if (tint != 0xFFFFFFFF) {
      return tint;
    }
    return fs.getFluid().defaultFluidState().createLegacyBlock().getMapColor(level, BlockPos.ZERO).col;
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> lst, TooltipFlag f) {
    super.appendHoverText(stack, ctx, lst, f);

    FluidStack fs = getStoredFluid(stack);

    if (!fs.isEmpty()) {
      lst.add(Component.translatable(fs.getDescriptionId())
          .append(" ")
          .append(DisplayUtil.milliBucket(fs.getAmount(), getCapacity(stack)))
          .withStyle(ChatFormatting.GRAY));
    }
  }
}
