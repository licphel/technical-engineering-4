package ten4.core.item.upgrades;

import ten4.lib.tile.mac.CmTileMachine;

public class LevelupAug extends UpgradeItem
{

    public LevelupAug()
    {
        super(0.2);
    }

    @Override
    public boolean effect(CmTileMachine tile)
    {
        tile.upgradeSlots.upgSize += 1;
        return super.effect(tile);
    }
}
