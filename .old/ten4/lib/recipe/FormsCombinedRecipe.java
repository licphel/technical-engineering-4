package ten4.lib.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import ten4.init.RecipeInit;
import ten4.lib.capability.item.AdvancedInventory;
import ten4.lib.tile.extension.CmTileMachineRecipe;
import ten4.util.TagHelper;

import java.util.ArrayList;
import java.util.List;

public class FormsCombinedRecipe implements RandRecipe
{

    protected ResourceLocation recipeName;
    protected ResourceLocation serializerType;
    protected List<FormsCombinedIngredient> input;
    protected List<FormsCombinedIngredient> output;
    protected int time;

    public FormsCombinedRecipe(ResourceLocation regName, ResourceLocation seriIn,
                               List<FormsCombinedIngredient> ip,
                               List<FormsCombinedIngredient> op, int cookTimeIn)
    {
        serializerType = seriIn;
        recipeName = regName;
        time = cookTimeIn;
        input = ip;
        output = op;

        /*
        System.out.println("------------------");
        System.out.println(serializerType);
        System.out.println(serializerType);
        System.out.println(getSerializer());
        System.out.println(getType());
         */
    }

    public List<FormsCombinedIngredient> allOutputFluids()
    {
        List<FormsCombinedIngredient> ss = new ArrayList<>();
        for(FormsCombinedIngredient ing : output) {
            if(ing.form.equals("fluid")) {
                ss.add(ing);
            }
        }
        return ss;
    }

    public List<FormsCombinedIngredient> allInputFluids()
    {
        List<FormsCombinedIngredient> ss = new ArrayList<>();
        for(FormsCombinedIngredient ing : input) {
            if(ing.form.equals("fluid")) {
                ss.add(ing);
            }
        }
        return ss;
    }

    public List<FormsCombinedIngredient> allOutputItems()
    {
        List<FormsCombinedIngredient> ss = new ArrayList<>();
        for(FormsCombinedIngredient ing : output) {
            if(ing.form.equals("item")) {
                ss.add(ing);
            }
        }
        return ss;
    }

    public List<FormsCombinedIngredient> allInputItems()
    {
        List<FormsCombinedIngredient> ss = new ArrayList<>();
        for(FormsCombinedIngredient ing : input) {
            if(ing.form.equals("item")) {
                ss.add(ing);
            }
        }
        return ss;
    }

    @Override
    public boolean matches(@NotNull RecipeInput inv, @NotNull Level worldIn)
    {
        if(!(inv instanceof AdvancedInventory)) {
            return false;
        }
        CmTileMachineRecipe mac = (CmTileMachineRecipe) ((AdvancedInventory) inv).tile;

        for(FormsCombinedIngredient i : input) {
            if(!i.check(mac, mac.inventory, mac.tanks)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients()
    {
        NonNullList<Ingredient> nonNullList = NonNullList.create();
        for(FormsCombinedIngredient i : input) {
            if(i != null && i.form.equals("item")) {
                nonNullList.add(i.toOriginStackIngredients());
            }
        }
        return nonNullList;
    }

    @Override
    public int inputLimit(ItemStack stack)
    {
        for(FormsCombinedIngredient lst : input) {
            if(lst.matchItems.contains(stack.getItem())) {
                return lst.amountOrCount;
            }
            else if(lst.ifTagItem != null
                    && TagHelper.containsItem(stack.getItem(), lst.ifTagItem)) {
                return lst.amountOrCount;
            }
        }
        return 0;
    }

    @Override
    public int inputLimit(FluidStack stack)
    {
        for(FormsCombinedIngredient lst : input) {
            if(lst.matchFluids.contains(stack.getFluid())) {
                return lst.amountOrCount;
            }
            else if(lst.ifTagFluid != null
                    && TagHelper.containsFluid(stack.getFluid(), lst.ifTagFluid)) {
                return lst.amountOrCount;
            }
        }
        return 0;
    }

    @Override
    public int time()
    {
        return time;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull RecipeInput p_44001_, HolderLookup.@NotNull Provider p_267052_)
    {
        return ItemStack.EMPTY;
    }

    public List<FormsCombinedIngredient> output()
    {
        return output;
    }

    public ResourceLocation getId()
    {
        return recipeName;
    }

    @Override
    public @NotNull RecipeType<?> getType()
    {
        return RecipeInit.getRcpType(serializerType.getPath());
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer()
    {
        return RecipeInit.getRcpSRL(serializerType.getPath());
    }

    public List<FormsCombinedIngredient> input()
    {
        return input;
    }

}
