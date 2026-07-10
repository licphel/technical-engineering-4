package ten4.core.item.upgrades;

import ten4.lib.tile.mac.CmTileMachine;

public class LevelupKnow extends UpgradeItem
{

    public LevelupKnow()
    {
        super(0);
    }

    @Override
    public boolean effect(CmTileMachine tile)
    {
        tile.upgradeSlots.upgSize = 6;
        return true;
    }
}
