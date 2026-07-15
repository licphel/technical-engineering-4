package com.hypothetic.ten4.core.registry.client;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.core.client.screen.*;
import com.hypothetic.ten4.core.registry.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = Ten4.ID, value = Dist.CLIENT)
public class ModScreens {
  @SubscribeEvent
  public static void onRegisterScreens(RegisterMenuScreensEvent event) {
    event.register(ModMenus.PULVERIZER.get(), PulverizerScreen::new);
    event.register(ModMenus.PRESS.get(), PressScreen::new);
    event.register(ModMenus.SMELTER.get(), SmelterScreen::new);
    event.register(ModMenus.HEAT_GENERATOR.get(), HeatGeneratorScreen::new);
    event.register(ModMenus.WATER_PUMP.get(), WaterPumpScreen::new);
  }
}
