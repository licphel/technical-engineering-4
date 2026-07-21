package com.hypothetic.ten4;

import com.hypothetic.ten4.core.registry.config.ModConfigs;
import com.hypothetic.ten4.core.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Ten4.ID)
public class Ten4 {
  public static final Logger LOGGER = LogManager.getLogger();
  public static final String ID = "ten4";
  public static final String VERSION = "26.1.3";
  public static final boolean DATA_GEN = false;

  public Ten4(IEventBus modBus, ModContainer container) {
    ModBlockEntityBridges.createMapBeforeRegistry();

    ModFluids.FLUIDS.register(modBus);
    ModBlocks.BLOCKS.register(modBus);
    ModItems.ITEMS.register(modBus);
    ModCreativeTabs.TABS.register(modBus);
    ModBlockEntities.BES.register(modBus);
    ModRecipes.SERIALIZERS.register(modBus);
    ModRecipes.TYPES.register(modBus);
    ModMenus.MENUS.register(modBus);
    ModSoundEvents.SES.register(modBus);

    container.registerConfig(ModConfig.Type.COMMON, ModConfigs.COMMON_SPEC);
    container.registerConfig(ModConfig.Type.CLIENT, ModConfigs.CLIENT_SPEC);
    container.registerConfig(ModConfig.Type.SERVER, ModConfigs.SERVER_SPEC);
  }

  public static ResourceLocation id(String path) {
    return ResourceLocation.fromNamespaceAndPath(ID, path);
  }

  public static String lang(String path) {
    return ID + "." + path;
  }
}
