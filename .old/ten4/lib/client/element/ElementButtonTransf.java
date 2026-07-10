package ten4.lib.client.element;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import ten4.lib.tile.option.FaceOption;
import ten4.util.ComponentHelper;
import ten4.util.RenderHelper;

import java.util.List;

public class ElementButtonTransf extends ElementButton {

  public int mode;
  Component text;

  public ElementButtonTransf(int x, int y, int width, int height, int xOff, int yOff, ResourceLocation resourceLocation, B_PACK run) {

    super(x, y, width, height, xOff, yOff, resourceLocation, run);
  }

  public void setTxt(String... key) {

    text = ComponentHelper.translated(ComponentHelper.GOLD, key);
  }

  @Override
  public void draw(GuiGraphics matrixStack) {

    switch (mode) {
      case FaceOption.NONE:
      case FaceOption.OFF:
        RenderHelper.render(matrixStack, x, y, width, height, textureW, textureH, xOff, yOff, resourceLocation);
        break;
      case FaceOption.IN:
        RenderHelper.render(matrixStack, x, y, width, height, textureW, textureH, xOff, yOff + height, resourceLocation);
        break;
      case FaceOption.OUT:
        RenderHelper.render(matrixStack, x, y, width, height, textureW, textureH, xOff, yOff + height * 2, resourceLocation);
        break;
      case FaceOption.BE_IN:
        RenderHelper.render(matrixStack, x, y, width, height, textureW, textureH, xOff, yOff + height * 3, resourceLocation);
        break;
      case FaceOption.BE_OUT:
        RenderHelper.render(matrixStack, x, y, width, height, textureW, textureH, xOff, yOff + height * 4, resourceLocation);
        break;
      case FaceOption.BOTH:
        RenderHelper.render(matrixStack, x, y, width, height, textureW, textureH, xOff, yOff + height * 5, resourceLocation);
        break;
    }
  }

  @Override
  public void addToolTip(List<Component> tooltips) {
    if (text != null) {
      tooltips.add(text);
      tooltips.add(ComponentHelper.translated("ten4.info." + FaceOption.toStr(mode)));
    }
  }
}
