package ten4.core.machine.useenergy.condenser;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import ten4.init.FluidInit;
import ten4.lib.capability.fluid.Tank;
import ten4.lib.tile.extension.CmTileMachineProcess;
import ten4.lib.tile.mac.IngredientType;
import ten4.lib.wrapper.SlotCm;
import ten4.util.TagHelper;

public class CondenserTile extends CmTileMachineProcess
{

    public CondenserTile(BlockPos pos, BlockState state)
    {

        super(pos, state);

        info.setCap(kFE(20));
        setEfficiency(30);

        addSlot(new SlotCm(this, 0, 79, 32));
        addTank(new Tank(1000));
    }

    public int inventorySize()
    {
        return 1;
    }

    public IngredientType slotType(int slot)
    {
        return IngredientType.INPUT;
    }

    public boolean valid(int slot, ItemStack stack)
    {
        return TagHelper.containsItem(stack.getItem(), TagHelper.keyItem("ten4:catalyst"));
    }

    public IngredientType tankType(int tank)
    {
        return IngredientType.OUTPUT;
    }

    public boolean valid(int slot, FluidStack stack)
    {
        return true;
    }

    public boolean cooking()
    {
        if(!ftr.selfGive(new FluidStack(FluidInit.getSource("liquid_bizarrerie"), 5), 0, 0, true)) {
            data.set(PROGRESS, 0);
            return true;
        }

        ItemStack cny = inventory.getItem(0);
        if(valid(0, cny) && getTileAliveTime() % 20 == 0) {
            cny.shrink(1);
            data.translate(PROGRESS, 200 * getActualEfficiency());
        }

        return false;
    }

    public void cookEnd()
    {
        ftr.selfGive(new FluidStack(FluidInit.getSource("liquid_bizarrerie"), 5), 0, 0, false);
    }

    public int ticks()
    {
        return 1000;
    }

}
