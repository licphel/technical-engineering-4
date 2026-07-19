package com.hypothetic.ten4.api.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class FluidDeferredRegister {
  private final String modId;
  private final DeferredRegister<FluidType> fluidTypes;
  private final DeferredRegister<Fluid> fluids;

  public FluidDeferredRegister(String modId) {
    this.modId = modId;
    this.fluidTypes = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, modId);
    this.fluids = DeferredRegister.create(BuiltInRegistries.FLUID, modId);
  }

  public FluidBuilder register(String name) {
    return new FluidBuilder(name);
  }

  public void register(IEventBus bus) {
    fluidTypes.register(bus);
    fluids.register(bus);
  }

  public void registerClientExtensions(RegisterClientExtensionsEvent event) {
    for (DeferredHolder<FluidType, ? extends FluidType> entry : fluidTypes.getEntries()) {
      if (entry.value() instanceof SimpleFluidType sft) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
          @Override
          public int getTintColor() {
            return sft.builder.tintColor;
          }

          @Override
          public ResourceLocation getStillTexture() {
            return sft.builder.stillTex;
          }

          @Override
          public ResourceLocation getFlowingTexture() {
            return sft.builder.flowingTex;
          }
        }, sft);
      }
    }
  }

  public record FluidHolder(DeferredHolder<FluidType, SimpleFluidType> type,
                            DeferredHolder<Fluid, BaseFlowingFluid.Source> still,
                            DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowing) {
  }

  public static class SimpleFluidType extends FluidType {
    public final FluidBuilder builder;

    public SimpleFluidType(Properties props, FluidBuilder builder) {
      super(props);
      this.builder = builder;
    }
  }

  public class FluidBuilder {
    String name;
    boolean canSwim = true;
    boolean canDrown = true;
    boolean canExtinguish = true;
    boolean canConvertToSource;
    boolean supportsBoating;
    boolean canHydrate;
    int lightLevel;
    int density = 1000;
    int temperature = 300;
    int viscosity = 1000;
    Rarity rarity = Rarity.COMMON;
    ResourceLocation stillTex;
    ResourceLocation flowingTex;
    int tintColor = 0xFFFFFFFF;
    boolean gas;

    int tickRate = 5;
    int slopeFindDistance = 4;
    int levelDecreasePerBlock = 1;
    float explosionResistance = 1;
    Supplier<? extends BucketItem> bucket;
    Supplier<? extends LiquidBlock> block;

    FluidBuilder(String name) {
      this.name = name;
    }

    public FluidBuilder gas(boolean v) {
      // Has neoforge handled gas?
      this.gas = v;
      return this;
    }

    public FluidBuilder density(int v) {
      density = v;
      return this;
    }

    public FluidBuilder viscosity(int v) {
      viscosity = v;
      return this;
    }

    public FluidBuilder temperature(int v) {
      temperature = v;
      return this;
    }

    public FluidBuilder lightLevel(int v) {
      lightLevel = v;
      return this;
    }

    public FluidBuilder rarity(Rarity v) {
      rarity = v;
      return this;
    }

    public FluidBuilder canSwim(boolean v) {
      canSwim = v;
      return this;
    }

    public FluidBuilder canDrown(boolean v) {
      canDrown = v;
      return this;
    }

    public FluidBuilder canExtinguish(boolean v) {
      canExtinguish = v;
      return this;
    }

    public FluidBuilder canConvertToSource(boolean v) {
      canConvertToSource = v;
      return this;
    }

    public FluidBuilder supportsBoating(boolean v) {
      supportsBoating = v;
      return this;
    }

    public FluidBuilder canHydrate(boolean v) {
      canHydrate = v;
      return this;
    }

    public FluidBuilder texture(ResourceLocation still, ResourceLocation flowing) {
      stillTex = still;
      flowingTex = flowing;
      return this;
    }

    public FluidBuilder tint(int v) {
      tintColor = v;
      return this;
    }

    public FluidBuilder tickRate(int v) {
      tickRate = v;
      return this;
    }

    public FluidBuilder slopeFindDistance(int v) {
      slopeFindDistance = v;
      return this;
    }

    public FluidBuilder levelDecreasePerBlock(int v) {
      levelDecreasePerBlock = v;
      return this;
    }

    public FluidBuilder explosionResistance(float v) {
      explosionResistance = v;
      return this;
    }

    @SuppressWarnings("unchecked")
    public FluidBuilder bucket(Supplier<? extends Item> v) {
      bucket = (Supplier<? extends BucketItem>) v;
      return this;
    }

    @SuppressWarnings("unchecked")
    public FluidBuilder block(Supplier<? extends Block> v) {
      block = (Supplier<? extends LiquidBlock>) v;
      return this;
    }

    public FluidHolder build() {
      if (stillTex == null) {
        stillTex = ResourceLocation.fromNamespaceAndPath(modId, "block/fluid/" + name);
      }
      if (flowingTex == null) {
        flowingTex = ResourceLocation.fromNamespaceAndPath(modId, "block/fluid/" + name + "_flowing");
      }

      FluidType.Properties typeProps = FluidType.Properties.create()
          .density(density)
          .viscosity(viscosity)
          .temperature(temperature)
          .lightLevel(lightLevel)
          .canSwim(canSwim)
          .canDrown(canDrown)
          .canConvertToSource(canConvertToSource)
          .supportsBoating(supportsBoating)
          .canHydrate(canHydrate)
          .canExtinguish(canExtinguish);

      DeferredHolder<FluidType, SimpleFluidType> type = fluidTypes.register(name,
          () -> new SimpleFluidType(typeProps, FluidBuilder.this));

      AtomicReference<DeferredHolder<Fluid, BaseFlowingFluid.Source>> stillRef = new AtomicReference<>();
      AtomicReference<DeferredHolder<Fluid, BaseFlowingFluid.Flowing>> flowingRef = new AtomicReference<>();

      BaseFlowingFluid.Properties fluidProps = new BaseFlowingFluid.Properties(type, () -> stillRef.get().get(), () -> flowingRef.get().get())
          .bucket(bucket)
          .block(block)
          .tickRate(tickRate)
          .slopeFindDistance(slopeFindDistance)
          .levelDecreasePerBlock(levelDecreasePerBlock)
          .explosionResistance(explosionResistance);

      DeferredHolder<Fluid, BaseFlowingFluid.Source> still = fluids.register(name, () -> new BaseFlowingFluid.Source(fluidProps));
      DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowing = fluids.register(name + "_flowing", () -> new BaseFlowingFluid.Flowing(fluidProps));
      stillRef.set(still);
      flowingRef.set(flowing);

      return new FluidHolder(type, still, flowing);
    }
  }
}
