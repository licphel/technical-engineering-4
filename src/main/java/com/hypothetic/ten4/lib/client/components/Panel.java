package com.hypothetic.ten4.lib.client.components;

import com.hypothetic.ten4.lib.client.render.gui.EnhancedGuiGraphics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;

public class Panel extends UiComponent {
  private static final float ANIM_SPEED = 0.4F;

  private final UiComponent button;
  private final int expandedWidth, expandedHeight;
  private final int collapsedWidth, collapsedHeight;
  private final int gap;
  private float progress;
  private boolean expanding;
  private boolean expandLeft;

  public Panel(UiComponent button, int expandedW, int expandedH, int gap) {
    super(button.getX(), button.getY(), expandedW, expandedH);
    this.button = button;
    addChild(button);
    this.expandedWidth = expandedW;
    this.expandedHeight = expandedH;
    this.collapsedWidth = button.getWidth();
    this.collapsedHeight = button.getHeight();
    this.gap = gap;
    this.progress = 0;
  }

  public Panel expandLeft() {
    this.expandLeft = true;
    return this;
  }

  private int bodyWidth() {
    return expandLeft ? expandedWidth - collapsedWidth - gap : expandedWidth;
  }

  private int bodyStartX() {
    return expandLeft ? x - gap - bodyWidth() : x + collapsedWidth + gap;
  }

  public boolean isExpanded() {
    return progress >= 1;
  }

  public UiComponent button() {
    return button;
  }

  public int currentHeight() {
    return (int) (collapsedHeight + (expandedHeight - collapsedHeight) * progress);
  }

  public void toggle() {
    expanding = !expanding;
    if (expanding) {
      progress = Math.max(0.01F, progress);
    }
  }

  @Override
  public void onRender(EnhancedGuiGraphics g, float pt) {
    GuiGraphics gg = g.inner();
    button.onRender(g, pt);

    if (progress <= 0) {
      return;
    }

    int bodyW = bodyWidth();
    int bodyX = bodyStartX();
    int bodyH = expandedHeight;
    int visibleW = Math.max(1, (int) (bodyW * progress));
    int visibleH = Math.max(1, (int) (bodyH * progress));

    if (expandLeft) {
      bodyX += bodyW - visibleW;
    }

    gg.enableScissor(bodyX, y, bodyX + visibleW, y + visibleH);
    renderBody(g, bodyX, y, bodyW, bodyH);
    gg.disableScissor();

    if (expanding && progress < 1) {
      progress = Math.min(1, progress + ANIM_SPEED * pt);
    } else if (!expanding && progress > 0f) {
      progress = Math.max(0, progress - ANIM_SPEED * pt);
    }

    button.setVisible(false);
    super.onRender(g, pt);
  }

  @Override
  public void onTick() {
    // Do not do animation in tick - it lags.
    super.onTick();
  }

  @Override
  public void onMouseClicked(int mx, int my, int button) {
    if (this.button.isMouseHovering(mx, my)) { // Only to click at the corner can hide the panel
      toggle();
    }

    super.onMouseClicked(mx, my, button);
  }

  @Override
  public boolean isMouseHovering(int mx, int my) {
    int ex = effectiveX();
    return mx >= ex && my >= y && mx <= ex + width && my <= y + currentHeight();
  }

  public Rect2i getTakeUp() {
    int ex = effectiveX();
    return new Rect2i(ex, y, x + width - ex, currentHeight());
  }

  private int effectiveX() {
    return expandLeft ? x - (int) ((bodyWidth() + gap) * progress) : x;
  }

  protected void renderBody(EnhancedGuiGraphics g, int bx, int by, int bw, int bh) {
    g.inner().fill(bx, by, bx + bw, by + bh, 0xCC222244);
    g.inner().renderOutline(bx, by, bw, bh, 0xFFFFFFFF);
  }
}
