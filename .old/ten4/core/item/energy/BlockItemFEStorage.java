package ten4.core.item.energy;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import ten4.core.block.Cell;
import ten4.init.TabInit;
import ten4.init.TileInit;
import ten4.init.template.DefItemBlock;
import ten4.lib.tile.mac.CmTileEntity;
import ten4.lib.tile.mac.CmTileMachine;
import ten4.util.ItemNBTHelper;

import java.util.List;

import static ten4.init.template.DefItem.build;

public class BlockItemFEStorage extends DefItemBlock
{

    //in game item do

    private CmTileMachine getBind()
    {
        return (CmTileMachine) CmTileEntity.ofType(TileInit.getType(BuiltInRegistries.ITEM.getKey(this).getPath()));
    }

    public BlockItemFEStorage(Block b)
    {
        super(b, build(1));
    }

    public void fillGroup()
    {
        TabInit.MACHINES.add(this::getDefaultInstance);

        if(getBlock() instanceof Cell) {
            TabInit.MACHINES.add(() -> {
                CmTileMachine t = getBind();
                ItemStack full = EnergyItemHelper.getState(this, t.info.maxStorageEnergy, t.info.maxReceiveEnergy, t.info.maxExtractEnergy);
                ItemNBTHelper.setTag(full, "energy", t.info.maxStorageEnergy);
                return full;
            });
        }
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack)
    {
        CmTileMachine t = getBind();
        return t.getDisplayWith();
    }

    //in whenPlaceToWorld item do(Tab or crafting...)

    @Override
    public @NotNull ItemStack getDefaultInstance()
    {
        CmTileMachine t = getBind();
        return EnergyItemHelper.getState(this, t.info.maxStorageEnergy, t.info.maxReceiveEnergy, t.info.maxExtractEnergy);
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack)
    {
        return ItemNBTHelper.getTag(stack, "energy") != 0;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack)
    {
        if(ItemNBTHelper.getTag(stack, "maxEnergy") == 0) {
            return 0;
        }
        return (int) (13 * (ItemNBTHelper.getTag(stack, "energy") / (double) ItemNBTHelper.getTag(stack, "maxEnergy")));
    }

    @Override
    public int getBarColor(@NotNull ItemStack p_150901_)
    {
        return Mth.color(1f, 0.1f, 0.1f);
    }



    /*
    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> stacks)
    {
        if(allowedIn(tab)) {
            CmTileMachine t = getBind();
            EnergyItemHelper.fillEmpty(this, stacks, t.info.maxStorageEnergy, t.info.maxReceiveEnergy, t.info.maxExtractEnergy);

            if(getBlock() instanceof Cell) {
                EnergyItemHelper.fillFull(this, stacks, t.info.maxStorageEnergy, t.info.maxReceiveEnergy, t.info.maxExtractEnergy);
            }
        }
    }

     */

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag p_40575_)
    {
        EnergyItemHelper.addTooltip(tooltip, stack);
    }

    @Override
    public void onCraftedBy(@NotNull ItemStack stack, @NotNull Level p_41448_, @NotNull Player p_41449_)
    {
        CmTileMachine t = getBind();
        EnergyItemHelper.setState(stack, t.info.maxStorageEnergy, t.info.maxReceiveEnergy, t.info.maxExtractEnergy);
    }

}
