package com.hypothetic.ten4;

import com.hypothetic.ten4.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Ten4.ID)
public class Ten4 {
  public static final String ID = "ten4";

  public Ten4(IEventBus modBus) {
    // Blocks
    ModBlocks.BLOCKS.register(modBus);
    ModBlocks.BLOCK_ITEMS.register(modBus);
    ModBlocks.trigger();

    // Items
    ModItems.ITEMS.register(modBus);
    ModItems.trigger();

    // Creative tabs
    ModCreativeTabs.TABS.register(modBus);

    // Block entities
    ModBlockEntities.TILES.register(modBus);
    ModBlockEntities.trigger();

    // Recipes
    ModRecipes.SERIALIZERS.register(modBus);
    ModRecipes.TYPES.register(modBus);

    // Menus
    ModMenus.MENUS.register(modBus);
    ModMenus.trigger();
  }

  public static ResourceLocation id(String path) {
    return ResourceLocation.fromNamespaceAndPath(ID, path);
  }

  public static ResourceLocation vanillaId(String path) {
    return ResourceLocation.fromNamespaceAndPath("minecraft", path);
  }

  public static String getLangKey(String path) {
    return ID + "." + path;
  }
}
