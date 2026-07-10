package ten4.core.machine.channel;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import ten4.lib.client.element.ElementBurnLeft;
import ten4.lib.tile.CmContainerMachine;

public class ChannelScreenEnergy extends ChannelScreen
{

    public ChannelScreenEnergy(CmContainerMachine screenContainer, Inventory inv, Component titleIn)
    {
        super(screenContainer, inv, titleIn, "textures/gui/channel.png");
    }

    ElementBurnLeft energy;

    public void addWidgets()
    {

        super.addWidgets();
        widgets.add(energy = getDefaultEne());
    }

    public void update()
    {

        super.update();
        energy.setPer(pEnergy());
        energy.setValue(energy(), maxEnergy());

    }

}
