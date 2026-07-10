package ten4.core.block;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;
import ten4.init.template.DefBlock;

import java.util.List;

public class MetalStorageBlock extends DefBlock
{

    public MetalStorageBlock(double hs)
    {

        super(build(hs, hs, MapColor.METAL, SoundType.METAL, 0, true));

    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState p_287732_, LootParams.@NotNull Builder p_287596_)
    {
        return List.of(asItem().getDefaultInstance());
    }

}
