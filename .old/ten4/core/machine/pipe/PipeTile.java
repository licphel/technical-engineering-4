package ten4.core.machine.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandler;
import ten4.core.block.PipeBased;
import ten4.lib.capability.net.InvHandlerWayFinding;
import ten4.lib.tile.mac.CmTileMachine;
import ten4.lib.tile.mac.IngredientType;
import ten4.lib.tile.mac.TransferManager;
import ten4.lib.tile.option.Type;

public class PipeTile extends CmTileMachine
{

    public int inventorySize()
    {
        return 0;
    }

    public PipeTile(BlockPos pos, BlockState state)
    {

        super(pos, state);

    }

    public boolean hasUpgrade()
    {
        return false;
    }

    public boolean hasSideBar()
    {
        return false;
    }

    public IngredientType slotType(int slot)
    {
        return IngredientType.IGNORE;
    }

    public boolean valid(int slot, ItemStack stack)
    {
        return true;
    }

    public IngredientType tankType(int tank)
    {
        return IngredientType.IGNORE;
    }

    public boolean valid(int slot, FluidStack stack)
    {
        return true;
    }

    @Override
    public Type typeOf()
    {
        return Type.NON_MAC;
    }

    public boolean isItemCanBeTransferred(ItemStack stack)
    {
        return true;
    }

    public int getCapacity()
    {
        return 6;
    }

    @Override
    public void update()
    {

        if(getTileAliveTime() % 10 == 0) {
            ((PipeBased) getBlockState().getBlock()).update(level, worldPosition);
            InvHandlerWayFinding.updateNet(this);
        }

    }

    @Override
    public IItemHandler getItemHandler(Direction d)
    {
        //cannot cache
        return new InvHandlerWayFinding(d, this);
    }

    @Override
    protected boolean hasFaceCapability(int cap, Direction d)
    {
        if(cap != TransferManager.CAP_ITEM) {
            return false;
        }
        return super.hasFaceCapability(cap, d);
    }

}
