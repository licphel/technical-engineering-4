package com.hypothetic.ten4.lib.client.components;

import com.hypothetic.ten4.lib.client.render.gui.EnhancedGuiGraphics;
import com.hypothetic.ten4.lib.container.ManualSlot;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class CapturedSlot extends UiComponent {
  private final AbstractContainerScreen<? extends AbstractContainerMenu> screen;
  private ManualSlot backingSlot;
  private int lastX;
  private int lastY;

  public CapturedSlot(int x, int y, AbstractContainerScreen<? extends AbstractContainerMenu> menu, ManualSlot backingSlot) {
    super(x, y, 16, 16);
    this.backingSlot = backingSlot;
    this.screen = menu;
  }

  @Override
  public void onRender(EnhancedGuiGraphics g, float pt) {
    super.onRender(g, pt);

    if (lastX != x || lastY != y) {
      backingSlot = backingSlot.replace(screen.getMenu(), x - screen.getGuiLeft(), y - screen.getGuiTop());
    }

    lastX = x;
    lastY = y;
  }

  public ManualSlot backingSlot() {
    return backingSlot;
  }
}
