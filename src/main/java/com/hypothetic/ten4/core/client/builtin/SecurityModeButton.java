package com.hypothetic.ten4.core.client.builtin;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.device.SecurityMode;
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

public class SecurityModeButton extends Button {
  private final SyncedFieldReader reader;
  private final BlockPos pos;

  public SecurityModeButton(int x, int y, int w, int h, SyncedFieldReader r, BlockPos p) {
    super(x, y, w, h);
    reader = r;
    pos = p;
    setAction(() -> {
      int cur = reader.getInt(BuiltinSyncedFields.SECURITY_MODE);
      int next = (cur + 1) % SecurityMode.values().length;
      PacketDistributor.sendToServer(new DeviceConfigPayload(pos, DeviceConfigPayload.SECURITY_MODE, next, 0));
    });
    setEnablePressed(false);
  }

  @Override
  public void onRender(EnhancedGuiGraphics g, float pt) {
    int mode = reader.getInt(BuiltinSyncedFields.SECURITY_MODE);
    int u = mode * 12;
    g.draw(TextureRegion.of(BuiltinComponents.PANELS, u, 232, 12, 12), x, y, width, height);
  }

  @Override
  public void onCollectingTooltips(List<Component> tooltips) {
    super.onCollectingTooltips(tooltips);
    int mode = reader.getInt(BuiltinSyncedFields.SECURITY_MODE);
    SecurityMode sm = SecurityMode.of(mode);
    MutableComponent mc = Component.translatable(Ten4.lang("misc.security_mode"));
    mc.append(sm.createTranslation());
    tooltips.add(mc);
    tooltips.add(sm.createDescription().withStyle(ChatFormatting.GRAY));
  }
}
