package com.hypothetic.ten4.lib.client.builtin;

import com.hypothetic.ten4.lib.blockentity.SignalMode;
import com.hypothetic.ten4.lib.client.components.Button;
import com.hypothetic.ten4.lib.client.render.gui.EnhancedGuiGraphics;
import com.hypothetic.ten4.lib.client.render.gui.TextureRegion;
import com.hypothetic.ten4.lib.container.sync.BuiltinSyncedFields;
import com.hypothetic.ten4.lib.container.sync.SyncedFieldReader;
import com.hypothetic.ten4.lib.network.SetSignalPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

/**
 * Tri-state button cycling IGNORE→LOW→HIGH.
 */
class SigModeButton extends Button {
  final SyncedFieldReader reader;

  SigModeButton(int x, int y, int w, int h, SyncedFieldReader reader, BlockPos pos) {
    super(x, y, w, h);
    this.reader = reader;
    setAction(() -> {
      int sm = reader.getInt(BuiltinSyncedFields.SIG_MODE);
      SignalMode next = SignalMode.of((sm + 1) % SignalMode.values().length);
      PacketDistributor.sendToServer(new SetSignalPayload(pos, next));
    });
    setEnablePressed(false);
  }

  @Override
  public void onRender(EnhancedGuiGraphics g, float pt) {
    int sm = reader.getInt(BuiltinSyncedFields.SIG_MODE);
    int u = sm * 12;
    g.draw(TextureRegion.of(BuiltinComponents.PANELS, u, 190, 12, 12), x, y, width, height);
  }

  @Override
  public void onCollectingTooltips(List<Component> tooltips) {
    super.onCollectingTooltips(tooltips);

    int mode = reader.getInt(BuiltinSyncedFields.SIG_MODE);
    SignalMode sig = SignalMode.of(mode);
    tooltips.add(sig.getComponent());
    tooltips.add(sig.getDesc().withStyle(ChatFormatting.GRAY));
  }
}
