package com.hypothetic.ten4.core.client.builtin;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.client.ComponentedContainerScreen;
import com.hypothetic.ten4.api.client.components.*;
import com.hypothetic.ten4.api.client.gui.EnhancedGuiGraphics;
import com.hypothetic.ten4.api.client.gui.TextureRegion;
import com.hypothetic.ten4.api.container.ContainerMenu;
import com.hypothetic.ten4.api.container.ManualSlot;
import com.hypothetic.ten4.api.container.sync.BuiltinSyncedFields;
import com.hypothetic.ten4.api.container.sync.SyncedFieldReader;
import com.hypothetic.ten4.api.container.sync.SyncedFluidStack;
import com.hypothetic.ten4.core.registry.ModSoundEvents;
import com.hypothetic.ten4.util.DisplayUtil;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.Objects;

public final class BuiltinComponents {
  public static final SoundEvent BEEP = ModSoundEvents.BEEP.get();
  public static final ResourceLocation TEXTURE = Ten4.id("textures/gui/components.png");
  public static final ResourceLocation PANELS = Ten4.id("textures/gui/panels.png");
  public static final TextureRegion PANEL_LEFT = TextureRegion.of(Ten4.id("textures/gui/panel_left.png"));
  public static final TextureRegion PANEL_RIGHT = TextureRegion.of(Ten4.id("textures/gui/panel_right.png"));
  public static final TextureRegion ENERGY_EMPTY = TextureRegion.of(TEXTURE, 204, 0, 8, 50);
  public static final TextureRegion FUEL_EMPTY = TextureRegion.of(TEXTURE, 168, 0, 14, 14);
  public static final TextureRegion FUEL_FULL = TextureRegion.of(TEXTURE, 168, 14, 14, 14);
  public static final TextureRegion PROGRESS_EMPTY = TextureRegion.of(TEXTURE, 234, 0, 22, 16);
  public static final TextureRegion PROGRESS_FULL = TextureRegion.of(TEXTURE, 234, 16, 22, 16);
  public static final TextureRegion FLUID_TANK = TextureRegion.of(TEXTURE, 214, 0, 18, 50);
  public static final TextureRegion FLUID_TANK_OVERLAY = TextureRegion.of(TEXTURE, 150, 0, 18, 50);
  public static final TextureRegion SLOT = TextureRegion.of(TEXTURE, 184, 0, 18, 18);

  private BuiltinComponents() {
  }

  public static GaugeFluid energyGauge(int x, int y, SyncedFieldReader reader) {
    return new GaugeFluid(x, y, 8, 50, 6, 48,
        () -> new FluidStack(Fluids.WATER, reader.getInt(BuiltinSyncedFields.ENERGY)),
        () -> reader.getInt(BuiltinSyncedFields.MAX_ENERGY)
    ) {
      @Override
      public void onCollectingTooltips(List<Component> tooltips, int mx, int my) {
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
      public void onCollectingTooltips(List<Component> tooltips, int mx, int my) {
      }
    }.withTexture(FUEL_EMPTY, FUEL_FULL).tagged(UiTag.JEI_RECIPE_CLICKABLE);
  }

  public static GaugeHorizontal progressGauge(int x, int y, SyncedFieldReader reader) {
    return new GaugeHorizontal(x, y, 22, 16,
        () -> reader.getInt(BuiltinSyncedFields.PROGRESS),
        () -> reader.getInt(BuiltinSyncedFields.MAX_PROGRESS)
    ).withTexture(PROGRESS_EMPTY, PROGRESS_FULL).tagged(UiTag.JEI_RECIPE_CLICKABLE);
  }

  public static GaugeFluid fluidGauge(int x, int y, SyncedFieldReader reader, SyncedFluidStack sfs) {
    return new GaugeFluid(x, y, 18, 50, 16, 48,
        () -> sfs.decode(reader),
        () -> reader.getInt(sfs.getCapacityField())
    ).withTexture(FLUID_TANK, FLUID_TANK_OVERLAY).tagged(UiTag.JEI_FLUID_TANK);
  }

  public static CapturedSlot capturedSlot(int x, int y,  AbstractContainerScreen<? extends AbstractContainerMenu> menu, ManualSlot backingSlot) {
    return new CapturedSlot(x, y, menu, backingSlot).withTexture(SLOT);
  }

  public static PanelLayout leftPanels() {
    return new PanelLayout(-Panel.MIN_WIDTH + 2, 8).panelGap(2);
  }

  public static PanelLayout rightPanels(int bgWidth) {
    return new PanelLayout(bgWidth - 2, 8).panelGap(2);
  }

  public static UiComponent infoBar(SyncedFieldReader reader) {
    return new UiComponent(164, 4, 7, 7) {
      @Override
      public void onRender(EnhancedGuiGraphics g, float pt) {
        super.onRender(g, pt);

        g.draw(TextureRegion.of(TEXTURE, 105, 190, width, height), x, y, width, height);
      }

      @Override
      public void onCollectingTooltips(List<Component> tooltips, int mx, int my) {
        super.onCollectingTooltips(tooltips, mx, my);

        String power = DisplayUtil.compactInt(reader.getInt(BuiltinSyncedFields.POWER));
        String eThru = DisplayUtil.compactInt(reader.getInt(BuiltinSyncedFields.ENERGY_THROUGHPUT));
        String iThru = DisplayUtil.compactInt(reader.getInt(BuiltinSyncedFields.ITEM_THROUGHPUT));
        String fThru = DisplayUtil.compactInt(reader.getInt(BuiltinSyncedFields.FLUID_THROUGHPUT));
        tooltips.add(Component.translatable(Ten4.lang("misc.power")).append(power + "FE/t"));
        tooltips.add(Component.translatable(Ten4.lang("misc.throughput.energy")).append(eThru + "FE/t"));
        tooltips.add(Component.translatable(Ten4.lang("misc.throughput.item")).append(iThru + "S/t"));
        tooltips.add(Component.translatable(Ten4.lang("misc.throughput.fluid")).append(fThru + "mB/t"));
      }
    }.withClickSound(BEEP);
  }

  public static UiComponent[] standardDeviceUI(ComponentedContainerScreen<ContainerMenu> screen) {
    Objects.requireNonNull(PANEL_LEFT);
    Objects.requireNonNull(PANEL_RIGHT);

    PanelLayout leftPanels = leftPanels();
    PanelLayout rightPanels = rightPanels(screen.getGuiSize()[0]);
    leftPanels.addPanel(new RedstonePanel(screen, PANEL_LEFT, Panel.LEFT));
    leftPanels.addPanel(new IoPanel(screen, PANEL_LEFT, Panel.LEFT));
    rightPanels.addPanel(new AugmentPanel(screen, PANEL_RIGHT, Panel.RIGHT));
    rightPanels.addPanel(new SecurityPanel(screen, PANEL_RIGHT, Panel.RIGHT));
    return new UiComponent[] {leftPanels, rightPanels, infoBar(screen.getMenu().fieldsReader())};
  }
}
