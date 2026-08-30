package com.hypothetic.ten4.core.registry;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.core.item.EnergyUnitItem;
import com.hypothetic.ten4.core.item.LiquidUnitItem;
import com.hypothetic.ten4.core.registry.config.ModConfigs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
  public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Ten4.ID);

  public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SINGLE_TAB = TABS.register("default",
      () -> CreativeModeTab.builder()
          .icon(() -> new ItemStack(ModItems.PULVERIZER.get()))
          .title(Component.translatable(Ten4.lang("creative_mode_tab.default")))
          // .withSearchBar()
          .displayItems((params, output) -> {
            for (Item item : BuiltInRegistries.ITEM) {
              String name = BuiltInRegistries.ITEM.getKey(item).getNamespace();
              if (Ten4.ID.equals(name)) {
                output.accept(item);
              }
            }

            // Creative full-storage variants
            int energyCap = ModConfigs.COMMON.others.energyUnitCapacity.get();
            ItemStack energyUnit = new ItemStack(ModItems.ENERGY_UNIT.get());
            EnergyUnitItem.setStoredEnergy(energyUnit, energyCap);
            output.accept(energyUnit);

            int liquidCap = ModConfigs.COMMON.others.liquidUnitCapacity.get();
            for (Fluid fluid : BuiltInRegistries.FLUID) {
              ResourceLocation key = BuiltInRegistries.FLUID.getKey(fluid);
              if (!fluid.isSource(fluid.defaultFluidState())) {
                continue;
              }
              addFilledUnit(output, fluid, liquidCap);
            }
          }).build());

  private static void addFilledUnit(CreativeModeTab.Output output, Fluid fluid, int capacity) {
    ItemStack stack = new ItemStack(ModItems.LIQUID_UNIT.get());
    LiquidUnitItem.setStoredFluid(stack, new FluidStack(fluid, capacity));
    LiquidUnitItem.setCapacity(stack, capacity);
    output.accept(stack);
  }
}
