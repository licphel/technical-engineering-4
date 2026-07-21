package com.hypothetic.ten4.core.client.builtin;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.client.ComponentedContainerScreen;
import com.hypothetic.ten4.api.client.components.Panel;
import com.hypothetic.ten4.api.client.gui.TextureRegion;
import com.hypothetic.ten4.api.container.ContainerMenu;
import com.hypothetic.ten4.api.container.sync.SyncedFieldReader;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.Objects;

import static com.hypothetic.ten4.core.client.builtin.BuiltinComponents.BEEP;

public class SecurityPanel extends Panel {
  public SecurityPanel(ComponentedContainerScreen<ContainerMenu> screen, TextureRegion tabBg, int type) {
    super(tabBg, 91, 41, type);
    SyncedFieldReader reader = screen.getMenu().fieldsReader();
    BlockPos pos = screen.getMenu().getBlockEntity().getBlockPos();
    addChild(new SecurityModeButton(40, 21, 12, 12, reader, pos).withClickSound(BEEP));
  }

  @Override
  protected TextureRegion getIcon() {
    return Objects.requireNonNull(TextureRegion.of(BuiltinComponents.TEXTURE, 152, 185, 11, 11));
  }

  @Override
  protected Component getTitle() {
    return Component.translatable(Ten4.lang("misc.security_label"));
  }
}
