package com.hypothetic.ten4.core.client.builtin;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.client.ComponentedContainerScreen;
import com.hypothetic.ten4.api.client.components.CapturedSlot;
import com.hypothetic.ten4.api.client.components.Panel;
import com.hypothetic.ten4.api.client.components.UiComponent;
import com.hypothetic.ten4.api.client.gui.EnhancedGuiGraphics;
import com.hypothetic.ten4.api.client.gui.TextureRegion;
import com.hypothetic.ten4.api.container.AugmentableContainerMenu;
import com.hypothetic.ten4.api.container.ContainerMenu;
import com.hypothetic.ten4.api.container.ManualSlot;
import net.minecraft.network.chat.Component;

import java.util.Objects;

public class AugmentPanel extends Panel {
  public AugmentPanel(ComponentedContainerScreen<ContainerMenu> screen, TextureRegion tabBg, int type) {
    super(tabBg, 91, 49, type);
    ContainerMenu menu = screen.getMenu();

    if (menu instanceof AugmentableContainerMenu acm) {
      for (ManualSlot slot : acm.getAugmentSlots()) {
        addChild(BuiltinComponents.capturedSlot(-1, -1, screen, slot));
      }
    }
  }

  @Override
  protected TextureRegion getIcon() {
    return Objects.requireNonNull(TextureRegion.of(BuiltinComponents.TEXTURE, 141, 185, 11, 11));
  }

  @Override
  protected Component getTitle() {
    return Component.translatable(Ten4.lang("misc.augment_label"));
  }

  @Override
  public void onTick() {
    super.onTick();
    int i = 0;
    for (UiComponent component : children) {
      if (component instanceof CapturedSlot slot) {
        slot.backingSlot().setActive(fullyOpen);
        slot.setSemanticX(11 + i * 18);
        slot.setSemanticY(23);
        i++;
      }
    }
  }
}
