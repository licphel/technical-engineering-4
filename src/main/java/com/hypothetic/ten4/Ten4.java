package com.hypothetic.ten4;

import com.hypothetic.ten4.core.device.HeatGeneratorBlockEntity;
import com.hypothetic.ten4.core.device.PulverizerBlockEntity;
import com.hypothetic.ten4.init.*;
import com.hypothetic.ten4.lib.blockentity.internet.EnergyCableBlockEntity;
import com.hypothetic.ten4.lib.client.render.EnergyCableRenderer;
import com.hypothetic.ten4.lib.network.CableSyncPayload;
import com.hypothetic.ten4.lib.network.DeviceConfigPayload;
import com.hypothetic.ten4.lib.network.IoFacePayload;
import com.hypothetic.ten4.lib.network.SetSignalPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

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

    // Network
    modBus.addListener(RegisterPayloadHandlersEvent.class, e -> {
      var r = e.registrar("1");
      r.playToServer(IoFacePayload.TYPE, IoFacePayload.CODEC, IoFacePayload::handle);
      r.playToServer(SetSignalPayload.TYPE, SetSignalPayload.CODEC, SetSignalPayload::handle);
      r.playToServer(DeviceConfigPayload.TYPE, DeviceConfigPayload.CODEC, DeviceConfigPayload::handle);
      r.playToClient(CableSyncPayload.TYPE, CableSyncPayload.CODEC, CableSyncPayload::handle);
    });

    // BER
    modBus.addListener(EntityRenderersEvent.RegisterRenderers.class, e ->
        e.registerBlockEntityRenderer(ModBlockEntities.GLASS_ENERGY_CABLE.get(), EnergyCableRenderer::new));

    // Capabilities
    modBus.addListener(this::registerCaps);
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

  @SuppressWarnings("unchecked")
  private void registerCaps(RegisterCapabilitiesEvent event) {
    BlockEntityType<PulverizerBlockEntity> pulv = ModBlockEntities.PULVERIZER.get();
    BlockEntityType<HeatGeneratorBlockEntity> extr = ModBlockEntities.HEAT_GENERATOR.get();
    BlockEntityType<EnergyCableBlockEntity> cable = ModBlockEntities.GLASS_ENERGY_CABLE.get();

    event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, pulv,
        (be, side) -> be.getEnergyStorage(side));
    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, pulv,
        (be, side) -> be.getItemHandler(side));
    event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, extr,
        (be, side) -> be.getEnergyStorage(side));
    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, extr,
        (be, side) -> be.getItemHandler(side));
    event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, cable,
        (be, side) -> be.getCap(side));
  }
}
