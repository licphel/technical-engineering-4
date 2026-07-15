package com.hypothetic.ten4.core.registry;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.device.AbstractDeviceBlockEntity;
import com.hypothetic.ten4.api.blockentity.transmission.EnergyDuctBlockEntity;
import com.hypothetic.ten4.api.blockentity.transmission.FluidDuctBlockEntity;
import com.hypothetic.ten4.api.blockentity.transmission.ItemDuctBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = Ten4.ID)
public class ModCapabilities {
  @SubscribeEvent
  public static void onRegisterCaps(RegisterCapabilitiesEvent event) {
    // Devices
    event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.PULVERIZER.get(), AbstractDeviceBlockEntity::getEnergyStorage);
    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.PULVERIZER.get(), AbstractDeviceBlockEntity::getItemHandler);
    event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.SMELTER.get(), AbstractDeviceBlockEntity::getEnergyStorage);
    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.SMELTER.get(), AbstractDeviceBlockEntity::getItemHandler);
    event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.WATER_PUMP.get(), AbstractDeviceBlockEntity::getEnergyStorage);
    event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.WATER_PUMP.get(), AbstractDeviceBlockEntity::getFluidHandler);

    // Generators
    event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.HEAT_GENERATOR.get(), AbstractDeviceBlockEntity::getEnergyStorage);
    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.HEAT_GENERATOR.get(), AbstractDeviceBlockEntity::getItemHandler);

    // Ducts
    event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.COPPER_ENERGY_DUCT.get(), EnergyDuctBlockEntity::getEnergyStorage);
    event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.COPPER_CONTROLLER_ENERGY_DUCT.get(), EnergyDuctBlockEntity::getEnergyStorage);
    event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.COPPER_FLUID_DUCT.get(), FluidDuctBlockEntity::getFluidHandler);
    event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.COPPER_CONTROLLER_FLUID_DUCT.get(), FluidDuctBlockEntity::getFluidHandler);
    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.COPPER_ITEM_DUCT.get(), ItemDuctBlockEntity::getItemHandler);
    event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.COPPER_CONTROLLER_ITEM_DUCT.get(), ItemDuctBlockEntity::getItemHandler);
  }
}
