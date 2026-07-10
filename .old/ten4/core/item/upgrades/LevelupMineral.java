package ten4.core.item.upgrades;

import ten4.core.machine.useenergy.quarry.QuarryTile;
import ten4.lib.tile.mac.CmTileMachine;

public class LevelupMineral extends UpgradeItem
{

    public LevelupMineral()
    {
        super(0);
    }

    public boolean effect(CmTileMachine tile)
    {
        return tile instanceof QuarryTile;
    }
}
