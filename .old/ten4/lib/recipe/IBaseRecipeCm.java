package ten4.lib.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

public interface IBaseRecipeCm extends Recipe<RecipeInput>
{

    int time();

    int inputLimit(ItemStack stack);

    int inputLimit(FluidStack stack);

    default boolean isSpecial()
    {
        return true;
    }

    @Override
    default boolean canCraftInDimensions(int p_43999_, int p_44000_)
    {
        return true;
    }

}
