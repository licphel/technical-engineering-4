package ten4.core.machine.useenergy.smelter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import ten4.core.item.upgrades.LevelupBlast;
import ten4.core.item.upgrades.LevelupSmoke;
import ten4.lib.tile.extension.CmTileMachineRecipe;
import ten4.lib.tile.extension.SlotInfo;
import ten4.lib.tile.mac.IngredientType;
import ten4.lib.wrapper.SlotCm;

public class FurnaceTile extends CmTileMachineRecipe<SmeltingRecipe>
{

    public FurnaceTile(BlockPos pos, BlockState state)
    {

        super(pos, state, new SlotInfo(0, 0, 1, 1, 0, 0, 0, 0));

        info.setCap(kFE(20));
        setEfficiency(15);

        recipeType = RecipeType.SMELTING;

        addSlot(new SlotCm(this, 0, 43, 20));
        addSlot(new SlotCm(this, 1, 115, 34).withIsResultSlot());
    }

    public boolean customFitStackIn(FluidStack s, int tank)
    {
        return false;
    }

    public boolean customFitStackIn(ItemStack s, int slot)
    {
        return getRecipe(recipeType, s) != null;
    }

    public int inventorySize()
    {
        return 2;
    }

    @Override
    public void initialRecipeType()
    {
        recipeType = RecipeType.SMELTING;
    }

    public IngredientType slotType(int slot)
    {
        if(slot == 0) {
            return IngredientType.INPUT;
        }
        if(slot == 1) {
            return IngredientType.OUTPUT;
        }
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
    public void shrinkItems()
    {
        for(int i = slotInfo.i1(); i <= slotInfo.i2(); i++) {
            ItemStack stack = inventory.getItem(i);
            int c1 = 1;
            if(!stack.getCraftingRemainingItem().isEmpty()) {
                ItemStack s2 = stack.getCraftingRemainingItem();
                s2.setCount(c1);
                Block.popResource(level, worldPosition, s2);
            }
            stack.shrink(c1);
        }
    }

    public boolean cooking()
    {
        ItemStack fullAdt = recipeNow.assemble(new SingleRecipeInput(inventory.getItem(0)), level.registryAccess());

        boolean give = true;
        if(fullAdt.isEmpty()) {
            return false;
        }

        if(!itr.selfGive(fullAdt, slotInfo.o1(), slotInfo.o2(), true)) {
            give = false;
        }

        if(!give) {
            reflection.setActive(false);
            data.set(PROGRESS, 0);
            return true;
        }

        return false;
    }

    public void cookEnd()
    {
        itr.selfGive(recipeNow.assemble(new SingleRecipeInput(inventory.getItem(0)), level.registryAccess()), slotInfo.o1(), slotInfo.o2(), false);
        shrinkItems();
    }

    @Override
    public int ticks()
    {
        if((upgradeSlots.countUpgrade(LevelupSmoke.class) > 0 && getRecipe(RecipeType.SMOKING, new SingleRecipeInput(inventory.getItem(0))) != null)
                || (upgradeSlots.countUpgrade(LevelupBlast.class) > 0 && getRecipe(RecipeType.BLASTING, new SingleRecipeInput(inventory.getItem(0))) != null)) {
            return ((SmeltingRecipe) recipeNow).getCookingTime() / 4;
        }
        return ((SmeltingRecipe) recipeNow).getCookingTime() / 2;
    }

}
