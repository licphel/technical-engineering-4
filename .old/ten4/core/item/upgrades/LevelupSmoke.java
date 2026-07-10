package ten4.core.item.upgrades;

import ten4.core.machine.useenergy.smelter.FurnaceTile;
import ten4.lib.tile.mac.CmTileMachine;

public class LevelupSmoke extends UpgradeItem
{

    public LevelupSmoke()
    {
        super(0);
    }

    @Override
    public boolean effect(CmTileMachine tile)
    {
        return tile instanceof FurnaceTile;
    }

}
