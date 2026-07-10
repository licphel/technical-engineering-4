package ten4.core.machine.useenergy.encflu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import ten4.init.FluidInit;
import ten4.lib.capability.fluid.Tank;
import ten4.lib.tile.extension.CmTileMachineProcess;
import ten4.lib.tile.mac.IngredientType;
import ten4.lib.wrapper.SlotCm;

public class EncfluTile extends CmTileMachineProcess
{

    public EncfluTile(BlockPos pos, BlockState state)
    {

        super(pos, state);

        info.setCap(kFE(20));
        setEfficiency(100);

        addTank(new Tank(1000));

        addSlot(new SlotCm(this, 0, 43, 15));
        addSlot(new SlotCm(this, 1, 43, 51));
        addSlot(new SlotCm(this, 2, 115, 34).withIsResultSlot());
    }

    public boolean customFitStackIn(ItemStack s, int slot)
    {
        if(slot == 1) {
            return s.isEnchantable() || s.is(Items.BOOK);
        }
        else if(slot == 0) {
            return s.isEnchanted();
        }
        return false;
    }

    public IngredientType slotType(int slot)
    {
        if(slot == 1) {
            return IngredientType.INPUT;
        }
        else if(slot == 0) {
            if(!inventory.getItem(slot).isEnchanted()
                    && !inventory.getItem(slot).isEmpty()) {
                return IngredientType.OUTPUT;
            }
            return IngredientType.INPUT;
        }
        else if(slot == 2) {
            return IngredientType.OUTPUT;
        }
        return IngredientType.IGNORE;
    }

    public boolean valid(int slot, ItemStack stack)
    {
        if(slot == 1) {
            return stack.isEnchantable() || stack.is(Items.BOOK);
        }
        else if(slot == 0) {
            return stack.isEnchanted();
        }
        return false;
    }

    public IngredientType tankType(int tank)
    {
        return IngredientType.OUTPUT;
    }

    public boolean valid(int slot, FluidStack stack)
    {
        return true;
    }

    public int inventorySize()
    {
        return 3;
    }

    public boolean conditionStart()
    {
        ItemStack tool = inventory.getItem(0);
        ItemStack expectedBook = inventory.getItem(1);
        boolean canStart1 =
                (expectedBook.isEnchantable() || expectedBook.is(Items.BOOK))
                        || ftr.selfGive(getXPFluid(), 0, 0, true);
        return tool.isEnchanted() && inventory.getItem(2).isEmpty() && canStart1;
    }

    public void cookEnd()
    {
        ItemStack tool = inventory.getItem(0);
        ItemStack expectedBook = inventory.getItem(1);

        ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(tool);
        if(enchantments.isEmpty()) {
            return;
        }

        if(expectedBook.is(Items.BOOK)) {
            ItemStack eb = Items.ENCHANTED_BOOK.getDefaultInstance();
            EnchantmentHelper.setEnchantments(eb, enchantments);
            inventory.setItem(2, eb);
        }
        else if(expectedBook.isEnchantable()) {
            ItemStack result = expectedBook.copyWithCount(1);
            EnchantmentHelper.setEnchantments(result, enchantments);
            inventory.setItem(2, result);
            expectedBook.shrink(1);
        }
        else {
            ftr.selfGive(getXPFluid(), 0, 0, false);
        }
        expectedBook.shrink(1);
        tool.remove(DataComponents.ENCHANTMENTS);
        tool.remove(DataComponents.STORED_ENCHANTMENTS);
    }

    private FluidStack getXPFluid()
    {
        ItemStack tool = inventory.getItem(0);
        ItemEnchantments ench = tool.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        int l = Math.max(1, ench.size() * 25);

        return new FluidStack(FluidInit.getSource("liquid_xp"), l);
    }

    @Override
    public int ticks()
    {
        return 800;
    }

}
