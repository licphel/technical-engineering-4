package ten4.init.template;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class InvisibleItem extends DefItem
{

    public InvisibleItem()
    {

        super(new Properties());

    }

    @Override
    public void inventoryTick(ItemStack stack, @NotNull Level p_41405_, @NotNull Entity p_41406_, int p_41407_, boolean p_41408_)
    {
        stack.setCount(0);
    }
}
