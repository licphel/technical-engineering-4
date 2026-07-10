package ten4.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.capabilities.Capabilities;
import ten4.util.DirectionHelper;

import javax.annotation.Nonnull;

public class PipeBased extends CableBased
{

    public PipeBased(MapColor m, SoundType s, String n)
    {

        super(m, s, n);

    }

    public int connectType(@Nonnull Level world, @Nonnull Direction facing, BlockPos pos)
    {

        BlockState sf = world.getBlockState(pos.offset(facing.getNormal()));

        BlockEntity t = world.getBlockEntity(pos);
        BlockEntity tf = world.getBlockEntity(pos.offset(facing.getNormal()));

        if(tf == null) {
            return 0;
        }
        if(t == null) {
            return 0;
        }

        //pos<
        //t tf
        //->facing

        boolean k = world.getCapability(Capabilities.ItemHandler.BLOCK, pos.offset(facing.getNormal()), DirectionHelper.safeOps(facing)) != null
                && world.getCapability(Capabilities.ItemHandler.BLOCK, pos, facing) != null;

        if(k) {
            if(sf.getBlock() != this) {
                return 2;
            }
            else {
                return 1;
            }
        }

        return 0;

    }

}
