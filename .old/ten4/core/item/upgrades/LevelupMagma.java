package ten4.core.item.upgrades;

import ten4.core.machine.useenergy.quarry.QuarryTile;
import ten4.lib.tile.mac.CmTileMachine;

public class LevelupMagma extends UpgradeItem
{

    public LevelupMagma()
    {
        super(0);
    }

    public boolean effect(CmTileMachine tile)
    {
        return tile instanceof QuarryTile;
    }
}
