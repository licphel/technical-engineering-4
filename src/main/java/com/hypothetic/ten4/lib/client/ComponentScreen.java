package com.hypothetic.ten4.lib.client;

import com.hypothetic.ten4.lib.client.components.UiComponent;
import com.hypothetic.ten4.lib.client.render.gui.EnhancedGuiGraphics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public abstract class ComponentScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
  protected final List<UiComponent> components = new ArrayList<>();
  protected EnhancedGuiGraphics graphics;

  public ComponentScreen(T menu, Inventory inv, Component title) {
    super(menu, inv, title);
  }

  @Override
  protected void init() {
    super.init();
    components.clear();
    buildElements();
    onRescaled(leftPos, topPos);
  }

  @Override
  public void render(GuiGraphics g, int mx, int my, float pt) {
    super.render(g, mx, my, pt);

    List<Component> tooltips = new LinkedList<>();
    Optional<TooltipComponent> tooltipImage = Optional.empty();

    if (menu.getCarried().isEmpty() && hoveredSlot != null && hoveredSlot.hasItem()) {
      ItemStack itemstack = hoveredSlot.getItem();
      tooltips.addAll(getTooltipFromContainerItem(itemstack));
      tooltipImage = itemstack.getTooltipImage();
    }

    for (UiComponent e : components) {
      if (e.isVisible() && e.isMouseHovering(mx, my)) {
        e.onCollectingTooltips(tooltips);
      }
    }

    if (!tooltips.isEmpty()) {
      g.renderTooltip(font, tooltips, tooltipImage, mx, my);
    }

    onRescaled(leftPos, topPos);
  }

  @Override
  protected void renderBg(GuiGraphics g, float pt, int mx, int my) {
    graphics = new EnhancedGuiGraphics(g);

    for (UiComponent e : components) {
      if (e.isVisible()) {
        e.onRender(graphics, pt);
      }
    }
  }

  @Override
  public boolean mouseClicked(double mx, double my, int button) {
    super.mouseClicked(mx, my, button); // Do not stop event spread chain

    for (UiComponent e : components) {
      if (e.isVisible() && e.hovering) {
        e.onMouseClicked((int) mx, (int) my, button);
        return true;
      }
    }

    return false;
  }

  @Override
  protected boolean hasClickedOutside(double x, double y, int left, int top, int p_97761_) {
    boolean flag = super.hasClickedOutside(x, y, left, top, p_97761_);

    if (!flag) {
      return true;
    }

    for (Rect2i r : getComponentAreas()) {
      if (r.contains((int) x, (int) y)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void containerTick() {
    super.containerTick();
    for (UiComponent e : components) {
      e.onTick();
    }
  }

  protected abstract void buildElements();

  protected void add(UiComponent e) {
    components.add(e);
  }

  public List<Rect2i> getComponentAreas() {
    List<Rect2i> areas = new ArrayList<>();
    for (UiComponent c : components) {
      if (c.isVisible()) {
        areas.addAll(c.getTakeUp());
      }
    }
    return areas;
  }

  public void onRescaled(int x, int y) {
    for (UiComponent e : components) {
      e.onRescaled(x, y);
    }
  }

  @Override
  public void mouseMoved(double mx, double my) {
    for (UiComponent e : components) {
      e.onMouseMotion((int) mx, (int) my);
    }
    super.mouseMoved(mx, my);
  }

  @Override
  public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
    for (UiComponent e : components) {
      if (e.isVisible() && e.hovering) {
        if (e.onMouseScrolled(mx, my, scrollY)) {
          return true;
        }
      }
    }
    return super.mouseScrolled(mx, my, scrollX, scrollY);
  }
}
