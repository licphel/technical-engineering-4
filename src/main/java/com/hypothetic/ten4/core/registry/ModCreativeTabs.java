package com.hypothetic.ten4.core.registry;

import com.hypothetic.ten4.Ten4;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
  public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Ten4.ID);

  public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SINGLE_TAB = TABS.register("default",
      () -> CreativeModeTab.builder()
          .icon(() -> new ItemStack(ModItems.PULVERIZER.get()))
          .title(Component.translatable(Ten4.lang("creative_mode_tab.default")))
          .displayItems((params, output) -> {
            for (Item item : BuiltInRegistries.ITEM) {
              String name = BuiltInRegistries.ITEM.getKey(item).getNamespace();
              if (Ten4.ID.equals(name)) {
                output.accept(item);
              }
            }
          }).build());
}
