package com.hypothetic.ten4.lib.client.builtin;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.lib.client.components.Button;
import com.hypothetic.ten4.lib.client.render.gui.EnhancedGuiGraphics;
import com.hypothetic.ten4.lib.client.render.gui.TextureRegion;
import com.hypothetic.ten4.lib.container.sync.BuiltinSyncedFields;
import com.hypothetic.ten4.lib.container.sync.SyncedFieldReader;
import com.hypothetic.ten4.lib.network.DeviceConfigPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

class RequestRateButton extends Button {
  static final int[] RATES = {1, 2, 5, 10, 20};
  final SyncedFieldReader reader;
  final BlockPos pos;

  RequestRateButton(int x, int y, int w, int h, SyncedFieldReader r, BlockPos p) {
    super(x, y, w, h);
    reader = r;
    pos = p;
    setAction(() -> {
      int cur = reader.getInt(BuiltinSyncedFields.REQUEST_RATE);
      int idx = 0;
      for (int i = 0; i < RATES.length; i++) {
        if (RATES[i] == cur) {
          idx = i;
          break;
        }
      }
      int next = RATES[(idx + 1) % RATES.length];
      PacketDistributor.sendToServer(new DeviceConfigPayload(pos, DeviceConfigPayload.REQUEST_RATE, next));
    });
    setEnablePressed(false);
  }

  @Override
  public void onRender(EnhancedGuiGraphics g, float pt) {
    super.onRender(g, pt);

    int idx = 0;
    int cur = reader.getInt(BuiltinSyncedFields.REQUEST_RATE);
    for (int i = 0; i < RATES.length; i++) {
      if (RATES[i] == cur) {
        idx = i;
        break;
      }
    }

    int u = idx * 12;
    g.draw(TextureRegion.of(BuiltinComponents.PANELS, u, 232, 12, 12), x, y, width, height);
  }

  @Override
  public void onCollectingTooltips(List<Component> tooltips) {
    super.onCollectingTooltips(tooltips);

    int v = reader.getInt(BuiltinSyncedFields.REQUEST_RATE);
    MutableComponent mc = Component.translatable(Ten4.getLangKey("misc.request_rate"));
    mc.append(Component.literal(Integer.toString(v)));

    tooltips.add(mc);
    tooltips.add(Component.translatable(Ten4.getLangKey("misc.request_rate.desc")).withStyle(ChatFormatting.GRAY));
  }
}
