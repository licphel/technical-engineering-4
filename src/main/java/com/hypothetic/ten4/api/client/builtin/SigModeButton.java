package com.hypothetic.ten4.api.client.builtin;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.device.SignalMode;
import com.hypothetic.ten4.api.client.components.Button;
import com.hypothetic.ten4.api.client.gui.EnhancedGuiGraphics;
import com.hypothetic.ten4.api.client.gui.TextureRegion;
import com.hypothetic.ten4.api.container.sync.BuiltinSyncedFields;
import com.hypothetic.ten4.api.container.sync.SyncedFieldReader;
import com.hypothetic.ten4.api.network.device.SigmodePayload;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
      PacketDistributor.sendToServer(new SigmodePayload(pos, next));
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

    MutableComponent mc = Component.translatable(Ten4.getLangKey("misc.sigmode"));
    mc.append(sig.createTranslation());
    tooltips.add(mc);
    tooltips.add(sig.createDescription().withStyle(ChatFormatting.GRAY));
  }
}
