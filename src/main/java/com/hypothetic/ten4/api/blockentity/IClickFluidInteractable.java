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

/**
 * A block entity that responds to right-click with a fluid container (bucket,
 * tank item, etc.). Implementers expose which handler receives deposited fluid
 * (input tanks) and which supplies extracted fluid (output tanks); the default
 * flow handles the rest via {@link FluidUtil}.
 *
 * <p>Deposit is tried first; if the held fluid was accepted, extraction is
 * skipped. Otherwise the output handler is drained into the held container,
 * falling back to the input handler when the output has nothing to give.
 *
 * <p>Follows CoFH's pattern: {@code tryXxxAndStow} into the player inventory,
 * then the resulting container is written back into the hand with
 * {@link Player#setItemInHand} so the client's held item syncs.
 */
public interface IClickFluidInteractable {
  /**
   * @return handler that receives deposited fluid (e.g. input/fuel tanks).
   */
  IFluidHandler getFluidInputHandler();

  /**
   * @return handler that supplies extractable fluid (e.g. output/byproduct tanks).
   */
  IFluidHandler getFluidOutputHandler();

  /**
   * Main entry, called by the block on right-click.
   *
   * @return true if the click was consumed by a fluid transfer
   */
  default boolean tryInteractFluid(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, BlockHitResult hit) {
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

  /** Deposits the held container's fluid into the input handler. */
  default boolean deposit(ItemStack stack, Player player, InteractionHand hand) {
    FluidActionResult result = FluidUtil.tryEmptyContainerAndStow(stack, getFluidInputHandler(), new InvWrapper(player.getInventory()),
        Integer.MAX_VALUE, player, true);
    if (result.isSuccess()) {
      player.setItemInHand(hand, result.getResult());
      return true;
    }
    return false;
  }

  /**
   * Fills the held container from the output handler; falls back to the input
   * handler when the output has nothing to give (e.g. taking fuel back).
   */
  default boolean extract(ItemStack stack, Player player, InteractionHand hand) {
    if (fillFrom(getFluidOutputHandler(), stack, player, hand)) {
      return true;
    }
    return fillFrom(getFluidInputHandler(), stack, player, hand);
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
