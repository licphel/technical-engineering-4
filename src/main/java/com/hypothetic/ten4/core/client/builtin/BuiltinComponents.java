package com.hypothetic.ten4.core.client.builtin;

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
import com.hypothetic.ten4.core.registry.ModSoundEvents;
import com.hypothetic.ten4.util.DisplayUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public final class BuiltinComponents {
  public static final SoundEvent BEEP = ModSoundEvents.BEEP.get();
  public static final ResourceLocation TEXTURE = Ten4.id("textures/gui/components.png");
  public static final ResourceLocation PANELS = Ten4.id("textures/gui/panels.png");
  public static final TextureRegion ENERGY_EMPTY = TextureRegion.of(TEXTURE, 204, 0, 8, 50);
  public static final TextureRegion FUEL_EMPTY = TextureRegion.of(TEXTURE, 168, 0, 14, 14);
  public static final TextureRegion FUEL_FULL = TextureRegion.of(TEXTURE, 168, 14, 14, 14);
  public static final TextureRegion PROGRESS_EMPTY = TextureRegion.of(TEXTURE, 234, 0, 22, 16);
  public static final TextureRegion PROGRESS_FULL = TextureRegion.of(TEXTURE, 234, 16, 22, 16);
  public static final TextureRegion FLUID_TANK = TextureRegion.of(TEXTURE, 214, 0, 18, 50);

  private BuiltinComponents() {
  }

  public static GaugeFluid energyGauge(int x, int y, SyncedFieldReader reader) {
    return new GaugeFluid(x, y, 8, 50, 6, 48,
        () -> new FluidStack(Fluids.WATER, reader.getInt(BuiltinSyncedFields.ENERGY)),
        () -> reader.getInt(BuiltinSyncedFields.MAX_ENERGY)
    ) {
      @Override
      public void onCollectingTooltips(List<Component> tooltips) {
        FluidStack stack = stackSupplier.get();
        tooltips.add(DisplayUtil.forgeEnergy(stack.getAmount(), capacity.getAsInt()));
      }
    }.withTexture(ENERGY_EMPTY, null).withTinter(() -> 0xFF52B380);
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

  public static Panel infoPanel(ComponentedContainerScreen<ContainerMenu> screen) {
    SyncedFieldReader reader = screen.getMenu().fieldsReader();
    ITranslatable ip = screen.getMenu().getBlockEntity();
    Component rawText = ip.createDescription();
    String power = DisplayUtil.compactInt(reader.getInt(BuiltinSyncedFields.POWER));
    String eThru = DisplayUtil.compactInt(reader.getInt(BuiltinSyncedFields.ENERGY_THROUGHPUT));
    String iThru = DisplayUtil.compactInt(reader.getInt(BuiltinSyncedFields.ITEM_THROUGHPUT));
    String fThru = DisplayUtil.compactInt(reader.getInt(BuiltinSyncedFields.FLUID_THROUGHPUT));

    return new Panel(panelBacking(15, 15, 241, 60, 15, 15), 91, 40, -15) {
      {
        // H: description hover
        addChild(new UiComponent(33, 21, 12, 12) {
          @Override
          public void onCollectingTooltips(List<Component> tooltips) {
            super.onCollectingTooltips(tooltips);
            tooltips.add(rawText);
          }
        });
        // D: data hover
        addChild(new UiComponent(48, 21, 12, 12) {
          @Override
          public void onCollectingTooltips(List<Component> tooltips) {
            super.onCollectingTooltips(tooltips);
            tooltips.add(Component.translatable(Ten4.lang("misc.power")).append(power + "FE/t"));
            tooltips.add(Component.translatable(Ten4.lang("misc.throughput.energy")).append(eThru + "FE/t"));
            tooltips.add(Component.translatable(Ten4.lang("misc.throughput.item")).append(iThru + "S/t"));
            tooltips.add(Component.translatable(Ten4.lang("misc.throughput.fluid")).append(fThru + "mB/t"));
          }
        });
      }

      @Override
      protected void renderBody(EnhancedGuiGraphics g, int bx, int by, int bw, int bh) {
        g.draw(TextureRegion.of(PANELS, 165, 77, 91, 40), bx, by);
        g.drawString(Component.translatable(Ten4.lang("misc.info_label")),
            bx + 15, by + 4, 0xDCFFFFFF, false);
      }
    }.expandLeft();
  }


  public static Panel redstonePanel(ComponentedContainerScreen<ContainerMenu> screen) {
    SyncedFieldReader reader = screen.getMenu().fieldsReader();
    BlockPos pos = screen.getMenu().getBlockEntity().getBlockPos();
    return new Panel(panelBacking(15, 15, 241, 0, 15, 15), 91, 40, -15) {
      {
        addChild(new SigModeButton(33, 21, 12, 12, reader, pos).withClickSound(BEEP));
        addChild(new ComparatorModeButton(48, 21, 12, 12, reader, pos).withClickSound(BEEP));
      }

      @Override
      protected void renderBody(EnhancedGuiGraphics g, int bx, int by, int bw, int bh) {
        g.draw(TextureRegion.of(PANELS, 165, 17, 91, 40), bx, by);
        g.drawString(Component.translatable(Ten4.lang("misc.redstone_label")),
            bx + 15, by + 4, 0xDCFFFFFF, false);
      }
    }.expandLeft();
  }

  public static Panel securityPanel(ComponentedContainerScreen<ContainerMenu> screen) {
    SyncedFieldReader reader = screen.getMenu().fieldsReader();
    BlockPos pos = screen.getMenu().getBlockEntity().getBlockPos();
    return new Panel(panelBacking(15, 15, 241, 119, 15, 15), 91, 40, -15) {
      {
        addChild(new SecurityModeButton(40, 21, 12, 12, reader, pos).withClickSound(BEEP));
      }

      @Override
      protected void renderBody(EnhancedGuiGraphics g, int bx, int by, int bw, int bh) {
        g.draw(TextureRegion.of(PANELS, 165, 136, 91, 40), bx, by);
        g.drawString(Component.translatable(Ten4.lang("misc.security_label")),
            bx + 15, by + 4, 0xFFFFFFFF, false);
      }
    }.expandLeft();
  }

  public static Panel ioPanel(ComponentedContainerScreen<ContainerMenu> screen) {
    IoConfigState state = new IoConfigState(screen.getMenu().fieldsReader());
    BlockPos pos = screen.getMenu().getBlockEntity().getBlockPos();
    return new Panel(panelBacking(15, 15, 0, 67, 15, 15), 91, 103, -15) {
      {
        Direction facing = screen.getMenu().getBlockEntity().getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        addChild(new IoTypeButton(38, 21, 12, 12, state).withClickSound(BEEP));

        addChild(new IoFaceButton(46, 40, Direction.UP, screen, state, "up").withClickSound(BEEP));
        addChild(new IoFaceButton(28, 58, facing.getClockWise(), screen, state, "left").withClickSound(BEEP));
        addChild(new IoFaceButton(46, 58, facing, screen, state, "front").withClickSound(BEEP));
        addChild(new IoFaceButton(64, 58, facing.getCounterClockWise(), screen, state, "right").withClickSound(BEEP));
        addChild(new IoFaceButton(46, 76, Direction.DOWN, screen, state, "down").withClickSound(BEEP));
        addChild(new IoFaceButton(64, 76, facing.getOpposite(), screen, state, "back").withClickSound(BEEP));

        addChild(new AutoFlagButton(10, 45, 26, 218, state, pos, 25,
            () -> state.isAutoEject(), "auto_eject").withClickSound(BEEP));
        addChild(new AutoFlagButton(10, 60, 0, 218, state, pos, 26,
            () -> state.isAutoExtract(), "auto_extract").withClickSound(BEEP));
        addChild(new AutoFlagButton(10, 75, 0, 204, state, pos, 24,
            () -> state.isStrictInput(), "strict_input").withClickSound(BEEP));
      }

      @Override
      protected void renderBody(EnhancedGuiGraphics g, int bx, int by, int bw, int bh) {
        g.draw(TextureRegion.of(PANELS, 0, 84, 91, 103), bx, by);
        g.drawString(Component.translatable(Ten4.lang("misc.io_label")),
            bx + 15, by + 4, 0xDCFFFFFF, false);
      }
    };
  }

  public static Panel augmentPanel(ComponentedContainerScreen<ContainerMenu> screen) {
    Panel panel = new Panel(panelBacking(15, 15, 0, 0, 15, 15), 91, 48, -15) {
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
    leftPanels.addPanel(BuiltinComponents.redstonePanel(screen));
    leftPanels.addPanel(BuiltinComponents.securityPanel(screen));
    rightPanels.addPanel(BuiltinComponents.ioPanel(screen));
    rightPanels.addPanel(BuiltinComponents.augmentPanel(screen));
    return new UiComponent[] {leftPanels, rightPanels};
  }
}
