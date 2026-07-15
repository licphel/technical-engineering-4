package com.hypothetic.ten4.core.client.screen;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.client.ComponentedContainerScreen;
import com.hypothetic.ten4.core.client.builtin.BuiltinComponents;
import com.hypothetic.ten4.api.container.ContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SmelterScreen extends ComponentedContainerScreen<ContainerMenu> {
  public SmelterScreen(ContainerMenu menu, Inventory inv, Component title) {
    super(menu, inv, title);
  }

  @Override
  protected ResourceLocation getBackground() {
    return Ten4.id("textures/gui/smelter.png");
  }

  @Override
  protected void buildElements() {
    add(BuiltinComponents.showMiscs(this));
    add(BuiltinComponents.defaultPanels(this));
    add(BuiltinComponents.energyGauge(9, 18, menu.fieldsReader()));
    add(BuiltinComponents.progressGauge(75, 35, menu.fieldsReader()));
  }
}
