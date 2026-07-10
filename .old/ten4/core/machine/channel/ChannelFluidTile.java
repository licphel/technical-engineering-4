package ten4.core.machine.channel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import ten4.lib.capability.fluid.Tank;
import ten4.lib.capability.fluid.TankArray;
import ten4.lib.tile.mac.IngredientType;
import ten4.lib.tile.mac.TransferManager;

public class ChannelFluidTile extends ChannelTile
{

    public ChannelFluidTile(BlockPos pos, BlockState state)
    {
        super(pos, state);
        info.setCap(0, 0, 1000);

        addTank(new Tank(2000));
        addTank(new Tank(2000));
    }

    public IngredientType tankType(int tank)
    {
        return IngredientType.BOTH;
    }

    public boolean valid(int slot, FluidStack stack)
    {
        return true;
    }

    public void update()
    {
        doBaseData();
        reflection.setActive(false);
        if(!signalAllowRun()) {
            return;
        }

        ftr.transferFluid();

        if(outputs.size() > 0) {
            reflection.setActive(true);
            BlockPos pos = outputs.get(nowOutputIndex);
            ftr.transferTo(pos, null, info.maxExtractFluid);
            if(!isPosSame(pos)) {
                spiltOut(pos);
            }
        }
        if(inputs.size() > 0) {
            reflection.setActive(true);
            BlockPos pos = inputs.get(nowInputIndex);
            ftr.transferFrom(inputs.get(nowInputIndex), null, info.maxReceiveFluid);
            if(!isPosSame(pos)) {
                spiltIn(pos);
            }
        }

        cycle();
    }

    public boolean isPosSame(BlockPos pos)
    {
        return (level.getBlockEntity(pos) instanceof ChannelFluidTile);
    }

    protected boolean hasFaceCapability(int cap, Direction d)
    {
        return cap == TransferManager.CAP_FLUID &&
                (d == reflection.direction() || d == null);
    }

    IFluidHandler handler;

    public void initHandlers()
    {
        handler = new TankArray(null, this);
    }

    public IFluidHandler getFluidHandler(Direction d)
    {
        return handler;
    }

}
