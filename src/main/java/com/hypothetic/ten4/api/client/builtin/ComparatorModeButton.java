package com.hypothetic.ten4.api.client.builtin;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.device.ComparatorMode;
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

import static com.hypothetic.ten4.api.client.builtin.BuiltinComponents.PANELS;

public class ComparatorModeButton extends Button {
  final SyncedFieldReader reader;
  final BlockPos pos;

  public ComparatorModeButton(int x, int y, int w, int h, SyncedFieldReader r, BlockPos p) {
    super(x, y, w, h);
    reader = r;
    pos = p;
    setAction(() -> {
      int cur = reader.getInt(BuiltinSyncedFields.COMPARATOR_MODE);
      int next = (cur + 1) % ComparatorMode.values().length;
      PacketDistributor.sendToServer(new DeviceConfigPayload(pos, DeviceConfigPayload.COMPARATOR_MODE, next));
    });
    setEnablePressed(false);
  }

  @Override
  public void onRender(EnhancedGuiGraphics g, float pt) {
    int mode = reader.getInt(BuiltinSyncedFields.COMPARATOR_MODE);
    int u = mode * 12, v = 218;
    g.draw(TextureRegion.of(PANELS, u, v, 12, 12), x, y, width, height);
  }

  @Override
  public void onCollectingTooltips(List<Component> tooltips) {
    super.onCollectingTooltips(tooltips);

    int mode = reader.getInt(BuiltinSyncedFields.COMPARATOR_MODE);
    ComparatorMode cm = ComparatorMode.of(mode);

    MutableComponent mc = Component.translatable(Ten4.lang("misc.comparator_mode"));
    mc.append(cm.createTranslation());
    tooltips.add(mc);
    tooltips.add(cm.createDescription().withStyle(ChatFormatting.GRAY));
  }
}
