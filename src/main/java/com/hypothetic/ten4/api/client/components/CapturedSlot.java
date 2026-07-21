package com.hypothetic.ten4.api.client.components;

import com.hypothetic.ten4.api.client.gui.EnhancedGuiGraphics;
import com.hypothetic.ten4.api.client.gui.TextureRegion;
import com.hypothetic.ten4.api.container.ManualSlot;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public class CapturedSlot extends UiComponent {
  private final AbstractContainerScreen<? extends AbstractContainerMenu> screen;
  private ManualSlot backingSlot;
  private int lastX;
  private int lastY;
  private @Nullable TextureRegion texture;

  public CapturedSlot(int x, int y, AbstractContainerScreen<? extends AbstractContainerMenu> menu, ManualSlot backingSlot) {
    super(x, y, 16, 16);
    this.backingSlot = backingSlot;
    this.screen = menu;
  }

  public CapturedSlot withTexture(@Nullable TextureRegion tex) {
    this.texture = tex;
    return this;
  }

  @Override
  public void onRender(EnhancedGuiGraphics g, float pt) {
    super.onRender(g, pt);

    g.draw(texture, x - 1, y - 1, width + 2, height + 2);

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
