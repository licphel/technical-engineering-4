package com.hypothetic.ten4.core.registry.client;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.client.renderer.RenderEnergyDuct;
import com.hypothetic.ten4.api.client.renderer.RenderFluidDuct;
import com.hypothetic.ten4.api.client.renderer.RenderItemDuct;
import com.hypothetic.ten4.core.registry.ModBlockEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Ten4.ID, value = Dist.CLIENT)
public class ModBlockEntityRenderers {
  @SubscribeEvent
  public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(ModBlockEntities.COPPER_ENERGY_DUCT.get(), ctx -> new RenderEnergyDuct(ctx, Ten4.id("block/duct/copper_energy_duct")));
    event.registerBlockEntityRenderer(ModBlockEntities.COPPER_ITEM_DUCT.get(), ctx -> new RenderItemDuct(ctx, Ten4.id("block/duct/copper_item_duct")));
    event.registerBlockEntityRenderer(ModBlockEntities.COPPER_FLUID_DUCT.get(), ctx -> new RenderFluidDuct(ctx, Ten4.id("block/duct/copper_fluid_duct")));
    event.registerBlockEntityRenderer(ModBlockEntities.COPPER_CONTROLLER_ENERGY_DUCT.get(), ctx -> new RenderEnergyDuct(ctx, Ten4.id("block/duct/copper_controller_energy_duct")));
    event.registerBlockEntityRenderer(ModBlockEntities.COPPER_CONTROLLER_ITEM_DUCT.get(), ctx -> new RenderItemDuct(ctx, Ten4.id("block/duct/copper_controller_item_duct")));
    event.registerBlockEntityRenderer(ModBlockEntities.COPPER_CONTROLLER_FLUID_DUCT.get(), ctx -> new RenderFluidDuct(ctx, Ten4.id("block/duct/copper_controller_fluid_duct")));
  }
}
