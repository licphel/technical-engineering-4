package com.hypothetic.ten4.api.client;

import com.hypothetic.ten4.api.client.builtin.BuiltinComponents;
import com.hypothetic.ten4.api.client.components.PanelLayout;
import com.hypothetic.ten4.api.container.ContainerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DeviceScreen extends ComponentScreen<ContainerMenu> {
  private final ResourceLocation bg;
  private final PanelLayout leftPanels;
  private final PanelLayout rightPanels;

  public DeviceScreen(ContainerMenu menu, Inventory inv, Component title, ResourceLocation bg) {
    super(menu, inv, title);
    this.bg = bg;
    this.imageWidth = 176;
    this.imageHeight = 166;

    this.leftPanels = BuiltinComponents.leftPanels();
    this.rightPanels = BuiltinComponents.rightPanels(imageWidth);
    leftPanels.addPanel(BuiltinComponents.infoPanel(this));
    leftPanels.addPanel(BuiltinComponents.sigModePanel(this));
    rightPanels.addPanel(BuiltinComponents.ioPanel(this));
    rightPanels.addPanel(BuiltinComponents.augmentPanel(this));
  }

  @Override
  protected void renderBg(GuiGraphics g, float pt, int mx, int my) {
    g.blit(bg, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    super.renderBg(g, pt, mx, my);
  }

  @Override
  protected void buildElements() {
    add(leftPanels);
    add(rightPanels);
  }
}
