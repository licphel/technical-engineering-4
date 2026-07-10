package ten4.lib.tile;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import ten4.TConst;
import ten4.lib.client.element.ElementBase;
import ten4.lib.wrapper.IntArrayCm;
import ten4.util.GuiHelper;
import ten4.util.RenderHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static ten4.lib.tile.mac.CmTileMachine.*;

public class CmScreen<T extends CmContainerMachine> extends AbstractContainerScreen<T>
{

    //all widgets
    public static final ResourceLocation handler = TConst.guiHandler;
    public final ResourceLocation BG;
    protected int texh;
    protected int texw;
    public int xSize;
    public int ySize;

    public int getYSize()
    {
        return ySize;
    }

    public int getXSize()
    {
        return xSize;
    }

    protected final ArrayList<ElementBase> widgets = new ArrayList<>();
    protected final List<Component> tooltips = new LinkedList<>();

    public T container;

    public CmScreen(T container, Inventory inv, Component titleIn, String path, int textureW, int textureH)
    {

        super(container, inv, titleIn);
        BG = TConst.asRes(path);
        texh = textureH;
        texw = textureW;
        this.container = container;

    }

    public void render(@NotNull GuiGraphics matrixStack, int mouseX, int mouseY, float partialTick)
    {
        renderBackground(matrixStack, mouseX, mouseY, partialTick);

        super.render(matrixStack, mouseX, mouseY, partialTick);

        RenderHelper.drawAll(widgets, matrixStack);
        RenderHelper.updateAll(widgets);
        RenderHelper.hangingAll(widgets, true, mouseX, mouseY);

        ElementBase element = getElementFromLocation(mouseX, mouseY);
        if(element != null) {
            element.addToolTip(tooltips);
        }

        matrixStack.renderTooltip(font, tooltips, Optional.empty(), mouseX, mouseY);
        renderTooltip(matrixStack, mouseX, mouseY);

        tooltips.clear();
    }

    boolean init;

    public void addWidgets()
    {
    }

    @Override
    protected void init()
    {

        super.init();

        if(!init) {
            addWidgets();
        }
        init = true;

        updateIJ();

    }

    protected void updateIJ()
    {
        int i = GuiHelper.getI(width, getXSize());
        int j = GuiHelper.getJ(height, getYSize());

        ElementBase e;
        for(int k = 0; k < widgets.size(); k++) {
            e = widgets.get(k);
            e.updateLocWhenFrameResize(i, j);
        }
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics matrixStack, float partialTicks, int mouseX, int mouseY)
    {

        int i = GuiHelper.getI(width, getXSize());
        int j = GuiHelper.getJ(height, getYSize());

        //renderBackground(matrixStack, mouseX, mouseY, partialTicks);

        RenderHelper.renderBackGround(matrixStack, i, j, texw, texh, BG);

    }

    public ElementBase getElementFromLocation(int mouseX, int mouseY)
    {

        for(int i = 0; i < widgets.size(); i++) {
            ElementBase element = widgets.get(i);
            if(element.checkInstr(mouseX, mouseY) && element.isVisible()) {
                return element;
            }
        }
        return null;

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {

        if(button == GLFW.GLFW_MOUSE_BUTTON_1) {
            RenderHelper.clickAll(widgets, (int) mouseX, (int) mouseY);
        }

        return super.mouseClicked(mouseX, mouseY, button);

    }

    public double pFuel()
    {

        IntArrayCm data = container.data;

        if(data.get(MAX_FUEL) != 0) {
            return ((double) data.get(FUEL)) / data.get(MAX_FUEL);
        }

        return 0;

    }

    public double pProgress()
    {

        IntArrayCm data = container.data;

        if(data.get(MAX_PROGRESS) != 0) {
            return ((double) data.get(PROGRESS)) / data.get(MAX_PROGRESS);
        }

        return 0;

    }

    public double pEnergy()
    {

        IntArrayCm data = container.data;

        if(data.get(MAX_ENERGY) != 0) {
            return ((double) data.get(ENERGY)) / data.get(MAX_ENERGY);
        }

        return 0;

    }

    public int energy()
    {

        IntArrayCm data = container.data;

        return data.get(ENERGY);

    }

    public int maxEnergy()
    {

        IntArrayCm data = container.data;

        return data.get(MAX_ENERGY);

    }

}
