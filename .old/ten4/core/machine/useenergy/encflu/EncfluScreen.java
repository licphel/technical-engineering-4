package ten4.core.machine.useenergy.encflu;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import ten4.lib.client.element.ElementBurnLeft;
import ten4.lib.client.element.ElementFluid;
import ten4.lib.client.element.ElementProgress;
import ten4.lib.tile.CmContainerMachine;
import ten4.lib.tile.CmScreenMachine;

public class EncfluScreen extends CmScreenMachine
{

    public EncfluScreen(CmContainerMachine screenContainer, Inventory inv, Component titleIn)
    {

        super(screenContainer, inv, titleIn, "textures/gui/enchantment_flusher.png", 256, 256);
        xSize = 176;
        ySize = 166;

    }

    ElementBurnLeft energy;
    ElementBurnLeft left;
    ElementProgress progress;
    ElementFluid fluid;

    public void addWidgets()
    {

        super.addWidgets();

        widgets.add(energy = getDefaultEne());
        widgets.add(left = new ElementBurnLeft(45, 36, 13, 13, 14, 0, handler));
        widgets.add(progress = new ElementProgress(76, 35, 22, 16, 27, 127, handler, true));
        widgets.add(fluid = new ElementFluid(140, 17, 18, 50, 0, 92, handler, 0, true));
    }

    public void update()
    {

        fluid.update(container);
        energy.setPer(pEnergy());
        energy.setValue(energy(), maxEnergy());
        left.setPer(pEnergy());
        progress.setPer(pProgress());

    }

}
