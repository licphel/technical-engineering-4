package com.hypothetic.ten4.api.client.components;

import com.hypothetic.ten4.api.client.gui.EnhancedGuiGraphics;
import net.minecraft.client.gui.GuiGraphics;

public class ButtonFilled extends Button {
  private final int color;

  public ButtonFilled(int x, int y, int w, int h, int color) {
    super(x, y, w, h);
    this.color = color;
  }

  private static int brighter(int rgb) {
    int r = Math.min(255, ((rgb >> 16) & 0xFF) + 40);
    int g = Math.min(255, ((rgb >> 8) & 0xFF) + 40);
    int b = Math.min(255, (rgb & 0xFF) + 40);
    return 0xFF000000 | (r << 16) | (g << 8) | b;
  }

  @Override
  public void onRender(EnhancedGuiGraphics g, float pt) {
    GuiGraphics graphics = g.inner();
    int c = hovering ? brighter(color) : color;
    graphics.fill(x, y, x + width, y + height, c);
  }
}
