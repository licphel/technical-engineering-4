package ten4.lib.client.element;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import ten4.util.ComponentHelper;
import ten4.util.RenderHelper;

import java.util.List;

public class ElementBar extends ElementBase {

  static final int barSize = 21;
  static final int barMax = 90;
  public boolean state;
  int h = barSize;
  int wait;
  int bw;
  Component text;

  public ElementBar(int xr, int y, int xOff, int yOff, ResourceLocation resourceLocation) {

    super(xr, y, barSize, barSize, xOff, yOff, resourceLocation);
    bw = barSize;
  }

  public ElementBar(int xr, int y, int h, int xOff, int yOff, ResourceLocation resourceLocation) {

    super(xr, y, barSize, h, xOff, yOff, resourceLocation);
    bw = barSize;
    this.h = h;
  }

  public void setTxt(String key) {

    text = ComponentHelper.translated(ComponentHelper.GOLD, key);
  }

  @Override
  public void draw(GuiGraphics matrixStack) {

    RenderHelper.bindTexture(resourceLocation);
    RenderHelper.render(matrixStack, x - bw, y, bw, h, textureW, textureH, xOff, yOff, resourceLocation);

    wait--;
    if (state) {
      if (bw < barMax) {
        bw = Math.min(barMax, bw + 6);
      } else {
        drawAdd(matrixStack);
      }
    } else {
      if (bw > barSize) {
        bw = Math.max(barSize, bw - 6);
      } else {
        bw = barSize;
      }
    }
  }

  @Override
  public void addToolTip(List<Component> tooltips) {
    if (text != null) {
      tooltips.add(text);
    }
  }

  @Override
  public void onMouseClicked(int mouseX, int mouseY) {
    if (wait <= 0) {
      state = !state;
      wait = 4;
    }
  }

  @Override
  public boolean checkInstr(int mouseX, int mouseY) {

    return (mouseX >= x - bw && mouseY >= y && mouseX <= x - bw + barSize && mouseY <= y + height) || (mouseX >= x - barSize && mouseY >= y && mouseX <= x && mouseY <= y + height);
  }

  public boolean isOpen() {

    return bw >= barMax;
  }

  public void drawAdd(GuiGraphics s) {

  }
}
