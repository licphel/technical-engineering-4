package com.hypothetic.ten4.registry;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.device.AbstractDeviceBlockEntity;
import com.hypothetic.ten4.api.blockentity.internet.EnergyDuctBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = Ten4.ID)
public class ModCapabilities {
  @SubscribeEvent
  public static void registerCaps(RegisterCapabilitiesEvent event) {
    // devices
    event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
        ModBlockEntities.PULVERIZER.get(), AbstractDeviceBlockEntity::getEnergyStorage);
    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
        ModBlockEntities.PULVERIZER.get(), AbstractDeviceBlockEntity::getItemHandler);
    event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
        ModBlockEntities.HEAT_GENERATOR.get(), AbstractDeviceBlockEntity::getEnergyStorage);
    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
        ModBlockEntities.HEAT_GENERATOR.get(), AbstractDeviceBlockEntity::getItemHandler);

    // ducts
    event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
        ModBlockEntities.COPPER_ENERGY_DUCT.get(), EnergyDuctBlockEntity::getEnergyStorage);
  }
}
