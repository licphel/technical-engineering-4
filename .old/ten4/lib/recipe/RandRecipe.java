package ten4.lib.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface RandRecipe extends IBaseRecipeCm
{

    default List<ItemStack> generateItems()
    {
        List<ItemStack> ss = new ArrayList<>();
        for(int i = 0; i < output().size(); i++) {
            FormsCombinedIngredient ing = output().get(i);
            ItemStack s = ing.genItem();
            ss.add(s);
        }
        return ss;
    }

    default List<FluidStack> generateFluids()
    {
        List<FluidStack> ss = new ArrayList<>();
        for(int i = 0; i < output().size(); i++) {
            FormsCombinedIngredient ing = output().get(i);
            ss.add(ing.genFluid());
        }
        return ss;
    }

    List<FormsCombinedIngredient> output();

    @Override
    default @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider p_267052_)
    {
        return output().get(0).symbolItem();
    }

}
