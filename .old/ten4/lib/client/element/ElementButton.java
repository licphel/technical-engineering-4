package ten4.lib.client.element;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import ten4.util.ComponentHelper;
import ten4.util.RenderHelper;

import java.util.List;

public class ElementButton extends ElementBase {

  public boolean state;
  B_PACK run_met;
  Component text;
  boolean nc;

  public ElementButton(int x, int y, int width, int height, int xOff, int yOff, ResourceLocation resourceLocation, B_PACK run) {

    super(x, y, width, height, xOff, yOff, resourceLocation);

    run_met = run;
  }

  public void setTxt(String... key) {

    text = ComponentHelper.translated(ComponentHelper.GOLD, key);
  }

  public ElementButton withNoChange() {
    nc = true;
    return this;
  }

  @Override
  public void draw(GuiGraphics matrixStack) {

    if (nc) {
      RenderHelper.render(matrixStack, x, y, width, height, textureW, textureH, xOff, yOff, resourceLocation);
      return;
    }

    if (hanging) {
      if (state) {
        RenderHelper.render(matrixStack, x, y, width, height, textureW, textureH, xOff, yOff + height * 3, resourceLocation);
      } else {
        RenderHelper.render(matrixStack, x, y, width, height, textureW, textureH, xOff, yOff + height, resourceLocation);
      }
    } else {
      if (state) {
        RenderHelper.render(matrixStack, x, y, width, height, textureW, textureH, xOff, yOff + height * 2, resourceLocation);
      } else {
        RenderHelper.render(matrixStack, x, y, width, height, textureW, textureH, xOff, yOff, resourceLocation);
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
    run_met.click();
  }

  public interface B_PACK {

    void click();
  }
}
