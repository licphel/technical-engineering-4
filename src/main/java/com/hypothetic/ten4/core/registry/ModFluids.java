package com.hypothetic.ten4.core.registry;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.registry.FluidDeferredRegister;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import java.util.EnumMap;
import java.util.Map;

@EventBusSubscriber(modid = Ten4.ID, value = Dist.CLIENT)
public final class ModFluids {
  public static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(Ten4.ID);

  static final ResourceLocation WATER_STILL = ResourceLocation.withDefaultNamespace("block/water_still");
  static final ResourceLocation WATER_FLOW = ResourceLocation.withDefaultNamespace("block/water_flow");

  public static final FluidDeferredRegister.FluidHolder TORAN = FLUIDS.register("toran_concentrate")
      .density(10000).viscosity(20000)
      .block(ModBlocks.TORAN).build();
  public static final FluidDeferredRegister.FluidHolder EXPERIENCE = FLUIDS.register("liquid_experience")
      .density(1000).viscosity(500)
      .block(ModBlocks.EXPERIENCE).build();
  public static final FluidDeferredRegister.FluidHolder REDSTONE = FLUIDS.register("liquid_redstone")
      .density(1000).viscosity(3000).lightLevel(7)
      .texture(WATER_STILL, WATER_FLOW).tint(0xFFCC0000)
      .block(ModBlocks.REDSTONE).build();
  public static final FluidDeferredRegister.FluidHolder NUTRIENT = FLUIDS.register("nutrient_solution")
      .density(1100).viscosity(1500)
      .texture(WATER_STILL, WATER_FLOW).tint(0xFF33AA33)
      .block(ModBlocks.NUTRIENT).build();

  public static final Map<DyeColor, FluidDeferredRegister.FluidHolder> DYES = new EnumMap<>(DyeColor.class);
  static {
    for (DyeColor dye : DyeColor.values()) {
      DYES.put(dye, FLUIDS.register(dye.getSerializedName() + "_dye_solution")
          .density(1100).viscosity(1100)
          .texture(WATER_STILL, WATER_FLOW)
          .tint(0xFF000000 | dye.getFireworkColor())
          .block(ModBlocks.DYE_BLOCKS.get(dye))
          .build());
    }
  }

  @SubscribeEvent
  static void registerClientExtensions(RegisterClientExtensionsEvent event) {
    FLUIDS.registerClientExtensions(event);
  }

  private ModFluids() {}
}
