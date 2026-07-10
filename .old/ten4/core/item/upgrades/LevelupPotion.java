package ten4.core.item.upgrades;

import ten4.core.machine.useenergy.beacon.BeaconTile;
import ten4.lib.tile.mac.CmTileMachine;

public class LevelupPotion extends UpgradeItem
{

    public LevelupPotion()
    {
        super(0);
    }

    @Override
    public boolean effect(CmTileMachine tile)
    {
        return tile instanceof BeaconTile;
    }
}
