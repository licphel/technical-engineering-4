package ten4.lib.client.element;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import ten4.lib.tile.CmContainerMachine;
import ten4.lib.wrapper.IntArrayCm;
import ten4.util.ComponentHelper;
import ten4.util.RenderHelper;

import java.util.List;

public class ElementProgress extends ElementBase {

  double p;
  boolean dv;
  int ie;
  int je;

  public ElementProgress(int x, int y, int width, int height, int xOff, int yOff, ResourceLocation resourceLocation) {

    super(x, y, width, height, xOff, yOff, resourceLocation);
  }

  public ElementProgress(int x, int y, int width, int height, int xOff, int yOff, ResourceLocation resourceLocation, boolean display) {

    super(x, y, width, height, xOff, yOff, resourceLocation);
    dv = display;
  }

  @Override
  public void draw(GuiGraphics matrixStack) {

    RenderHelper.render(matrixStack, x, y, width, height, textureW, textureH, xOff, yOff, resourceLocation);

    RenderHelper.bindTexture(resourceLocation);
    RenderHelper.render(matrixStack, x, y, (int) (p * width), height, textureW, textureH, xOff, yOff + height, resourceLocation);
  }

  @Override
  public void addToolTip(List<Component> tooltips) {
    if (dv) {
      tooltips.add(ComponentHelper.make((int) (p * 100) + "%"));
    }
  }

  public void setEs(int i1, int i2, CmContainerMachine c) {

    IntArrayCm ia = c.data;
    ie = ia.get(i1);
    je = ia.get(i2);
  }

  public double getPer() {
    return p;
  }

  public void setPer(double per) {
    p = per;
  }
}
