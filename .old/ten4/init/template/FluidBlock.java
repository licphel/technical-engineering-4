package ten4.init.template;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.MapColor;
import ten4.init.FluidInit;

public class FluidBlock extends LiquidBlock
{

    public FluidBlock(String getter)
    {
        super(FluidInit.getSource(getter), Properties.of().mapColor(MapColor.WATER)
                .explosionResistance(100)
                .destroyTime(100));
    }

}
