package com.hypothetic.ten4.api.client.builtin;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.ITranslatable;
import com.hypothetic.ten4.api.client.ComponentedContainerScreen;
import com.hypothetic.ten4.api.client.components.*;
import com.hypothetic.ten4.api.client.gui.EnhancedGuiGraphics;
import com.hypothetic.ten4.api.client.gui.TextureRegion;
import com.hypothetic.ten4.api.container.AugmentableContainerMenu;
import com.hypothetic.ten4.api.container.ContainerMenu;
import com.hypothetic.ten4.api.container.ManualSlot;
import com.hypothetic.ten4.api.container.sync.BuiltinSyncedFields;
import com.hypothetic.ten4.api.container.sync.SyncedFieldReader;
import com.hypothetic.ten4.api.container.sync.SyncedFluidStack;
import com.hypothetic.ten4.util.DisplayUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

public final class BuiltinComponents {
  public static final ResourceLocation TEXTURE = Ten4.id("textures/gui/components.png");
  public static final ResourceLocation PANELS = Ten4.id("textures/gui/panels.png");
  public static final TextureRegion ENERGY_EMPTY = TextureRegion.of(TEXTURE, 204, 0, 8, 50);
  public static final TextureRegion ENERGY_FULL = TextureRegion.of(TEXTURE, 204, 50, 8, 50);
  public static final TextureRegion FUEL_EMPTY = TextureRegion.of(TEXTURE, 168, 0, 14, 14);
  public static final TextureRegion FUEL_FULL = TextureRegion.of(TEXTURE, 168, 14, 14, 14);
  public static final TextureRegion PROGRESS_EMPTY = TextureRegion.of(TEXTURE, 234, 0, 22, 16);
  public static final TextureRegion PROGRESS_FULL = TextureRegion.of(TEXTURE, 234, 16, 22, 16);
  public static final TextureRegion FLUID_TANK = TextureRegion.of(TEXTURE, 214, 0, 18, 50);

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
        tooltips.add(DisplayUtil.forgeEnergy(partial.getAsInt(), full.getAsInt()));
      }
    }.withTexture(ENERGY_EMPTY, ENERGY_FULL);
  }

  public static GaugeVertical fuelGauge(int x, int y, SyncedFieldReader reader) {
    return new GaugeVertical(x, y, 14, 14,
        () -> reader.getInt(BuiltinSyncedFields.FUEL),
        () -> reader.getInt(BuiltinSyncedFields.MAX_FUEL)
    ) {
      @Override
      public void onCollectingTooltips(List<Component> tooltips) {
      }
    }.withTexture(FUEL_EMPTY, FUEL_FULL);
  }

  public static GaugeHorizontal progressGauge(int x, int y, SyncedFieldReader reader) {
    return new GaugeHorizontal(x, y, 22, 16,
        () -> reader.getInt(BuiltinSyncedFields.PROGRESS),
        () -> reader.getInt(BuiltinSyncedFields.MAX_PROGRESS)
    ).withTexture(PROGRESS_EMPTY, PROGRESS_FULL);
  }

  public static GaugeFluid fluidGauge(int x, int y, SyncedFieldReader reader, SyncedFluidStack sfs) {
    return new GaugeFluid(x, y, 18, 50, 16, 48,
        () -> sfs.decode(reader),
        () -> reader.getInt(sfs.getCapacityField())
    ).withTexture(FLUID_TANK, null);
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
    return new PanelLayout(-15, 8).panelGap(2);
  }

  public static PanelLayout rightPanels(int bgWidth) {
    return new PanelLayout(bgWidth, 8).panelGap(2);
  }

  public static Panel infoPanel(ComponentedContainerScreen<ContainerMenu> screen) {
    SyncedFieldReader reader = screen.getMenu().fieldsReader();
    ITranslatable ip = screen.getMenu().getBlockEntity();
    Component rawText = Component.translatable(ip.createTranslationKey());

    return new Panel(BuiltinComponents.panelBacking(15, 15, 241, 67, 15, 15), 91, 72, -15) {
      double scrollY;
      int verticalH = -1;

      @Override
      public boolean onMouseScrolled(double mx, double my, double delta) {
        scrollY = Math.max(0, Math.min(scrollY - delta * 10, verticalH - 48));
        return true;
      }

      @Override
      protected void renderBody(EnhancedGuiGraphics g, int bx, int by, int bw, int bh) {
        g.draw(TextureRegion.of(PANELS, 165, 84, 91, 72), bx, by);
        g.drawString(Component.translatable(Ten4.lang("misc.info_label")),
            bx + 15, by + 4, 0xDCFFFFFF, false);

        if (verticalH == -1) {
          verticalH = g.font().split(rawText, 82).size() * 9;
        }

        g.inner().enableScissor(bx + 6, by + 18, bx + bw - 2, by + bh - 2);
        g.drawBrokenString(rawText, bx + 6, by + 18 - (int) scrollY, 0xFF000000, 82, false);
        g.inner().disableScissor();
      }
    }.expandLeft();
  }

  public static Panel sigModePanel(ComponentedContainerScreen<ContainerMenu> screen) {
    SyncedFieldReader reader = screen.getMenu().fieldsReader();
    BlockPos pos = screen.getMenu().getBlockEntity().getBlockPos();
    return new Panel(BuiltinComponents.panelBacking(15, 15, 241, 0, 15, 15), 91, 48, -15) {
      {
        addChild(new SigModeButton(18, 24, 12, 12, reader, pos));
        addChild(new StrictInputButton(33, 24, 12, 12, reader, pos));
        addChild(new ComparatorModeButton(48, 24, 12, 12, reader, pos));
        addChild(new ReqIntervalButton(63, 24, 12, 12, reader, pos));
      }

      @Override
      protected void renderBody(EnhancedGuiGraphics g, int bx, int by, int bw, int bh) {
        g.draw(TextureRegion.of(PANELS, 165, 17, 91, 48), bx, by);
        g.drawString(Component.translatable(Ten4.lang("misc.sigmode_label")),
            bx + 15, by + 4, 0xDCFFFFFF, false);
      }
    }.expandLeft();
  }

  public static Panel ioPanel(ComponentedContainerScreen<ContainerMenu> screen) {
    IoConfigState state = new IoConfigState(screen.getMenu().fieldsReader());
    return new Panel(BuiltinComponents.panelBacking(15, 15, 0, 67, 15, 15), 91, 90, -15) {
      {
        Direction facing = screen.getMenu().getBlockEntity().getBlockState()
            .getValue(BlockStateProperties.HORIZONTAL_FACING);
        addChild(new IoTypeButton(6, 21, 12, 12, state));
        addChild(new IoFaceButton(36, 24, Direction.UP, screen, state, "up"));
        addChild(new IoFaceButton(18, 42, facing.getClockWise(), screen, state, "left"));
        addChild(new IoFaceButton(36, 42, facing, screen, state, "front"));
        addChild(new IoFaceButton(54, 42, facing.getCounterClockWise(), screen, state, "right"));
        addChild(new IoFaceButton(36, 60, Direction.DOWN, screen, state, "down"));
        addChild(new IoFaceButton(54, 60, facing.getOpposite(), screen, state, "back"));
      }

      @Override
      protected void renderBody(EnhancedGuiGraphics g, int bx, int by, int bw, int bh) {
        g.draw(TextureRegion.of(PANELS, 0, 84, 91, 90), bx, by);
        g.drawString(Component.translatable(Ten4.lang("misc.io_label")),
            bx + 15, by + 4, 0xDCFFFFFF, false);
      }
    };
  }

  public static Panel augmentPanel(ComponentedContainerScreen<ContainerMenu> screen) {
    Panel panel = new Panel(BuiltinComponents.panelBacking(15, 15, 0, 0, 15, 15), 91, 48, -15) {
      @Override
      public void onTick() {
        super.onTick();
        boolean exp = isExpanded();
        int i = 0;
        for (UiComponent component : children) {
          if (component instanceof CapturedSlot slot) {
            slot.backingSlot().setActive(exp);
            slot.setSemanticX(9 + i * 18);
            slot.setSemanticY(23);
            i++;
          }
        }
      }

      @Override
      protected void renderBody(EnhancedGuiGraphics g, int bx, int by, int bw, int bh) {
        g.draw(TextureRegion.of(PANELS, 0, 17, 91, 48), bx, by);
        g.drawString(Component.translatable(Ten4.lang("misc.augment_label")),
            bx + 15, by + 4, 0xDCFFFFFF, false);
      }
    };

    ContainerMenu menu = screen.getMenu();
    if (menu instanceof AugmentableContainerMenu acm) {
      for (ManualSlot slot : acm.getAugmentSlots()) {
        panel.addChild(new CapturedSlot(-1, -1, screen, slot));
      }
    }
    return panel;
  }

  public static UiComponent[] defaultPanels(ComponentedContainerScreen<ContainerMenu> screen) {
    PanelLayout leftPanels = BuiltinComponents.leftPanels();
    PanelLayout rightPanels = BuiltinComponents.rightPanels(screen.getGuiSize()[0]);
    leftPanels.addPanel(BuiltinComponents.infoPanel(screen));
    leftPanels.addPanel(BuiltinComponents.sigModePanel(screen));
    rightPanels.addPanel(BuiltinComponents.ioPanel(screen));
    rightPanels.addPanel(BuiltinComponents.augmentPanel(screen));
    return new UiComponent[] {leftPanels, rightPanels};
  }
}
