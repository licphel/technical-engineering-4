package ten4.init.template;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import ten4.core.item.ICanFillGroup;
import ten4.init.FluidInit;
import ten4.init.TabInit;
import ten4.util.ComponentHelper;
import ten4.util.SafeOperationHelper;

import static ten4.init.template.DefItem.build;

public class Bucket extends BucketItem implements ICanFillGroup
{

    public Bucket(String id)
    {
        super(FluidInit.getSource(id), build(1).craftRemainder(Items.BUCKET));
    }

    public void fillGroup()
    {
        TabInit.TOOLS.add(this::getDefaultInstance);
    }

    @Override
    public @NotNull String getDescriptionId()
    {
        return ComponentHelper.getKey(SafeOperationHelper.regNameOf(this));
    }

}
