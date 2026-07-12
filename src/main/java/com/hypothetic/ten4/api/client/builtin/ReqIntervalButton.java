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

class ReqIntervalButton extends Button {
  static final int[] RATES = {1, 2, 5, 10, 20};
  final SyncedFieldReader reader;
  final BlockPos pos;

  ReqIntervalButton(int x, int y, int w, int h, SyncedFieldReader r, BlockPos p) {
    super(x, y, w, h);
    reader = r;
    pos = p;
    setAction(() -> {
      int cur = reader.getInt(BuiltinSyncedFields.REQUEST_INTERVAL);
      int idx = 0;
      for (int i = 0; i < RATES.length; i++) {
        if (RATES[i] == cur) {
          idx = i;
          break;
        }
      }
      int next = RATES[(idx + 1) % RATES.length];
      PacketDistributor.sendToServer(new DeviceConfigPayload(pos, DeviceConfigPayload.REQUEST_INTERVAL, next));
    });
    setEnablePressed(false);
  }

  @Override
  public void onRender(EnhancedGuiGraphics g, float pt) {
    super.onRender(g, pt);

    int idx = 0;
    int cur = reader.getInt(BuiltinSyncedFields.REQUEST_INTERVAL);
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

    int v = reader.getInt(BuiltinSyncedFields.REQUEST_INTERVAL);
    MutableComponent mc = Component.translatable(Ten4.getLangKey("misc.request_interval"));
    mc.append(Component.literal(Integer.toString(v)));

    tooltips.add(mc);
    tooltips.add(Component.translatable(Ten4.getLangKey("misc.request_interval.desc")).withStyle(ChatFormatting.GRAY));
  }
}
