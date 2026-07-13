package com.hypothetic.ten4.registry.client;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.core.device.HeatGeneratorScreen;
import com.hypothetic.ten4.core.device.PulverizerScreen;
import com.hypothetic.ten4.core.device.WaterPumpScreen;
import com.hypothetic.ten4.registry.ModMenus;
import com.hypothetic.ten4.api.container.ContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = Ten4.ID, value = Dist.CLIENT)
public class ModScreens {
  @SubscribeEvent
  public static void registerScreens(RegisterMenuScreensEvent event) {
    event.register(ModMenus.PULVERIZER.get(),
        (ContainerMenu menu, Inventory inv, Component title) -> new PulverizerScreen(menu, inv, title,
            Ten4.id("textures/gui/pulverizer.png")));
    event.register(ModMenus.HEAT_GENERATOR.get(),
        (ContainerMenu menu, Inventory inv, Component title) -> new HeatGeneratorScreen(menu, inv, title,
            Ten4.id("textures/gui/heat_generator.png")));
    event.register(ModMenus.WATER_PUMP.get(),
        (ContainerMenu menu, Inventory inv, Component title) -> new WaterPumpScreen(menu, inv, title,
            Ten4.id("textures/gui/water_pump.png")));
  }
}
