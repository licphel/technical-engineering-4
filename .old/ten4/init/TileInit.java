package ten4.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import ten4.TConst;
import ten4.core.machine.cable.CableTile;
import ten4.core.machine.cable.CableTileClr;
import ten4.core.machine.cable.CableTileQtz;
import ten4.core.machine.cable.CableTileStar;
import ten4.core.machine.cell.CellTile;
import ten4.core.machine.channel.ChannelEnergyTile;
import ten4.core.machine.channel.ChannelFluidTile;
import ten4.core.machine.channel.ChannelItemTile;
import ten4.core.machine.engine.biomass.BiomassTile;
import ten4.core.machine.engine.extractor.ExtractorTile;
import ten4.core.machine.engine.metalizer.MetalizerTile;
import ten4.core.machine.engine.solar.SolarTile;
import ten4.core.machine.pipe.PipeBlacklistTile;
import ten4.core.machine.pipe.PipeTile;
import ten4.core.machine.pipe.PipeWhitelistTile;
import ten4.core.machine.useenergy.beacon.BeaconTile;
import ten4.core.machine.useenergy.compressor.CompressorTile;
import ten4.core.machine.useenergy.condenser.CondenserTile;
import ten4.core.machine.useenergy.encflu.EncfluTile;
import ten4.core.machine.useenergy.farm.FarmTile;
import ten4.core.machine.useenergy.indfur.IndfurTile;
import ten4.core.machine.useenergy.mobrip.MobRipTile;
import ten4.core.machine.useenergy.psionicant.PsionicantTile;
import ten4.core.machine.useenergy.pulverizer.PulverizerTile;
import ten4.core.machine.useenergy.quarry.QuarryTile;
import ten4.core.machine.useenergy.refiner.RefinerTile;
import ten4.core.machine.useenergy.smelter.FurnaceTile;
import ten4.lib.tile.mac.CmTileEntity;

import java.util.HashMap;
import java.util.Map;

public class TileInit
{

    static Map<String, DeferredHolder<BlockEntityType<?>, BlockEntityType<? extends CmTileEntity>>> regs = new HashMap<>();
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, TConst.modid);

    public static void regAll()
    {
        regTile("channel_energy", ChannelEnergyTile::new);
        regTile("channel_item", ChannelItemTile::new);
        regTile("channel_fluid", ChannelFluidTile::new);

        regTile("engine_extraction", ExtractorTile::new);
        regTile("engine_metal", MetalizerTile::new);
        regTile("engine_biomass", BiomassTile::new);
        regTile("engine_solar", SolarTile::new);

        regTile("machine_smelter", FurnaceTile::new);
        regTile("machine_farm_manager", FarmTile::new);
        regTile("machine_pulverizer", PulverizerTile::new);
        regTile("machine_compressor", CompressorTile::new);
        regTile("machine_beacon_simulator", BeaconTile::new);
        regTile("machine_mob_ripper", MobRipTile::new);
        regTile("machine_quarry", QuarryTile::new);
        regTile("machine_psionicant", PsionicantTile::new);
        regTile("machine_induction_furnace", IndfurTile::new);
        regTile("machine_enchantment_flusher", EncfluTile::new);
        regTile("machine_refiner", RefinerTile::new);
        regTile("machine_matter_condenser", CondenserTile::new);

        regTile("cable", CableTile::new);
        regTile("cable_quartz", CableTileQtz::new);
        regTile("cable_azure", CableTileClr::new);
        regTile("cable_star", CableTileStar::new);
        regTile("pipe", PipeTile::new);
        regTile("pipe_white", PipeWhitelistTile::new);
        regTile("pipe_black", PipeBlacklistTile::new);
        regTile("cell", CellTile::new);
        //regTile("pole", PoleTile::new);
    }

    public static void regTile(String id, BlockEntityType.BlockEntitySupplier<CmTileEntity> im)
    {

        DeferredHolder<BlockEntityType<?>, BlockEntityType<? extends CmTileEntity>> reg = TILES.register(id, () ->
                BlockEntityType.Builder.of(im, BlockInit.getBlock(id)).build(null));
        regs.put(id, reg);

    }

    public static BlockEntityType<? extends CmTileEntity> getType(String id)
    {

        return regs.get(id).get();

    }

}
