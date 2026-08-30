package com.hypothetic.ten4.compat.jei;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.client.gui.EnhancedGuiGraphics;
import com.hypothetic.ten4.api.client.gui.TextureRegion;
import com.hypothetic.ten4.api.recipe.Complex;
import com.hypothetic.ten4.compat.jei.util.JeiModTickTimer;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import static com.hypothetic.ten4.core.client.builtin.BuiltinComponents.TEXTURE;

public abstract class JeiModCategory<T> implements IRecipeCategory<T> {
  public static final TextureRegion ENERGY_EMPTY = TextureRegion.of(TEXTURE, 204, 52, 8, 50);
  public static final TextureRegion FUEL_EMPTY = TextureRegion.of(TEXTURE, 168, 52, 14, 14);
  public static final TextureRegion FUEL_FULL = TextureRegion.of(TEXTURE, 168, 14 + 52, 14, 14);
  public static final TextureRegion PROGRESS_EMPTY = TextureRegion.of(TEXTURE, 234, 52, 22, 16);
  public static final TextureRegion PROGRESS_FULL = TextureRegion.of(TEXTURE, 234, 16 + 52, 22, 16);
  public static final TextureRegion FLUID_TANK = TextureRegion.of(TEXTURE, 214, 52, 18, 50);
  public static final TextureRegion FLUID_TANK_OVERLAY = TextureRegion.of(TEXTURE, 150, 0, 18, 50);
  public static final TextureRegion SLOT = TextureRegion.of(TEXTURE, 184, 52, 18, 18);

  private static final IDrawable FLUID_BACKGROUND = new IDrawable() {
    @Override
    public int getWidth() {
      return 18;
    }

    @Override
    public int getHeight() {
      return 50;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
      EnhancedGuiGraphics g = new EnhancedGuiGraphics(guiGraphics);
      g.draw(FLUID_TANK, xOffset - 1, yOffset - 1);
    }
  };

  private static final IDrawable FLUID_OVERLAY = new IDrawable() {
    @Override
    public int getWidth() {
      return 18;
    }

    @Override
    public int getHeight() {
      return 50;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
      EnhancedGuiGraphics g = new EnhancedGuiGraphics(guiGraphics);
      g.draw(FLUID_TANK_OVERLAY, xOffset - 1, yOffset - 1);
    }
  };

  private final RecipeType<T> type;
  private final Component title;
  private final IDrawable icon;

  public JeiModCategory(IGuiHelper helper, RecipeType<T> type, ItemStack iconStack) {
    this.type = type;
    this.title = Component.translatable(Ten4.lang(type.getUid().getPath()));
    this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, iconStack);
  }

  @Override
  public RecipeType<T> getRecipeType() {
    return type;
  }

  @Override
  public Component getTitle() {
    return title;
  }

  @Override
  public abstract int getWidth();

  @Override
  public abstract int getHeight();

  @Override
  public IDrawable getIcon() {
    return icon;
  }

  @Override
  public void draw(T recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
    createBackground().draw(guiGraphics);
  }

  protected void addItemInput(IRecipeLayoutBuilder builder, Complex input, int x, int y) {
    builder.addInputSlot(x, y)
        .addItemStacks(input.itemStacks());
        //.setStandardSlotBackground();
  }

  protected void addItemOutput(IRecipeLayoutBuilder builder, Complex output, int x, int y, boolean outputSlot) {
    IRecipeSlotBuilder sb = builder.addOutputSlot(x, y)
        .addItemStack(output.symbolItem())
        .addRichTooltipCallback((recipeSlotView, tooltip) -> {
          if (output.chance() < 1) {
            MutableComponent mc = Component.translatable(Ten4.lang("misc.chance")).withStyle(ChatFormatting.GOLD);
            mc.append(Component.literal((int) (output.chance() * 100) + "%").withStyle(ChatFormatting.GRAY));
            tooltip.add(mc);
          }
        });
    if (outputSlot) {
      //sb.setOutputSlotBackground();
    } else {
      //sb.setStandardSlotBackground();
    }
  }

  protected void addItemOutput(IRecipeLayoutBuilder builder, Complex output, int x, int y) {
    addItemOutput(builder, output, x, y, false);
  }

  protected void addFluidInput(IRecipeLayoutBuilder builder, Complex input, int x, int y) {
    IRecipeSlotBuilder sb = builder.addInputSlot(x, y)
        .addIngredients(NeoForgeTypes.FLUID_STACK, input.fluidStacks())
        .setBackground(FLUID_BACKGROUND, 0, 0)
        .setOverlay(FLUID_OVERLAY, 0, 0);

    if (input.count() > 0) {
      sb.setFluidRenderer(input.count() * 2L, false, 16, 48);
    }
  }

  protected void addFluidOutput(IRecipeLayoutBuilder builder, Complex output, int x, int y) {
    FluidStack stack = output.symbolFluid();
    IRecipeSlotBuilder sb = builder.addOutputSlot(x, y)
        .addFluidStack(stack.getFluid(), stack.getAmount())
        .setBackground(FLUID_BACKGROUND, 0, 0)
        .setOverlay(FLUID_OVERLAY, 0, 0)
        .addRichTooltipCallback((recipeSlotView, tooltip) -> {
          if (output.chance() < 1) {
            MutableComponent mc = Component.translatable(Ten4.lang("misc.chance")).withStyle(ChatFormatting.GOLD);
            mc.append(Component.literal((int) (output.chance() * 100) + "%").withStyle(ChatFormatting.GRAY));
            tooltip.add(mc);
          }
        });

    if (output.count() > 0) {
      sb.setFluidRenderer(output.count() * 2L, false, 16, 48);
    }
  }

  protected void addEnergyGauge(IRecipeExtrasBuilder builder, int x, int y, boolean increasing) {
    builder.addDrawable(new IDrawable() {
      final JeiModTickTimer timer = new JeiModTickTimer(1000, 100, !increasing);
      final int width = 8;
      final int height = 50;
      final int innerW = 6;
      final int innerH = 48;

      @Override
      public int getWidth() {
        return width;
      }

      @Override
      public int getHeight() {
        return height;
      }

      @Override
      public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
        EnhancedGuiGraphics g = new EnhancedGuiGraphics(guiGraphics);

        int e = timer.getValue(), me = timer.getMaxValue();
        float frac = me > 0 ? (float) e / me : 0;
        int fill = Math.round(innerH * frac);

        g.draw(ENERGY_EMPTY, x, y, width, height);

        if (fill > 0) {
          int ox = Math.round((width - innerW) / 2.0F);
          int oy = Math.round((height - innerH) / 2.0F);
          g.drawFluid(Fluids.WATER, x + ox, y - fill + oy + innerH, innerW, fill, false, () -> 0xFF52B380);
        }
      }
    });
  }

  protected void addFuelGauge(IRecipeExtrasBuilder builder, int burnTime, int x, int y) {
    builder.addWidget(new IRecipeWidget() {
      final JeiModTickTimer timer = new JeiModTickTimer(burnTime, 100, true);
      final int width = 14;
      final int height = 14;

      @Override
      public ScreenPosition getPosition() {
        return new ScreenPosition(x, y);
      }

      @Override
      public void drawWidget(GuiGraphics guiGraphics, double mouseX, double mouseY) {
        EnhancedGuiGraphics g = new EnhancedGuiGraphics(guiGraphics);

        int e = timer.getValue(), me = timer.getMaxValue();
        float frac = me > 0 ? (float) e / me : 0;
        int fill = Math.round(height * frac);

        g.draw(FUEL_EMPTY, 0, 0, width, height);

        if (fill > 0) {
          int fillUV = Math.round(height * frac);
          g.draw(FUEL_FULL, 0, height - fill, width, fill, 0, height - fillUV, width, fillUV);
        }
      }

      @Override
      public void getTooltip(ITooltipBuilder tooltip, double mouseX, double mouseY) {
        if (mouseX >= 0 && mouseX <= width && mouseY >= 0 && mouseY <= height) {
          tooltip.add(Component.translatable("ten4.misc.supplement_time").append(burnTime + "t"));
        }
      }
    });
  }

  protected void addProgressGauge(IRecipeExtrasBuilder builder, int time, int x, int y) {
    builder.addWidget(new IRecipeWidget() {
      final int width = 22;
      final int height = 16;
      final JeiModTickTimer timer = new JeiModTickTimer(time, 100, false);

      @Override
      public ScreenPosition getPosition() {
        return new ScreenPosition(x, y);
      }

      @Override
      public void drawWidget(GuiGraphics guiGraphics, double mouseX, double mouseY) {
        EnhancedGuiGraphics g = new EnhancedGuiGraphics(guiGraphics);

        int e = timer.getValue(), me = timer.getMaxValue();
        float frac = me > 0 ? (float) e / me : 0;
        int fill = Math.round(width * frac);

        g.draw(PROGRESS_EMPTY, 0, 0, width, height);

        if (fill > 0) {
          int fillUV = Math.round(width * frac);
          g.draw(PROGRESS_FULL, 0, 0, fill, height, 0, 0, fillUV, height);
        }
      }

      @Override
      public void getTooltip(ITooltipBuilder tooltip, double mouseX, double mouseY) {
        if (mouseX >= 0 && mouseX <= width && mouseY >= 0 && mouseY <= height) {
          tooltip.add(Component.translatable("ten4.misc.consumed_time").append(time + "t"));
        }
      }
    });
  }

  protected IDrawable createBackground() {
    TextureRegion tr = TextureRegion.of(Ten4.id("textures/gui/compat/" + getRecipeType().getUid().getPath() + ".png"));
    assert tr != null;
    return new IDrawable() {
      @Override
      public int getWidth() {
        return tr.width();
      }

      @Override
      public int getHeight() {
        return tr.height();
      }

      @Override
      public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
        EnhancedGuiGraphics g = new EnhancedGuiGraphics(guiGraphics);
        g.draw(tr, xOffset, yOffset);
      }
    };
  }
}
