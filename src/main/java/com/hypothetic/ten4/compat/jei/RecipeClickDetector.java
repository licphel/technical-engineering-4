package com.hypothetic.ten4.compat.jei;

import com.hypothetic.ten4.api.client.ComponentedContainerScreen;
import com.hypothetic.ten4.api.client.components.UiComponent;
import com.hypothetic.ten4.api.client.components.UiTag;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

record RecipeClickDetector(RecipeType<?> recipeType)
    implements IGuiContainerHandler<ComponentedContainerScreen<? extends AbstractContainerMenu>> {
  @Override
  public Collection<IGuiClickableArea> getGuiClickableAreas(ComponentedContainerScreen<? extends AbstractContainerMenu> containerScreen, double guiMouseX, double guiMouseY) {
    List<IGuiClickableArea> areas = new ArrayList<>();

    for (UiComponent component : containerScreen.getComponents()) {
      if (component.getTag() == UiTag.JEI_RECIPE_CLICKABLE) {
        areas.add(IGuiClickableArea.createBasic(
            component.getSemanticX(),
            component.getSemanticY(),
            component.getWidth(),
            component.getHeight(),
            recipeType)
        );
      }
    }

    return areas;
  }
}
