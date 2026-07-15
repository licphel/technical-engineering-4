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

class IoFaceButton extends Button {
  final Direction dir;
  final ComponentedContainerScreen<ContainerMenu> screen;
  final IoConfigState state;
  final String relSide;

  IoFaceButton(int x, int y, Direction dir, ComponentedContainerScreen<ContainerMenu> screen, IoConfigState state, String relSide) {
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
    int u, v;
    switch (mode) {
      case OFF -> {
        u = 102;
        v = 104;
      }
      case PASSIVE_INPUT -> {
        u = 93;
        v = 84;
      }
      case PASSIVE_OUTPUT -> {
        u = 93;
        v = 94;
      }
      case ACTIVE_INPUT -> {
        u = 102;
        v = 84;
      }
      case ACTIVE_OUTPUT -> {
        u = 102;
        v = 94;
      }
      case PASSIVE_BIPASS -> {
        u = 93;
        v = 104;
      }
      default -> {
        return;
      }
    }
    g.draw(TextureRegion.of(BuiltinComponents.PANELS, u, v, 7, 8), x, y);
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
  public void onCollectingTooltips(List<Component> tooltips) {
    super.onCollectingTooltips(tooltips);

    MutableComponent mc = Component.translatable(Ten4.lang("misc.facemode"));
    mc.append(state.get(dir).createTranslation());
    tooltips.add(mc);
    tooltips.add(Component.translatable(Ten4.lang("misc." + relSide)).withStyle(ChatFormatting.GRAY));
    tooltips.add(state.get(dir).createDescription().withStyle(ChatFormatting.GRAY));
  }
}
