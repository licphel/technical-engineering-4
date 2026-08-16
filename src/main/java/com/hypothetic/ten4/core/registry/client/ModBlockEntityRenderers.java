package com.hypothetic.ten4.core.registry.client;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.core.client.renderer.LiquidUnitItemRenderer;
import com.hypothetic.ten4.core.client.renderer.RenderEnergyDuct;
import com.hypothetic.ten4.core.client.renderer.RenderFluidDuct;
import com.hypothetic.ten4.core.client.renderer.RenderItemDuct;
import com.hypothetic.ten4.core.client.renderer.RenderLiquidUnitBlock;
import com.hypothetic.ten4.core.registry.ModBlockEntities;
import com.hypothetic.ten4.core.registry.ModItems;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

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

    register(event, ModBlockEntities.LIQUID_UNIT.get(), RenderLiquidUnitBlock::new);
  }

  @SubscribeEvent
  public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
    // IClientItemExtensions for the liquid unit — renders stored fluid in the item form
    event.registerItem(new IClientItemExtensions() {
      @Override
      public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return LiquidUnitItemRenderer.INSTANCE;
      }
    }, ModItems.LIQUID_UNIT.get());
  }

  @SuppressWarnings("unchecked")
  private static <T extends BlockEntity> void register(EntityRenderersEvent.RegisterRenderers event, BlockEntityType<?> blockEntityType, BlockEntityRendererProvider<T> blockEntityRendererProvider) {
    event.registerBlockEntityRenderer((BlockEntityType<? extends T>) blockEntityType, blockEntityRendererProvider);
  }
}
