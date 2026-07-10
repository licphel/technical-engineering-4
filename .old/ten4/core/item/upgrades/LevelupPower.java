package ten4.core.item.upgrades;

import ten4.lib.tile.mac.CmTileMachine;

public class LevelupPower extends UpgradeItem
{

    public LevelupPower()
    {
        super(0.35);
    }

    @Override
    public boolean effect(CmTileMachine tile)
    {
        tile.upgradeSlots.upgSize += 2;
        return super.effect(tile);
    }

}
