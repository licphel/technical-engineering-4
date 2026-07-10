package ten4.init;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import ten4.TConst;
import ten4.core.block.fluid.ExpTicker;
import ten4.core.block.fluid.NullFluidTicker;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class FluidInit
{

    static Map<String, BaseFlowingFluid.Properties> props = new HashMap<>();
    static Map<String, DeferredHolder<Fluid, FlowingFluid>> regs1 = new HashMap<>();
    static Map<String, DeferredHolder<Fluid, FlowingFluid>> regs2 = new HashMap<>();
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(net.minecraft.core.registries.BuiltInRegistries.FLUID, TConst.modid);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, TConst.modid);

    public static void regAll()
    {
        regFluid("liquid_xp", build("liquid_xp", 100, 10, 100, 25000, true),
                 1, 1000, 6, new ExpTicker()
        );
        regFluid("liquid_bizarrerie", build("liquid_bizarrerie", 100000, 15, -500, 1000, false),
                 2, 5000, 4, new NullFluidTicker()
        );
        /*
        regFluid("liquid_honey", build("liquid_honey", 6000, 0, 30, 25000, false),
                 4, 2000, 3);
        regFluid("liquid_royal_jelly", build("liquid_royal_jelly", 6000, 0, 30, 25000, false),
                 4, 2000, 3);
        regFluid("liquid_spicy_jelly", build("liquid_spicy_jelly", 6000, 0, 30, 25000, false),
                 4, 2000, 3);
         */
    }

    public static FluidType.Properties build(String id, int dens, int lum, int temp, int visc, boolean isGas)
    {
        FluidType.Properties bd = FluidType.Properties.create()
                .density(dens)
                .temperature(temp)
                .lightLevel(lum)
                .viscosity(visc)
                .descriptionId(TConst.modid + "." + id)
                .canPushEntity(true)
                .canConvertToSource(false)
                .canDrown(true)
                .canSwim(true)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BOTTLE_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BOTTLE_EMPTY);

        return bd;
    }

    public interface FluidTicker
    {
        void tick(Level level, BlockPos pos, FluidState state);
    }

    public static void regFluid(String id, FluidType.Properties attr, int dec, float res, int slope, FluidTicker ticker)
    {
        DeferredHolder<FluidType, FluidType> type = FLUID_TYPES.register(id, () -> {
            return new FluidType(attr)
            {
                @Override
                @SuppressWarnings("removal")
                public void initializeClient(@NotNull Consumer<IClientFluidTypeExtensions> consumer)
                {
                    consumer.accept(new IClientFluidTypeExtensions()
                    {
                        public @NotNull ResourceLocation getStillTexture()
                        {
                            return TConst.asRes("fluid/" + id);
                        }

                        public @NotNull ResourceLocation getFlowingTexture()
                        {
                            return TConst.asRes("fluid/" + id + "_flowing");
                        }
                    });
                }
            };
        });
        DeferredHolder<Fluid, FlowingFluid> reg = FLUIDS.register(id, () -> {
            return new BaseFlowingFluid.Source(props.get(id))
            {
                public void tick(@NotNull Level p_75995_, @NotNull BlockPos p_75996_, @NotNull FluidState p_75997_)
                {
                    super.tick(p_75995_, p_75996_, p_75997_);
                    ticker.tick(p_75995_, p_75996_, p_75997_);
                }
            };
        });
        DeferredHolder<Fluid, FlowingFluid> reg2 = FLUIDS.register(id + "_flowing", () -> {
            return new BaseFlowingFluid.Flowing(props.get(id));
        });
        regs1.put(id, reg);
        regs2.put(id, reg2);
        props.put(id, new BaseFlowingFluid.Properties(type, reg, reg2)
                .levelDecreasePerBlock(dec)
                .explosionResistance(res)
                .slopeFindDistance(slope)
                .tickRate(10)
                .block(() -> (LiquidBlock) BlockInit.getBlock(id))
                .bucket(() -> ItemInit.getItem(id + "_bucket"))
        );
    }

    public static FlowingFluid getSource(String id)
    {
        return regs1.get(id).get();
    }

    public static FlowingFluid getFlowing(String id)
    {
        return regs2.get(id).get();
    }

}
