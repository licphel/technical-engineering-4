package ten4.init.template;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import ten4.core.block.CableBased;
import ten4.core.block.PipeBased;
import ten4.core.item.ICanFillGroup;
import ten4.init.TabInit;
import ten4.util.ComponentHelper;
import java.util.ArrayList;
import java.util.List;

public class DefItemBlock extends BlockItem implements ICanFillGroup
{

    public DefItemBlock(Block b, Properties prp)
    {
        super(b, prp);
    }

    public void fillGroup()
    {
        if(getBlock() instanceof CableBased || getBlock() instanceof PipeBased) {
            TabInit.MACHINES.add(this::getDefaultInstance);
            return;
        }
        TabInit.BLOCKS.add(this::getDefaultInstance);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag p_41424_)
    {
        List<Component> list = new ArrayList<>();

        for(int i = 0; true; i++) {
            //*getPATH!
            String k = "ten4." + BuiltInRegistries.ITEM.getKey(this).getPath() + "." + i;
            Component ttc = ComponentHelper.translated(ComponentHelper.GOLD, k);
            if(ttc.getString().equals(k)) {
                break;
            }

            list.add(ttc);
        }

        if(DefItem.shift()) {
            tooltip.addAll(list);
        }
        else if(list.size() > 0) {
            tooltip.add(ComponentHelper.translated(ComponentHelper.GOLD, "ten4.shift"));
        }
    }

    @Override
    public @NotNull String getDescriptionId()
    {
        return ComponentHelper.getKey(BuiltInRegistries.ITEM.getKey(this).getPath());
    }

}
