package com.hypothetic.ten4;

import com.hypothetic.ten4.core.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Ten4.ID)
public class Ten4 {
  public static final Logger LOGGER = LogManager.getLogger();
  public static final String ID = "ten4";
  public static final String VERSION = "26.1.1";

  public Ten4(IEventBus modBus) {
    ModBlockEntityBridges.createMapBeforeRegistry();

    ModBlocks.BLOCKS.register(modBus);
    ModItems.ITEMS.register(modBus);
    ModCreativeTabs.TABS.register(modBus);
    ModBlockEntities.BES.register(modBus);
    ModRecipes.SERIALIZERS.register(modBus);
    ModRecipes.TYPES.register(modBus);
    ModMenus.MENUS.register(modBus);
  }

  public static ResourceLocation id(String path) {
    return ResourceLocation.fromNamespaceAndPath(ID, path);
  }

  public static String lang(String path) {
    return ID + "." + path;
  }
}
