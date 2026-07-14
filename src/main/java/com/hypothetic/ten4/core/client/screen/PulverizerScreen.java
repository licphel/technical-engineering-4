package com.hypothetic.ten4.core.client.screen;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.client.ComponentedContainerScreen;
import com.hypothetic.ten4.api.client.builtin.BuiltinComponents;
import com.hypothetic.ten4.api.container.ContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PulverizerScreen extends ComponentedContainerScreen<ContainerMenu> {
  public PulverizerScreen(ContainerMenu menu, Inventory inv, Component title) {
    super(menu, inv, title);
  }

  @Override
  protected void buildElements() {
    add(BuiltinComponents.defaultPanels(this));
    add(BuiltinComponents.energyGauge(9, 18, menu.fieldsReader()));
    add(BuiltinComponents.progressGauge(68, 35, menu.fieldsReader()));
  }

  @Override
  protected ResourceLocation getBackground() {
    return Ten4.id("textures/gui/pulverizer.png");
  }
}
