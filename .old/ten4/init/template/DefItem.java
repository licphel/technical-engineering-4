package ten4.init.template;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import ten4.core.item.ICanFillGroup;
import ten4.init.TabInit;
import ten4.util.ComponentHelper;
import java.util.ArrayList;
import java.util.List;

public class DefItem extends Item implements ICanFillGroup
{

    public static Properties build(int stack)
    {
        return new Properties().stacksTo(stack);
    }

    public DefItem()
    {
        this(build(64));
    }

    public DefItem(Properties prp)
    {
        super(prp);
    }

    @Override
    public @NotNull String getDescriptionId()
    {
        return ComponentHelper.getKey(BuiltInRegistries.ITEM.getKey(this).getPath());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag p_41424_)
    {
        List<Component> list = new ArrayList<>();

        for(int i = 0; true; i++) {
            //*getPATH!
            String k = "ten4." + BuiltInRegistries.ITEM.getKey(this).getPath() + "." + i;
            Component ttc = ComponentHelper.translated(ComponentHelper.GOLD, k);
            if(ttc.getString().equals(k)) {
                break;
            }

            list.add(ttc);
        }

        if(shift()) {
            tooltip.addAll(list);
        }
        else if(list.size() > 0) {
            tooltip.add(ComponentHelper.translated(ComponentHelper.GOLD, "ten4.shift"));
        }
    }

    public static boolean shift()
    {
        return GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS;
    }

    public void fillGroup()
    {
        TabInit.ITEMS.add(this::getDefaultInstance);
    }

}
