package com.hypothetic.ten4.api.client.components;

import com.hypothetic.ten4.api.client.gui.EnhancedGuiGraphics;
import net.minecraft.util.Mth;

public class ScrollableTextView extends UiComponent {
  private static final int SCROLLBAR_WIDTH = 3;
  private static final int SCROLL_SPEED = 10;
  private final ContentRenderer renderContent;
  private double scrollY;
  private int contentHeight = -1;
  private boolean showScrollbar = true;
  private int padding = 4;

  public ScrollableTextView(int x, int y, int w, int h, ContentRenderer renderContent) {
    super(x, y, w, h);
    this.renderContent = renderContent;
  }

  public ScrollableTextView padding(int px) { this.padding = px; return this; }
  public ScrollableTextView noScrollbar() { this.showScrollbar = false; return this; }

  @Override
  public void onRender(EnhancedGuiGraphics g, float pt) {
    if (!visible) return;
    int sbW = showScrollbar ? SCROLLBAR_WIDTH + 1 : 0;
    int innerX = x + padding;
    int innerW = width - padding * 2 - sbW;
    int innerY = y + padding;
    int innerH = height - padding * 2;
    g.inner().enableScissor(innerX, innerY, innerX + innerW, innerY + innerH);
    contentHeight = renderContent.render(g, innerX, innerY - (int) scrollY);
    g.inner().disableScissor();

    if (showScrollbar && contentHeight > innerH) {
      int sbX = x + width - SCROLLBAR_WIDTH - 1 - padding;
      int sbH = Math.max(16, (int) ((float) innerH / contentHeight * innerH));
      int sbY = innerY + (int) ((float) scrollY / contentHeight * (innerH - sbH));
      g.inner().fill(sbX, sbY, sbX + SCROLLBAR_WIDTH, sbY + sbH, 0x88FFFFFF);
    }

    super.onRender(g, pt);
  }

  @Override
  public boolean onMouseScrolled(double mx, double my, double delta) {
    int innerH = height - padding * 2;
    if (contentHeight <= innerH) return false;
    scrollY = Mth.clamp(scrollY - delta * SCROLL_SPEED, 0, Math.max(0, contentHeight - innerH));
    return true;
  }

  @FunctionalInterface
  public interface ContentRenderer {
    int render(EnhancedGuiGraphics g, int originX, int topY);
  }
}
