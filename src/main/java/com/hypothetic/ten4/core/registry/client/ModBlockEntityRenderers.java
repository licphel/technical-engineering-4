package com.hypothetic.ten4.core.registry.client;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.client.renderer.RenderEnergyDuct;
import com.hypothetic.ten4.api.client.renderer.RenderFluidDuct;
import com.hypothetic.ten4.api.client.renderer.RenderItemDuct;
import com.hypothetic.ten4.core.registry.ModBlockEntities;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Ten4.ID, value = Dist.CLIENT)
public class ModBlockEntityRenderers {
  @SubscribeEvent
  public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
    register(event, ModBlockEntities.COPPER_ENERGY_DUCT.get(), ctx -> new RenderEnergyDuct(ctx, Ten4.id("block/duct/copper_energy_duct")));
    register(event, ModBlockEntities.OPAQUE_COPPER_ENERGY_DUCT.get(), ctx -> new RenderEnergyDuct(ctx, Ten4.id("block/duct/opaque_copper_energy_duct")).setOpaque());
    register(event, ModBlockEntities.COPPER_CONTROLLER_ENERGY_DUCT.get(), ctx -> new RenderEnergyDuct(ctx, Ten4.id("block/duct/copper_controller_energy_duct")));
    register(event, ModBlockEntities.COPPER_ITEM_DUCT.get(), ctx -> new RenderItemDuct(ctx, Ten4.id("block/duct/copper_item_duct")));
    register(event, ModBlockEntities.OPAQUE_COPPER_ITEM_DUCT.get(), ctx -> new RenderItemDuct(ctx, Ten4.id("block/duct/opaque_copper_item_duct")).setOpaque());
    register(event, ModBlockEntities.COPPER_CONTROLLER_ITEM_DUCT.get(), ctx -> new RenderItemDuct(ctx, Ten4.id("block/duct/copper_controller_item_duct")));
    register(event, ModBlockEntities.COPPER_FLUID_DUCT.get(), ctx -> new RenderFluidDuct(ctx, Ten4.id("block/duct/copper_fluid_duct")));
    register(event, ModBlockEntities.OPAQUE_COPPER_FLUID_DUCT.get(), ctx -> new RenderFluidDuct(ctx, Ten4.id("block/duct/opaque_copper_fluid_duct")).setOpaque());
    register(event, ModBlockEntities.COPPER_CONTROLLER_FLUID_DUCT.get(), ctx -> new RenderFluidDuct(ctx, Ten4.id("block/duct/copper_controller_fluid_duct")));
  }

  @SuppressWarnings("unchecked")
  private static <T extends BlockEntity> void register(EntityRenderersEvent.RegisterRenderers event, BlockEntityType<?> blockEntityType, BlockEntityRendererProvider<T> blockEntityRendererProvider) {
    event.registerBlockEntityRenderer((BlockEntityType<? extends T>) blockEntityType, blockEntityRendererProvider);
  }
}
