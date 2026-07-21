package com.hypothetic.ten4.core.client.builtin;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.client.ComponentedContainerScreen;
import com.hypothetic.ten4.api.client.components.Panel;
import com.hypothetic.ten4.api.client.gui.TextureRegion;
import com.hypothetic.ten4.api.container.ContainerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.Objects;

import static com.hypothetic.ten4.core.client.builtin.BuiltinComponents.BEEP;

public class RedstonePanel extends Panel {
  public RedstonePanel(ComponentedContainerScreen<ContainerMenu> screen, TextureRegion tabBg, int type) {
    super(tabBg, 91, 41, type);
    var reader = screen.getMenu().fieldsReader();
    BlockPos pos = screen.getMenu().getBlockEntity().getBlockPos();
    addChild(new RedstoneModeButton(33, 21, 12, 12, reader, pos).withClickSound(BEEP));
    addChild(new RedstoneComparatorModeButton(48, 21, 12, 12, reader, pos).withClickSound(BEEP));
  }

  @Override
  protected TextureRegion getIcon() {
    return Objects.requireNonNull(TextureRegion.of(BuiltinComponents.TEXTURE, 119, 185, 11, 11));
  }

  @Override
  protected Component getTitle() {
    return Component.translatable(Ten4.lang("misc.redstone_label"));
  }
}
