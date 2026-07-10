package com.hypothetic.ten4.lib.client;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.lib.blockentity.FaceMode;
import com.hypothetic.ten4.lib.client.components.*;
import com.hypothetic.ten4.lib.client.render.gui.EnhancedGuiGraphics;
import com.hypothetic.ten4.lib.client.render.gui.TextureRegion;
import com.hypothetic.ten4.lib.container.AugmentableContainerMenu;
import com.hypothetic.ten4.lib.container.ContainerMenu;
import com.hypothetic.ten4.lib.container.ManualSlot;
import com.hypothetic.ten4.lib.container.sync.BuiltinSyncedFields;
import com.hypothetic.ten4.lib.container.sync.SyncedFieldReader;
import com.hypothetic.ten4.lib.util.DisplayHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.IntSupplier;

public final class BuiltinComponents {
  private static final ResourceLocation TEXTURE = Ten4.id("textures/gui/components.png");
  private static final ResourceLocation PANELS = Ten4.id("textures/gui/panels.png");

  private BuiltinComponents() {
  }

  public static GaugeVertical energyGauge(int x, int y, SyncedFieldReader reader) {
    return new GaugeVertical(x, y, 8, 50,
        () -> reader.getInt(BuiltinSyncedFields.ENERGY),
        () -> reader.getInt(BuiltinSyncedFields.MAX_ENERGY)
    ) {
      @Override
      public void onCollectingTooltips(List<Component> tooltips) {
        super.onCollectingTooltips(tooltips);

        tooltips.add(DisplayHelper.getFE(partial.getAsInt(), full.getAsInt()));
      }
    }.withTexture(
        TextureRegion.of(TEXTURE, 204, 0, 8, 50),
        TextureRegion.of(TEXTURE, 204, 50, 8, 50));
  }

  public static GaugeVertical fuelGauge(int x, int y, SyncedFieldReader reader) {
    return new GaugeVertical(x, y, 14, 14,
        () -> reader.getInt(BuiltinSyncedFields.FUEL),
        () -> reader.getInt(BuiltinSyncedFields.MAX_FUEL)
    ) {
      @Override
      public void onCollectingTooltips(List<Component> tooltips) {
        super.onCollectingTooltips(tooltips);
      }
    }.withTexture(
        TextureRegion.of(TEXTURE, 168, 0, 14, 14),
        TextureRegion.of(TEXTURE, 168, 14, 14, 14));
  }

  public static GaugeHorizontal progressGauge(int x, int y, SyncedFieldReader reader) {
    return new GaugeHorizontal(x, y, 22, 16,
        () -> reader.getInt(BuiltinSyncedFields.PROGRESS),
        () -> reader.getInt(BuiltinSyncedFields.MAX_PROGRESS)
    ).withTexture(
        TextureRegion.of(TEXTURE, 234, 0, 22, 16),
        TextureRegion.of(TEXTURE, 234, 16, 22, 16));
  }

  // 2-px padding rightward
  public static Button buttonNonstate(int x, int y, int w, int h, int u, int v, int srcW, int srcH) {
    return new Button(x, y, w, h)
        .withTexture(
            TextureRegion.of(TEXTURE, u, v, srcW, srcH),
            TextureRegion.of(TEXTURE, u + srcW, v, srcW, srcH),
            null
        ).setEnablePressed(false);
  }

  public static Button panelBacking(int w, int h, int u, int v, int srcW, int srcH) {
    return new Button(0, 0, w, h)
        .withTexture(
            TextureRegion.of(PANELS, u, v, srcW, srcH),
            TextureRegion.of(PANELS, u, v, srcW, srcH),
            TextureRegion.of(PANELS, u, v, srcW, srcH)
        ).setEnablePressed(false);
  }

  public static PanelLayout leftPanels() {
    return new PanelLayout(-15, 6).panelGap(2);
  }

  public static PanelLayout rightPanels(int bgWidth) {
    return new PanelLayout(bgWidth, 6).panelGap(2);
  }

  public static Panel infoPanel(DeviceScreen screen) {
    return new Panel(BuiltinComponents.panelBacking(9, 9, 0, 0, 9, 9), 80, 40, 2)
        .expandLeft();
  }

  public static Panel sigModePanel(DeviceScreen screen) {
    return new Panel(BuiltinComponents.panelBacking(9, 9, 0, 11, 9, 9), 80, 40, 2)
        .expandLeft();
  }

  public static Panel ioPanel(DeviceScreen screen) {
    return new Panel(BuiltinComponents.panelBacking(15, 15, 0, 67, 15, 15), 102, 90, -15) {
      // 渲染六个机器面：
      // x 上 x
      // 左 前 右
      // x 下 后
      // 需要获取当前的方块纹理贴到gui上面。
      // 他们在renderBody里面的偏移量分别是：
      // 上 41 25
      // 左 23 43
      // 前 41 43
      // 右 59 43
      // 下 41 61
      // 后 59 61

      // 每个面偏移量再加上(14, 14)，渲染facemode对应的小图标
      // PANEL纹理里面的UV分别为：OFF 113 104, PASSIVE_RECEIVE 104 84 PASSIVE_EXTRACT 104 94
      // ACTIVE_RECEIVE 113 84 ACTIVE_EXTRACT 113 94 PASSIVE_BOTH 104 104. 大小均为7x8

      // 点击每个面可以切换facemode并发包。
      // 下面有一个三态按钮（这个需要来一个内部类）用于切换当前正在配置的是能量/物品还是流体。该按钮位于相对于renderBody偏移量的6， 21
      // UV为123, 84,大小12x12。横向U+方向连续排布能量/物品/流体状态纹理。

      {
        //addChild();
      }

      @Override
      protected void renderBody(EnhancedGuiGraphics g, int bx, int by, int bw, int bh) {
        g.draw(TextureRegion.of(PANELS, 0, 84, 102, 90), bx, by);
        g.drawString(Component.translatable(Ten4.getLangKey("gui.device.io_label")),
            bx + 15, by + 3, 0XFFFFFFFF, false);
      }
    };
  }

  public static Panel augmentPanel(DeviceScreen screen) {
    Panel panel = new Panel(BuiltinComponents.panelBacking(15, 15, 0, 0, 15, 15), 102, 48, -15) {
      @Override
      public void onTick() {
        super.onTick();

        boolean exp = isExpanded();

        int i = 0;
        int j = 0;

        for (UiComponent component : children) {
          if (component instanceof CapturedSlot slot) {
            slot.backingSlot().setActive(exp);

            slot.setSemanticX(16 + i * 18);
            slot.setSemanticY(23 + j * 18);

            i++;
            if (i == 2) {
              i = 0;
              j++;
            }
          }
        }
      }

      @Override
      protected void renderBody(EnhancedGuiGraphics g, int bx, int by, int bw, int bh) {
        g.draw(TextureRegion.of(PANELS, 0, 17, 102, 48), bx, by);
        g.drawString(Component.translatable(Ten4.getLangKey("gui.device.augment_label")),
            bx + 15, by + 3, 0XFFFFFFFF, false);
      }
    };

    // Add augment slots if needed
    ContainerMenu menu = screen.getMenu();
    if (menu instanceof AugmentableContainerMenu acm) {
      for (ManualSlot slot : acm.getAugmentSlots()) {
        panel.addChild(new CapturedSlot(-1, -1, screen, slot));
      }
    }

    return panel;
  }

  private static class IoConfigButton extends Button {
    // 0 - energy, 1 - item, 2 - fluid
    private final IntSupplier type;

    public IoConfigButton(int x, int y, int w, int h, IntSupplier type) {
      super(x, y, w, h);
      this.type = type;

      setAction(() -> {

      });
    }
  }
}
