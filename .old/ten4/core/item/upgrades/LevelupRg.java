package ten4.core.item.upgrades;

import ten4.lib.tile.extension.CmTileMachineRadiused;
import ten4.lib.tile.mac.CmTileMachine;

public class LevelupRg extends UpgradeItem
{

    public LevelupRg()
    {
        super(0);
    }

    @Override
    public boolean effect(CmTileMachine tile)
    {
        boolean a = tile instanceof CmTileMachineRadiused;
        if(a) {
            ((CmTileMachineRadiused) tile).radius += ((CmTileMachineRadiused) tile).initialRadius * 0.5;
        }
        return a;
    }

}
