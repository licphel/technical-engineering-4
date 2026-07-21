package com.hypothetic.ten4.core.registry;

import com.hypothetic.ten4.Ten4;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;

@EventBusSubscriber(modid = Ten4.ID)
public final class ModFuels {
  @SubscribeEvent
  public static void onRegisterFuels(FurnaceFuelBurnTimeEvent event) {
    ItemStack stack = event.getItemStack();

    if (stack.is(ModItems.OIL_SAND)) {
      event.setBurnTime(500);
    }
  }
}
