package ten4.core.machine.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import ten4.lib.wrapper.SlotCm;

;

public class PipeWhitelistTile extends PipeTile
{

    public int inventorySize()
    {
        return 9;
    }

    public PipeWhitelistTile(BlockPos pos, BlockState state)
    {

        super(pos, state);

        for(int x = 7, y = 35, i = 0; i < 9; i++) {
            addSlot(new SlotCm(this, i, x + 18 * i, y));
        }

    }

    @Override
    public boolean isItemCanBeTransferred(ItemStack stack)
    {
        return inventory.asAValidCheckOf(stack);
    }

}
