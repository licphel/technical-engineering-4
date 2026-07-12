package com.hypothetic.ten4.api.client.components;

import com.hypothetic.ten4.api.client.gui.EnhancedGuiGraphics;
import com.hypothetic.ten4.api.client.gui.TextureRegion;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntSupplier;

public class GaugeHorizontal extends UiComponent {
  protected final IntSupplier partial;
  protected final IntSupplier full;
  protected @Nullable TextureRegion emptyTex;
  protected @Nullable TextureRegion fullTex;

  public GaugeHorizontal(int x, int y, int w, int h, IntSupplier partial, IntSupplier full) {
    super(x, y, w, h);
    this.partial = partial;
    this.full = full;
  }

  public GaugeHorizontal withTexture(@Nullable TextureRegion emptyTex, @Nullable TextureRegion fullTex) {
    this.emptyTex = emptyTex;
    this.fullTex = fullTex;
    return this;
  }

  @Override
  public void onRender(EnhancedGuiGraphics g, float pt) {
    int e = partial.getAsInt(), me = full.getAsInt();
    float frac = me > 0 ? (float) e / me : 0;
    int fill = Math.round(width * frac);

    g.draw(emptyTex, x, y, width, height);

    if (fill > 0 && fullTex != null) {
      int fillUV = Math.round(fullTex.width() * frac);
      g.draw(fullTex, x, y, fill, height, 0, 0, fillUV, fullTex.height());
    }
  }
}
