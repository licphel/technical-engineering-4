package com.hypothetic.ten4.lib.client.components;

import com.hypothetic.ten4.lib.client.render.gui.EnhancedGuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class UiComponent {
  public boolean hovering;
  protected int semanticX;
  protected int semanticY;
  protected int x;
  protected int y;
  protected int width;
  protected int height;
  protected boolean visible = true;
  protected List<UiComponent> children = new ArrayList<>();

  public UiComponent(int x, int y, int w, int h) {
    this.x = semanticX = x;
    this.y = semanticY = y;
    this.width = w;
    this.height = h;
  }

  public UiComponent addChild(UiComponent child) {
    children.add(child);
    return this;
  }

  public UiComponent removeChild(UiComponent child) {
    children.remove(child);
    return this;
  }

  public void onRescaled(int i, int j) {
    x = semanticX + i;
    y = semanticY + j;

    for (UiComponent child : children) {
      child.onRescaled(x, y);
    }
  }

  public void onRender(EnhancedGuiGraphics g, float pt) {
    for (UiComponent child : children) {
      if (child.isVisible()) {
        child.onRender(g, pt);
      }
    }
  }

  public void onTick() {
    for (UiComponent child : children) {
      child.onTick();
    }
  }

  public void onCollectingTooltips(List<Component> tooltips) {
    for (UiComponent child : children) {
      if (child.isVisible()) {
        child.onCollectingTooltips(tooltips);
      }
    }
  }

  public void onMouseClicked(int mouseX, int mouseY, int button) {
    for (UiComponent child : children) {
      if (child.isVisible() && child.isMouseHovering(mouseX, mouseY)) {
        child.onMouseClicked(mouseX, mouseY, button);
      }
    }
  }

  public void onMouseMotion(int mouseX, int mouseY) {
    hovering = isMouseHovering(mouseX, mouseY);

    for (UiComponent child : children) {
      child.onMouseMotion(mouseX, mouseY);
    }
  }

  public boolean isMouseHovering(int mouseX, int mouseY) {
    boolean flag = mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;

    if (flag) {
      return true;
    }

    for (UiComponent child : children) {
      if (child.isMouseHovering(mouseX, mouseY)) {
        return true;
      }
    }
    return false;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean v) {
    this.visible = v;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public void setX(int x) {
    this.x = x;
  }

  public void setY(int y) {
    this.y = y;
  }

  public void setSemanticX(int semanticX) {
    int delta = semanticX - this.semanticX;
    this.semanticX = semanticX;
    this.x += delta;
  }

  public void setSemanticY(int semanticY) {
    int delta = semanticY - this.semanticY;
    this.semanticY = semanticY;
    this.y += delta;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public Rect2i getTakeUp() {
    if (children.isEmpty()) {
      return new Rect2i(x, y, width, height);
    }

    int minX = getX();
    int maxX = getX() + getWidth();
    int minY = getY();
    int maxY = getY() + getHeight();

    for (UiComponent child : children) {
      Rect2i r = child.getTakeUp();
      minX = Math.min(minX, r.getX());
      minY = Math.min(minY, r.getY());
      maxX = Math.max(maxX, r.getX() + r.getWidth());
      maxY = Math.max(maxY, r.getY() + r.getHeight());
    }
    return new Rect2i(minX, minY, maxX - minX, maxY - minY);
  }
}
