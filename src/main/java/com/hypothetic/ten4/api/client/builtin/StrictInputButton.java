package com.hypothetic.ten4.api.client.builtin;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.client.components.Button;
import com.hypothetic.ten4.api.client.gui.EnhancedGuiGraphics;
import com.hypothetic.ten4.api.client.gui.TextureRegion;
import com.hypothetic.ten4.api.container.sync.BuiltinSyncedFields;
import com.hypothetic.ten4.api.container.sync.SyncedFieldReader;
import com.hypothetic.ten4.api.network.device.DeviceConfigPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

class StrictInputButton extends Button {
  final SyncedFieldReader reader;

  StrictInputButton(int x, int y, int w, int h, SyncedFieldReader reader, BlockPos pos) {
    super(x, y, w, h);
    this.reader = reader;
    setAction(() -> {
      boolean b = !reader.getBool(BuiltinSyncedFields.STRICT_INPUT);
      PacketDistributor.sendToServer(new DeviceConfigPayload(pos, DeviceConfigPayload.STRICT_INPUT, b ? 1 : 0));
    });
    setEnablePressed(false);
  }

  @Override
  public void onRender(EnhancedGuiGraphics g, float pt) {
    super.onRender(g, pt);

    int u = reader.getBool(BuiltinSyncedFields.STRICT_INPUT) ? 12 : 0;
    g.draw(TextureRegion.of(BuiltinComponents.PANELS, u, 204, 12, 12), x, y, width, height);
  }

  @Override
  public void onCollectingTooltips(List<Component> tooltips) {
    super.onCollectingTooltips(tooltips);

    boolean b = reader.getBool(BuiltinSyncedFields.STRICT_INPUT);
    MutableComponent mc = Component.translatable(Ten4.lang("misc.strict_input"));
    if (b) {
      mc.append(Component.translatable(Ten4.lang("misc.enabled")));
    } else {
      mc.append(Component.translatable(Ten4.lang("misc.disabled")));
    }
    tooltips.add(mc);
    tooltips.add(Component.translatable(Ten4.lang("misc.strict_input.desc")).withStyle(ChatFormatting.GRAY));
  }
}
