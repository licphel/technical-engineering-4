package ten4.core.item.energy;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import ten4.lib.tile.mac.CmTileMachine;
import ten4.util.DisplayHelper;
import ten4.util.ItemNBTHelper;

import java.util.List;

import static ten4.lib.tile.mac.CmTileMachine.ENERGY;

public class EnergyItemHelper
{

    public static void addTooltip(List<Component> tooltips, ItemStack stack)
    {

        double e = ItemNBTHelper.getTag(stack, "energy");
        double me = ItemNBTHelper.getTag(stack, "maxEnergy");

        if(e > 0 || me > 0) {
            tooltips.add(DisplayHelper.join(e, me));
        }

    }

    public static void setState(ItemStack s, int sto, int rec, int ext)
    {

        ItemNBTHelper.setTag(s, "receive", rec);
        ItemNBTHelper.setTag(s, "extract", ext);
        ItemNBTHelper.setTag(s, "maxEnergy", sto);

    }

    public static ItemStack getState(Item i, int sto, int rec, int ext)
    {

        ItemStack def = new ItemStack(i);
        setState(def, sto, rec, ext);

        return def;

    }

    public static void fillFull(Item i, NonNullList<ItemStack> stacks, int sto, int rec, int ext)
    {

        ItemStack full = getState(i, sto, rec, ext);
        ItemNBTHelper.setTag(full, "energy", sto);
        stacks.add(full);

    }

    public static void fillEmpty(Item i, NonNullList<ItemStack> stacks, int sto, int rec, int ext)
    {

        ItemStack def = getState(i, sto, rec, ext);
        stacks.add(def);

    }

    //MACHINES:

    public static ItemStack fromMachine(CmTileMachine tile, ItemStack stack)
    {

        ItemNBTHelper.setTag(stack, "energy", tile.data.get(ENERGY));
        ItemNBTHelper.setTag(stack, "receive", tile.info.maxReceiveEnergy);
        ItemNBTHelper.setTag(stack, "extract", tile.info.maxExtractEnergy);
        ItemNBTHelper.setTag(stack, "maxEnergy", tile.info.maxStorageEnergy);

        HolderLookup.Provider registries = tile.getLevel() != null ? tile.getLevel().registryAccess() : null;

        CompoundTag upgTag = new CompoundTag();
        for(int i = 0; i < tile.upgradeSlots.getInv().getContainerSize(); i++) {
            if(registries != null) {
                ItemStack upgStack = tile.upgradeSlots.getInv().getItem(i);
                if(!upgStack.isEmpty()) {
                    upgTag.put("upg" + i, upgStack.save(registries));
                }
            }
        }

        CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        nbt.put("upgrades", upgTag);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));

        return stack;

    }

    public static void pushToTile(CmTileMachine tile, ItemStack stack)
    {

        tile.data.set(ENERGY, ItemNBTHelper.getTag(stack, "energy"));

        HolderLookup.Provider registries = tile.getLevel() != null ? tile.getLevel().registryAccess() : null;

        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag nbt = data.copyTag();
        if(nbt.contains("upgrades")) {
            CompoundTag upgTag = nbt.getCompound("upgrades");
            for(int i = 0; i < tile.upgradeSlots.getInv().getContainerSize(); i++) {
                if(upgTag.contains("upg" + i) && registries != null) {
                    tile.upgradeSlots.getInv().setItem(i,
                            ItemStack.parse(registries, upgTag.getCompound("upg" + i)).orElse(ItemStack.EMPTY));
                }
            }
        }

    }

}
