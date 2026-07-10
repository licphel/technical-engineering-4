package ten4.core.item.upgrades;

import ten4.init.TabInit;
import ten4.init.template.DefItem;
import ten4.lib.tile.mac.CmTileMachine;

public abstract class UpgradeItem extends DefItem
{

    double percent;

    public UpgradeItem(double per)
    {

        super(build(1));
        percent = per;

    }

    public void fillGroup()
    {
        TabInit.TOOLS.add(this::getDefaultInstance);
    }

    public boolean effect(CmTileMachine tile)
    {
        tile.efficientIn += tile.initialEfficientIn * percent;
        tile.info.maxStorageEnergy += tile.info.initialEnergyStorage * percent;
        tile.info.maxReceiveEnergy += tile.info.initialEnergyReceive * percent;
        tile.info.maxExtractEnergy += tile.info.initialEnergyExtract * percent;
        tile.info.maxReceiveItem += tile.info.initialItemReceive * percent;
        tile.info.maxExtractItem += tile.info.initialItemExtract * percent;
        return true;
    }

}
