package com.hypothetic.ten4.core.client.builtin;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.device.FaceMode;
import com.hypothetic.ten4.api.blockentity.device.FaceModePacker;
import com.hypothetic.ten4.api.client.ComponentedContainerScreen;
import com.hypothetic.ten4.api.client.components.Button;
import com.hypothetic.ten4.api.client.gui.EnhancedGuiGraphics;
import com.hypothetic.ten4.api.client.gui.TextureRegion;
import com.hypothetic.ten4.api.container.ContainerMenu;
import com.hypothetic.ten4.api.network.device.IoFacePayload;
import com.hypothetic.ten4.util.ClientUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class IoFaceButton extends Button {
  final Direction dir;
  final ComponentedContainerScreen<ContainerMenu> screen;
  final IoFlagReader state;
  final String relSide;

  public IoFaceButton(int x, int y, Direction dir, ComponentedContainerScreen<ContainerMenu> screen, IoFlagReader state, String relSide) {
    super(x, y, 16, 16);
    this.dir = dir;
    this.screen = screen;
    this.state = state;
    setAction(() -> {
      int curPacked = state.packedFor(state.type);
      int nextPacked = FaceModePacker.cycle(curPacked, dir);
      PacketDistributor.sendToServer(new IoFacePayload(
          screen.getMenu().getBlockEntity().getBlockPos(), state.type, nextPacked));
    });
    this.relSide = relSide;
  }

  private static void renderFaceModeIcon(EnhancedGuiGraphics g, int x, int y, FaceMode mode) {
    int u = 52 + 9 * mode.ordinal();
    g.draw(TextureRegion.of(BuiltinComponents.PANELS, u, 218, 7, 8), x, y);
  }

  @Override
  public void onRender(EnhancedGuiGraphics g, float pt) {
    super.onRender(g, pt);

    BlockState blockState = screen.getMenu().getBlockEntity().getBlockState();
    TextureRegion faceTex = ClientUtil.getFaceSprite(blockState, dir);
    if (faceTex != null) {
      g.draw(faceTex, x, y, 16, 16);
    }
    FaceMode mode = state.get(dir);

    renderFaceModeIcon(g, x + 9, y + 9, mode);
  }

  @Override
  public void onCollectingTooltips(List<Component> tooltips, int mx, int my) {
    super.onCollectingTooltips(tooltips, mx, my);

    MutableComponent mc = Component.translatable(Ten4.lang("misc.facemode"));
    mc.append(state.get(dir).createTranslation());
    tooltips.add(mc);
    tooltips.add(Component.translatable(Ten4.lang("misc." + relSide)).withStyle(ChatFormatting.GRAY));
    tooltips.add(state.get(dir).createDescription().withStyle(ChatFormatting.GRAY));
    // Show auto flag states in tooltip
    if (state.isAutoEject()) {
      tooltips.add(Component.translatable(Ten4.lang("misc.auto_eject")).append(Component.translatable(Ten4.lang("misc.enabled"))).withStyle(ChatFormatting.GRAY));
    }
    if (state.isAutoExtract()) {
      tooltips.add(Component.translatable(Ten4.lang("misc.auto_extract")).append(Component.translatable(Ten4.lang("misc.enabled"))).withStyle(ChatFormatting.GRAY));
    }
    if (state.isStrictInput()) {
      tooltips.add(Component.translatable(Ten4.lang("misc.strict_input")).append(Component.translatable(Ten4.lang("misc.enabled"))).withStyle(ChatFormatting.GRAY));
    }
  }

  @Override
  public void onMouseMotion(int mx, int my) {
    super.onMouseMotion(mx, my);
    if (hovering) {
      state.activeDir = dir;
    }
  }
}
