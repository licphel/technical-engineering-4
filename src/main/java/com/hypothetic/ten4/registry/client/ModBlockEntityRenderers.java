package com.hypothetic.ten4.registry.client;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.registry.ModBlockEntities;
import com.hypothetic.ten4.api.client.renderer.RenderEnergyDuct;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Ten4.ID, value = Dist.CLIENT)
public class ModBlockEntityRenderers {
  @SubscribeEvent
  public static void listen(EntityRenderersEvent.RegisterRenderers e) {
    e.registerBlockEntityRenderer(ModBlockEntities.COPPER_ENERGY_DUCT.get(),
        ctx -> new RenderEnergyDuct(ctx, Ten4.id("block/duct/copper_energy_duct")));
  }
}
