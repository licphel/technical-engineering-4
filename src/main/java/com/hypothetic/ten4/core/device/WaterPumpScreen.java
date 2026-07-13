package com.hypothetic.ten4.core.device;

import com.hypothetic.ten4.api.client.DeviceScreen;
import com.hypothetic.ten4.api.client.builtin.BuiltinComponents;
import com.hypothetic.ten4.api.container.ContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class WaterPumpScreen extends DeviceScreen {
  public WaterPumpScreen(ContainerMenu menu, Inventory inv, Component title, ResourceLocation bg) {
    super(menu, inv, title, bg);
  }

  @Override
  protected void buildElements() {
    super.buildElements();

    add(BuiltinComponents.energyGauge(9, 18, menu.fieldsReader()));
    add(BuiltinComponents.fluidGauge(79, 18, menu.fieldsReader(), WaterPumpBlockEntity.TANK_0));
  }
}
