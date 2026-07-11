package com.hypothetic.ten4.init;

import com.hypothetic.ten4.Ten4;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class ModCreativeTabs {
  public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Ten4.ID);

  static final List<Supplier<Item>> blockTab = new ArrayList<>();
  public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BLOCK_TAB = TABS.register("block",
      () -> CreativeModeTab.builder()
          .icon(() -> new ItemStack(Items.IRON_BLOCK))
          .title(Component.translatable(Ten4.getLangKey("creative_mode_tab.block")))
          .displayItems((params, output) -> blockTab.forEach(s -> output.accept(s.get())))
          .build());
  static final List<Supplier<Item>> deviceTab = new ArrayList<>();
  public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MACHINE_TAB = TABS.register("device",
      () -> CreativeModeTab.builder()
          .icon(() -> new ItemStack(Items.FURNACE))
          .title(Component.translatable(Ten4.getLangKey("creative_mode_tab.device")))
          .displayItems((params, output) -> deviceTab.forEach(s -> output.accept(s.get())))
          .build());
  static final List<Supplier<Item>> materialTab = new ArrayList<>();
  public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ITEM_TAB = TABS.register("item",
      () -> CreativeModeTab.builder()
          .icon(() -> new ItemStack(Items.IRON_INGOT))
          .title(Component.translatable(Ten4.getLangKey("creative_mode_tab.material")))
          .displayItems((params, output) -> materialTab.forEach(s -> output.accept(s.get())))
          .build());
  static final List<Supplier<Item>> toolTab = new ArrayList<>();
  public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TOOL_TAB = TABS.register("tool",
      () -> CreativeModeTab.builder()
          .icon(() -> new ItemStack(Items.STICK))
          .title(Component.translatable(Ten4.getLangKey("creative_mode_tab.tool")))
          .displayItems((params, output) -> toolTab.forEach(s -> output.accept(s.get())))
          .build());

  private ModCreativeTabs() {
  }

  public static void init() {
  }
}
