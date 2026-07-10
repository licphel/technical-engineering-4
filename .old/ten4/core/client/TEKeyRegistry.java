package ten4.core.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.bus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class TEKeyRegistry
{

    public static final KeyMapping C_CHANGE_MODE =
            new KeyMapping(
                    "ten4.key.c",
                    KeyConflictContext.IN_GAME,
                    KeyModifier.NONE,
                    InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_C),
                    "key.categories.misc"
            );

    @SubscribeEvent
    public static void onClientSetup(RegisterKeyMappingsEvent e)
    {
        e.register(C_CHANGE_MODE);
    }

}
