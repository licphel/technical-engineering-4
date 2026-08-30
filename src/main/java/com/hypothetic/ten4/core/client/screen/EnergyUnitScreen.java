package com.hypothetic.ten4.core.client.screen;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.client.ComponentedContainerScreen;
import com.hypothetic.ten4.api.container.ContainerMenu;
import com.hypothetic.ten4.core.client.builtin.BuiltinComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EnergyUnitScreen extends ComponentedContainerScreen<ContainerMenu> {
  public EnergyUnitScreen(ContainerMenu menu, Inventory inv, Component title) {
    super(menu, inv, title);
  }

  @Override
  protected ResourceLocation getBackground() {
    return Ten4.id("textures/gui/energy_unit.png");
  }

  @Override
  protected void buildElements() {
    add(BuiltinComponents.standardDeviceUI(this));
    add(BuiltinComponents.energyGauge(84, 18, menu.fieldsReader()));
  }
}
