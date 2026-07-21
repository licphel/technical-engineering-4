package com.hypothetic.ten4.compat.jei;

import com.hypothetic.ten4.api.client.ComponentedContainerScreen;
import com.hypothetic.ten4.api.client.components.GaugeFluid;
import com.hypothetic.ten4.api.client.components.UiComponent;
import com.hypothetic.ten4.api.client.components.UiTag;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.renderer.Rect2i;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.Optional;

record GeneralDetector(
    IGuiHandlerRegistration registry) implements IGuiContainerHandler<ComponentedContainerScreen<?>> {
  @Override
  public List<Rect2i> getGuiExtraAreas(ComponentedContainerScreen<?> containerScreen) {
    return containerScreen.getComponentAreas();
  }

  @Override
  public Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(ComponentedContainerScreen<? > containerScreen, double mouseX, double mouseY) {
    IJeiHelpers jeiHelpers = registry.getJeiHelpers();
    IIngredientManager ingredientManager = jeiHelpers.getIngredientManager();

    for (UiComponent component : containerScreen.getComponents()) {
      if (component instanceof GaugeFluid gf
          && component.getTag() == UiTag.JEI_FLUID_TANK
          && component.isMouseHovering((int) mouseX, (int) mouseY)) {
        Optional<IClickableIngredient<FluidStack>> ret = ingredientManager.createClickableIngredient(
            NeoForgeTypes.FLUID_STACK,
            gf.getStack(),
            gf.getBounds(),
            true
        );
        // Stupid java generics. Why Optional<T> cannot cast to Optional<?>?
        return Optional.ofNullable(ret.orElse(null));
      }
    }

    return Optional.empty();
  }
}
