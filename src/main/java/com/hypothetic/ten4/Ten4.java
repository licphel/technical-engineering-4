package com.hypothetic.ten4;

import com.hypothetic.ten4.device.PulverizerBlockEntity;
import com.hypothetic.ten4.init.*;
import com.hypothetic.ten4.lib.blockentity.device.AbstractDeviceBlockEntity;
import com.hypothetic.ten4.device.HeatGeneratorBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

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

        // Capabilities
        modBus.addListener(this::registerCaps);
    }

    @SuppressWarnings("unchecked")
    private void registerCaps(RegisterCapabilitiesEvent event) {
      BlockEntityType<PulverizerBlockEntity> pulv = ModBlockEntities.PULVERIZER.get();
      BlockEntityType<HeatGeneratorBlockEntity> extr = ModBlockEntities.HEAT_GENERATOR.get();
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, pulv,
                (be, side) -> ((AbstractDeviceBlockEntity) be).getEnergyStorage(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, pulv,
                (be, side) -> ((AbstractDeviceBlockEntity) be).getItemHandler(side));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, extr,
                (be, side) -> ((AbstractDeviceBlockEntity) be).getEnergyStorage(side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, extr,
                (be, side) -> ((AbstractDeviceBlockEntity) be).getItemHandler(side));
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
