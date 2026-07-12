package com.hypothetic.ten4.compat.jei;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.client.builtin.BuiltinComponents;
import com.hypothetic.ten4.api.client.gui.EnhancedGuiGraphics;
import com.hypothetic.ten4.api.client.gui.TextureRegion;
import com.hypothetic.ten4.api.recipe.RecipeEntry;
import com.hypothetic.ten4.api.recipe.ModRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public abstract class ModRecipeCategory<T> implements IRecipeCategory<T> {
  private final RecipeType<T> type;
  private final Component title;
  private final IDrawable icon;

  public ModRecipeCategory(IGuiHelper helper, RecipeType<T> type, ItemStack iconStack) {
    this.type = type;
    this.title = Component.translatable(Ten4.getLangKey(type.getUid().getPath()));
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

  protected void addItemInput(IRecipeLayoutBuilder builder, RecipeEntry input, int x, int y) {
    builder.addInputSlot(x, y)
        .addItemStacks(input.itemStacks())
        .setStandardSlotBackground();
  }

  protected void addItemOutput(IRecipeLayoutBuilder builder, RecipeEntry output, int x, int y) {
    builder.addOutputSlot(x, y)
        .addItemStack(output.symbolItem())
        .addRichTooltipCallback((recipeSlotView, tooltip) -> {
          if (output.chance() < 1) {
            tooltip.add(Component.literal((int) (output.chance() * 100) + "%").withStyle(ChatFormatting.GRAY));
          }
        })
        .setStandardSlotBackground();
  }

  private static final IDrawable fluidTank = new IDrawable() {
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
      g.draw(TextureRegion.of(BuiltinComponents.TEXTURE, 214, 0, 18, 50), xOffset - 1, yOffset - 1);
    }
  };

  protected void addFluidInput(IRecipeLayoutBuilder builder, RecipeEntry input, int x, int y) {
    builder.addInputSlot(x, y)
        .addIngredients(NeoForgeTypes.FLUID_STACK, input.fluidStacks())
        .setFluidRenderer(input.count(), false, 16, 48)
        .setBackground(fluidTank, x, y);
  }

  protected void addFluidOutput(IRecipeLayoutBuilder builder, ItemStack output, int x, int y) {
    builder.addOutputSlot(x, y)
        .addItemStack(output)
        .setBackground(fluidTank, x, y);
  }

  protected void addEnergyGauge(IRecipeExtrasBuilder builder, int x, int y, boolean increasing) {
    builder.addDrawable(new IDrawable() {
      final ModTickTimer timer = new ModTickTimer(1000, 100, !increasing);
      final int width = 8;
      final int height = 50;

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
        int fill = Math.round(height * frac);

        g.draw(BuiltinComponents.ENERGY_EMPTY, x, y, width, height);

        if (fill > 0) {
          int fillUV = Math.round(height * frac);
          g.draw(BuiltinComponents.ENERGY_FULL, x, y + height - fill, width, fill, 0, height - fillUV, width, fillUV);
        }
      }
    });
  }

  protected void addFuelGauge(IRecipeExtrasBuilder builder, int burnTime, int x, int y) {
    builder.addDrawable(new IDrawable() {
      final ModTickTimer timer = new ModTickTimer(burnTime, 100, true);
      final int width = 14;
      final int height = 14;

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
        int fill = Math.round(height * frac);

        g.draw(BuiltinComponents.FUEL_EMPTY, x, y, width, height);

        if (fill > 0) {
          int fillUV = Math.round(height * frac);
          g.draw(BuiltinComponents.FUEL_FULL, x, y + height - fill, width, fill, 0, height - fillUV, width, fillUV);
        }
      }
    });
  }

  protected void addProgressGauge(IRecipeExtrasBuilder builder, ModRecipe recipe, int x, int y) {
    builder.addDrawable(new IDrawable() {
      final int width = 22;
      final int height = 16;
      final ModTickTimer timer = new ModTickTimer(recipe.time(), 100, false);

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
        int fill = Math.round(width * frac);

        g.draw(BuiltinComponents.PROGRESS_EMPTY, x, y, width, height);

        if (fill > 0) {
          int fillUV = Math.round(width * frac);
          g.draw(BuiltinComponents.PROGRESS_FULL, x, y, fill, height, 0, 0, fillUV, height);
        }
      }
    });
  }
}
