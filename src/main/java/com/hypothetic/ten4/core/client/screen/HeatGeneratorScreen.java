package com.hypothetic.ten4.core.client.screen;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.client.ComponentedContainerScreen;
import com.hypothetic.ten4.api.client.builtin.BuiltinComponents;
import com.hypothetic.ten4.api.container.ContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class HeatGeneratorScreen extends ComponentedContainerScreen<ContainerMenu> {
  public HeatGeneratorScreen(ContainerMenu menu, Inventory inv, Component title) {
    super(menu, inv, title);
  }

  @Override
  protected ResourceLocation getBackground() {
    return Ten4.id("textures/gui/heat_generator.png");
  }

  @Override
  protected void buildElements() {
    add(BuiltinComponents.showMiscs(this));
    add(BuiltinComponents.defaultPanels(this));
    add(BuiltinComponents.energyGauge(120, 18, menu.fieldsReader()));
    add(BuiltinComponents.fuelGauge(80, 36, menu.fieldsReader()));
  }
}
