package com.hypothetic.ten4.api.client.components;

import com.hypothetic.ten4.api.client.gui.EnhancedGuiGraphics;
import com.hypothetic.ten4.api.client.gui.TextureRegion;
import com.hypothetic.ten4.util.DisplayHelper;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class GaugeFluid extends UiComponent {
  protected final Supplier<FluidStack> stackSupplier;
  protected final IntSupplier capacity;
  protected final int innerW;
  protected final int innerH;
  protected @Nullable TextureRegion emptyTex;
  protected @Nullable TextureRegion overlayTex;

  public GaugeFluid(int x, int y, int w, int h, int innerW, int innerH, Supplier<FluidStack> stackSupplier, IntSupplier capacity) {
    super(x, y, w, h);
    this.stackSupplier = stackSupplier;
    this.capacity = capacity;
    this.innerW = innerW;
    this.innerH = innerH;
  }

  public GaugeFluid withTexture(@Nullable TextureRegion emptyTex, @Nullable TextureRegion overlayTex) {
    this.emptyTex = emptyTex;
    this.overlayTex = overlayTex;
    return this;
  }

  @Override
  public void onRender(EnhancedGuiGraphics g, float pt) {
    FluidStack stack = stackSupplier.get();
    if (stack == null) {
      stack = FluidStack.EMPTY;
    }

    int e = stack.getAmount(), me = capacity.getAsInt();
    float frac = me > 0 ? (float) e / me : 0;
    int fill = Math.round(innerH * frac);

    g.draw(emptyTex, x, y, width, height);

    if (fill > 0) {
      int ox = Math.round((width - innerW) / 2.0F);
      int oy = Math.round((height - innerH) / 2.0F);
      g.drawFluid(stack.getFluid(), x + ox, y - fill + oy + innerH, innerW, fill, false);
    }

    g.draw(overlayTex, x, y, width, height);
  }

  @Override
  public void onCollectingTooltips(List<Component> tooltips) {
    super.onCollectingTooltips(tooltips);

    FluidStack stack = stackSupplier.get();
    if (stack == null) {
      stack = FluidStack.EMPTY;
    }

    if (!stack.isEmpty()) {
      tooltips.add(stack.getHoverName());
    }

    tooltips.add(DisplayHelper.getMB(stack.getAmount(), capacity.getAsInt()));
  }
}
