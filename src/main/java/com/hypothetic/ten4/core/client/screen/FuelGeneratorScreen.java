package com.hypothetic.ten4.core.client.screen;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.client.ComponentedContainerScreen;
import com.hypothetic.ten4.api.container.ContainerMenu;
import com.hypothetic.ten4.core.blockentity.device.FuelGeneratorBlockEntity;
import com.hypothetic.ten4.core.client.builtin.BuiltinComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FuelGeneratorScreen extends ComponentedContainerScreen<ContainerMenu> {
  public FuelGeneratorScreen(ContainerMenu menu, Inventory inv, Component title) {
    super(menu, inv, title);
  }

  @Override
  protected ResourceLocation getBackground() {
    return Ten4.id("textures/gui/fuel_generator.png");
  }

  @Override
  protected void buildElements() {
    add(BuiltinComponents.standardDeviceUI(this));
    add(BuiltinComponents.fluidGauge(43, 18, menu.fieldsReader(), FuelGeneratorBlockEntity.TANK_0));
    add(BuiltinComponents.energyGauge(120, 18, menu.fieldsReader()));
    add(BuiltinComponents.fuelGauge(80, 36, menu.fieldsReader()));
  }
}
