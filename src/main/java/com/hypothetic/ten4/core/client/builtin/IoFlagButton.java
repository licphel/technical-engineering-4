package com.hypothetic.ten4.core.client.builtin;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.client.components.Button;
import com.hypothetic.ten4.api.client.gui.EnhancedGuiGraphics;
import com.hypothetic.ten4.api.client.gui.TextureRegion;
import com.hypothetic.ten4.api.network.device.DeviceConfigPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.function.BooleanSupplier;

public class IoFlagButton extends Button {
  private final IoFlagReader state;
  private final BlockPos pos;
  private final int flagShift; // 0=eject, 6=extract, 24=strictInput (global)
  private final BooleanSupplier getter;
  private final int u, v;
  private final String langKey;

  public IoFlagButton(int x, int y, int u, int v, IoFlagReader state, BlockPos pos,
                      int flagShift, BooleanSupplier getter, String langKey) {
    super(x, y, 12, 12);
    this.u = u;
    this.v = v;
    this.state = state;
    this.pos = pos;
    this.flagShift = flagShift;
    this.getter = getter;
    this.langKey = langKey;
    setAction(() -> {
      int key = switch (state.type) {
        case 0 -> DeviceConfigPayload.ENERGY_AUTO_FLAGS;
        case 1 -> DeviceConfigPayload.ITEM_AUTO_FLAGS;
        default -> DeviceConfigPayload.FLUID_AUTO_FLAGS;
      };
      int flags = state.autoFlags();
      int bit = flagShift >= 24 ? flagShift : state.activeDir.ordinal() + flagShift;
      int mask = 1 << bit;
      int next = flags ^ mask;
      PacketDistributor.sendToServer(new DeviceConfigPayload(pos, key, next, 0));
    });
    setEnablePressed(false);
  }

  boolean isOn() {
    return getter.getAsBoolean();
  }

  @Override
  public void onRender(EnhancedGuiGraphics g, float pt) {
    super.onRender(g, pt);
    g.draw(TextureRegion.of(BuiltinComponents.PANELS, u + (isOn() ? 12 : 0), v, 12, 12), x, y, width, height);
  }

  @Override
  public void onCollectingTooltips(List<Component> tooltips) {
    super.onCollectingTooltips(tooltips);
    MutableComponent mc = Component.translatable(Ten4.lang("misc." + langKey));
    mc.append(isOn()
        ? Component.translatable(Ten4.lang("misc.enabled"))
        : Component.translatable(Ten4.lang("misc.disabled")));
    tooltips.add(mc);
    tooltips.add(Component.translatable(Ten4.lang("misc." + langKey + ".desc")).withStyle(ChatFormatting.GRAY));
  }

  @Override
  public boolean isVisible() {
    if (flagShift == 24 && state.type == 0) {
      return false;
    }
    return super.isVisible();
  }
}
