package ten4.lib.capability.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import ten4.lib.tile.mac.CmTileMachine;
import ten4.lib.tile.option.FaceOption;
import ten4.util.DirectionHelper;

import java.util.Queue;

import static ten4.lib.tile.mac.CmTileMachine.ENERGY;

@SuppressWarnings("all")
public class EnergyTransferor
{

    final CmTileMachine t;

    public EnergyTransferor(CmTileMachine t)
    {
        this.t = t;
    }

    public final Queue<Direction> energyQR = DirectionHelper.newQueueOffer();

    public void transferEnergy()
    {
        //if(getTileAliveTime() % 2 == 0) {
        energyQR.offer(energyQR.remove());
        for(Direction d : energyQR) {
            transferTo(d, t.info.maxExtractEnergy);
            transferFrom(d, t.info.maxReceiveEnergy);
        }
        //}
    }

    public static IEnergyStorage handlerOf(BlockEntity e, Direction d)
    {
        if (e == null || e.getLevel() == null) return null;
        return e.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, e.getBlockPos(), d);
    }

    private BlockEntity checkTile(Direction d)
    {
        return checkTile(t.getBlockPos().offset(d.getNormal()));
    }

    private BlockEntity checkTile(BlockPos pos)
    {
        return t.getLevel().getBlockEntity(pos);
    }

    public void transferTo(BlockPos p, Direction d, int v)
    {

        if(d != null) {
            if(FaceOption.isPassive(t.info.direCheckEnergy(d))) {
                return;
            }
            if(!FaceOption.isOut(t.info.direCheckEnergy(d))) {
                return;
            }
        }

        BlockEntity tile = checkTile(p);

        if(tile != null) {
            IEnergyStorage e = handlerOf(tile, DirectionHelper.safeOps(d));
            if(e == null) {
                return;
            }
            if(e.canReceive()) {
                int diff = e.receiveEnergy(Math.min(v, t.data.get(ENERGY)), false);
                if(diff != 0) {
                    t.data.translate(ENERGY, -diff);
                }
            }
        }

    }

    public void transferFrom(BlockPos p, Direction d, int v)
    {

        if(d != null) {
            if(FaceOption.isPassive(t.info.direCheckEnergy(d))) {
                return;
            }
            if(!FaceOption.isIn(t.info.direCheckEnergy(d))) {
                return;
            }
        }

        BlockEntity tile = checkTile(p);

        if(tile != null) {
            IEnergyStorage e = handlerOf(tile, DirectionHelper.safeOps(d));
            if(e == null) {
                return;
            }
            if(e.canExtract()) {
                int diff = e.extractEnergy(Math.min(v, t.info.maxStorageEnergy - t.data.get(ENERGY)), false);
                if(diff != 0) {
                    t.data.translate(ENERGY, diff);
                }
            }
        }

    }

    public void transferTo(Direction d, int v)
    {
        transferTo(t.getBlockPos().offset(d.getNormal()), d, v);
    }

    public void transferFrom(Direction d, int v)
    {
        transferFrom(t.getBlockPos().offset(d.getNormal()), d, v);
    }

    public int getSizeCan()
    {
        int size = 0;

        BlockEntity tile;

        for(Direction d : Direction.values()) {
            tile = checkTile(d);
            if(tile != null) {
                if(handlerOf(tile, DirectionHelper.safeOps(d)) != null) {
                    size++;
                }
            }
        }

        return size;
    }

}
