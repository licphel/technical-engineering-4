package ten4.lib.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public interface CmSerializer<T extends Recipe<?>> extends RecipeSerializer<T>
{

    public static int fallBackTime = 150;

    ResourceLocation id();

    @Override
    MapCodec<T> codec();

    @Override
    StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();

}
