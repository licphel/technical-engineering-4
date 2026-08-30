package com.hypothetic.ten4.api.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public interface IClickFluidInteractable {
  IFluidHandler getInteractedFluidInputView();

  IFluidHandler getInteractedFluidOutputView();

  default boolean isAbleToInteractFluid() {
    return true;
  }

  default boolean tryInteractFluid(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, BlockHitResult hit) {
    if (!isAbleToInteractFluid()) {
      return false;
    }
    if (hand != InteractionHand.MAIN_HAND || level.isClientSide() || stack.isEmpty()) {
      return false;
    }
    if (FluidUtil.getFluidHandler(stack).isEmpty()) {
      return false;
    }
    if (deposit(stack, player, hand)) {
      return true;
    }
    return extract(stack, player, hand);
  }

  default boolean deposit(ItemStack stack, Player player, InteractionHand hand) {
    FluidActionResult result = FluidUtil.tryEmptyContainerAndStow(stack, getInteractedFluidInputView(), new InvWrapper(player.getInventory()),
        Integer.MAX_VALUE, player, true);
    if (result.isSuccess()) {
      player.setItemInHand(hand, result.getResult());
      return true;
    }
    return false;
  }

  default boolean extract(ItemStack stack, Player player, InteractionHand hand) {
    if (fillFrom(getInteractedFluidOutputView(), stack, player, hand)) {
      return true;
    }
    return fillFrom(getInteractedFluidInputView(), stack, player, hand);
  }

  private boolean fillFrom(IFluidHandler source, ItemStack stack, Player player, InteractionHand hand) {
    FluidActionResult result = FluidUtil.tryFillContainerAndStow(stack, source, new InvWrapper(player.getInventory()),
        Integer.MAX_VALUE, player, true);
    if (result.isSuccess()) {
      player.setItemInHand(hand, result.getResult());
      return true;
    }
    return false;
  }
}
