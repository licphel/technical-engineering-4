package ten4.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import ten4.core.block.mac.WateredMachine6;

public class Channel extends WateredMachine6
{

    final VoxelShape CEILING = Block.box
            (1, 13, 1, 15, 16, 15);
    final VoxelShape FLOOR = Block.box
            (1, 0, 1, 15, 3, 15);
    final VoxelShape NORTH = Block.box
            (1, 1, 0, 15, 15, 3);
    final VoxelShape SOUTH = Block.box
            (1, 1, 13, 15, 15, 16);
    final VoxelShape WEST = Block.box
            (0, 1, 1, 3, 15, 15);
    final VoxelShape EAST = Block.box
            (13, 1, 1, 16, 15, 15);

    public Channel(String name)
    {
        super(name);
    }

    public VoxelShape nshape(BlockState s)
    {
        switch(s.getValue(direction)) {
            case NORTH -> {
                return NORTH;
            }
            case EAST -> {
                return EAST;
            }
            case WEST -> {
                return WEST;
            }
            case SOUTH -> {
                return SOUTH;
            }
            case UP -> {
                return CEILING;
            }
            case DOWN -> {
                return FLOOR;
            }
        }
        return NORTH;
    }

    public @NotNull VoxelShape getShape(@NotNull BlockState p_60555_, @NotNull BlockGetter p_60556_, @NotNull BlockPos p_60557_, @NotNull CollisionContext p_60558_)
    {
        return nshape(p_60555_);
    }

    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState p_60572_, @NotNull BlockGetter p_60573_, @NotNull BlockPos p_60574_, @NotNull CollisionContext p_60575_)
    {
        return nshape(p_60572_);
    }

}
