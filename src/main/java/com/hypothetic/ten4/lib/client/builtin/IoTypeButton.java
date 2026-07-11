package com.hypothetic.ten4.lib.client.builtin;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.lib.client.components.Button;
import com.hypothetic.ten4.lib.client.render.gui.EnhancedGuiGraphics;
import com.hypothetic.ten4.lib.client.render.gui.TextureRegion;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

class IoTypeButton extends Button {
  final IoConfigState state;

  IoTypeButton(int x, int y, int w, int h, IoConfigState state) {
    super(x, y, w, h);
    this.state = state;
    setAction(() -> state.type = (state.type + 1) % 3);
  }

  @Override
  public void onRender(EnhancedGuiGraphics g, float pt) {
    super.onRender(g, pt);

    int u = state.type * 12;
    g.draw(TextureRegion.of(BuiltinComponents.PANELS, u, 176, 12, 12), x, y, width, height);
  }

  @Override
  public void onCollectingTooltips(List<Component> tooltips) {
    super.onCollectingTooltips(tooltips);

    MutableComponent mc = Component.translatable(Ten4.getLangKey("misc.current_configuring"));
    mc.append(state.getComponent());
    tooltips.add(mc);
  }
}
