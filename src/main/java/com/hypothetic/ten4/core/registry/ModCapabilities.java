package com.hypothetic.ten4.core.registry;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.device.AbstractDeviceBlockEntity;
import com.hypothetic.ten4.api.blockentity.transmission.EnergyDuctBlockEntity;
import com.hypothetic.ten4.api.blockentity.transmission.FluidDuctBlockEntity;
import com.hypothetic.ten4.api.blockentity.transmission.ItemDuctBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = Ten4.ID)
public class ModCapabilities {
  @SubscribeEvent
  public static void onRegisterCaps(RegisterCapabilitiesEvent event) {
    // Devices
    register(event, Capabilities.EnergyStorage.BLOCK, ModBlockEntities.PULVERIZER.get(), AbstractDeviceBlockEntity::getEnergyStorage);
    register(event, Capabilities.ItemHandler.BLOCK, ModBlockEntities.PULVERIZER.get(), AbstractDeviceBlockEntity::getItemHandler);
    register(event, Capabilities.EnergyStorage.BLOCK, ModBlockEntities.PRESS.get(), AbstractDeviceBlockEntity::getEnergyStorage);
    register(event, Capabilities.ItemHandler.BLOCK, ModBlockEntities.PRESS.get(), AbstractDeviceBlockEntity::getItemHandler);
    register(event, Capabilities.EnergyStorage.BLOCK, ModBlockEntities.SMELTER.get(), AbstractDeviceBlockEntity::getEnergyStorage);
    register(event, Capabilities.ItemHandler.BLOCK, ModBlockEntities.SMELTER.get(), AbstractDeviceBlockEntity::getItemHandler);
    register(event, Capabilities.EnergyStorage.BLOCK, ModBlockEntities.WATER_PUMP.get(), AbstractDeviceBlockEntity::getEnergyStorage);
    register(event, Capabilities.FluidHandler.BLOCK, ModBlockEntities.WATER_PUMP.get(), AbstractDeviceBlockEntity::getFluidHandler);

    // Generators
    register(event, Capabilities.EnergyStorage.BLOCK, ModBlockEntities.HEAT_GENERATOR.get(), AbstractDeviceBlockEntity::getEnergyStorage);
    register(event, Capabilities.ItemHandler.BLOCK, ModBlockEntities.HEAT_GENERATOR.get(), AbstractDeviceBlockEntity::getItemHandler);

    // Ducts
    register(event, Capabilities.EnergyStorage.BLOCK, ModBlockEntities.COPPER_ENERGY_DUCT.get(), EnergyDuctBlockEntity::getEnergyStorage);
    register(event, Capabilities.EnergyStorage.BLOCK, ModBlockEntities.OPAQUE_COPPER_ENERGY_DUCT.get(), EnergyDuctBlockEntity::getEnergyStorage);
    register(event, Capabilities.EnergyStorage.BLOCK, ModBlockEntities.COPPER_CONTROLLER_ENERGY_DUCT.get(), EnergyDuctBlockEntity::getEnergyStorage);
    register(event, Capabilities.ItemHandler.BLOCK, ModBlockEntities.COPPER_ITEM_DUCT.get(), ItemDuctBlockEntity::getItemHandler);
    register(event, Capabilities.ItemHandler.BLOCK, ModBlockEntities.OPAQUE_COPPER_ITEM_DUCT.get(), ItemDuctBlockEntity::getItemHandler);
    register(event, Capabilities.ItemHandler.BLOCK, ModBlockEntities.COPPER_CONTROLLER_ITEM_DUCT.get(), ItemDuctBlockEntity::getItemHandler);
    register(event, Capabilities.FluidHandler.BLOCK, ModBlockEntities.COPPER_FLUID_DUCT.get(), FluidDuctBlockEntity::getFluidHandler);
    register(event, Capabilities.FluidHandler.BLOCK, ModBlockEntities.OPAQUE_COPPER_FLUID_DUCT.get(), FluidDuctBlockEntity::getFluidHandler);
    register(event, Capabilities.FluidHandler.BLOCK, ModBlockEntities.COPPER_CONTROLLER_FLUID_DUCT.get(), FluidDuctBlockEntity::getFluidHandler);
  }

  @SuppressWarnings("unchecked")
  private static <T, C extends @Nullable Object, BE extends BlockEntity> void register(RegisterCapabilitiesEvent event, BlockCapability<T, C> capability, BlockEntityType<?> blockEntityType, ICapabilityProvider<? super BE, C, T> provider) {
    event.registerBlockEntity(capability, (BlockEntityType<BE>) blockEntityType, provider);
  }
}
