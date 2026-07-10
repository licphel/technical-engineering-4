package ten4.core.block.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import ten4.init.FluidInit;

public class NullFluidTicker implements FluidInit.FluidTicker
{

    public void tick(Level level, BlockPos pos, FluidState state)
    {
    }

}
