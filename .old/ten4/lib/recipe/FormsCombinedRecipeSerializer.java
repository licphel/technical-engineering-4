package ten4.lib.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;
import ten4.TConst;

import java.util.ArrayList;
import java.util.List;

public class FormsCombinedRecipeSerializer implements CmSerializer<FormsCombinedRecipe>
{

    public final ResourceLocation serializerType;
    public int sizeOut;
    public int sizeInp;

    public FormsCombinedRecipeSerializer(String reg, int si, int so)
    {

        serializerType = ResourceLocation.fromNamespaceAndPath(TConst.modid, reg);
        sizeInp = si;
        sizeOut = so;
    }

    @Override
    public ResourceLocation id()
    {
        return serializerType;
    }

    @Override
    public @NotNull MapCodec<FormsCombinedRecipe> codec()
    {
        return MapCodec.unit(() -> {
            List<FormsCombinedIngredient> ip = new ArrayList<>();
            List<FormsCombinedIngredient> op = new ArrayList<>();
            for(int i = 0; i < sizeInp; i++) ip.add(EMPTY());
            for(int i = 0; i < sizeOut; i++) op.add(EMPTY());
            return new FormsCombinedRecipe(serializerType, serializerType, ip, op, 150);
        });
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, FormsCombinedRecipe> streamCodec()
    {
        return StreamCodec.of(
                (buf, recipe) -> toNetwork(buf, recipe),
                (buf) -> fromNetwork(serializerType, buf)
        );
    }

    public FormsCombinedRecipe fromJson(ResourceLocation recipeId, JsonObject json)
    {

        List<FormsCombinedIngredient> ip = getInputs(recipeId, json);
        List<FormsCombinedIngredient> op = getOutputs(recipeId, json);
        int time = JsonParser.getIntOr(json, "time", fallBackTime);

        return new FormsCombinedRecipe(recipeId, serializerType, ip, op, time);

    }

    public FormsCombinedRecipe fromNetwork(ResourceLocation recipeId, RegistryFriendlyByteBuf buffer)
    {

        List<FormsCombinedIngredient> ip = new ArrayList<>();
        List<FormsCombinedIngredient> op = new ArrayList<>();
        for(int i = 0; i < sizeInp; i++) {
            ip.add(FormsCombinedIngredient.parseFrom(recipeId, buffer));
        }
        for(int i = 0; i < sizeOut; i++) {
            op.add(FormsCombinedIngredient.parseFrom(recipeId, buffer));
        }
        int cook = buffer.readInt();

        return new FormsCombinedRecipe(recipeId, serializerType, ip, op, cook);
    }

    public void toNetwork(RegistryFriendlyByteBuf buffer, FormsCombinedRecipe recipe)
    {

        for(FormsCombinedIngredient ip : recipe.input) {
            ip.writeTo(buffer);
        }
        for(FormsCombinedIngredient op : recipe.output) {
            op.writeTo(buffer);
        }
        buffer.writeInt(recipe.time);
    }

    public List<FormsCombinedIngredient> getInputs(ResourceLocation id, JsonObject json)
    {
        List<FormsCombinedIngredient> lst = new ArrayList<>();
        JsonArray array = GsonHelper.getAsJsonArray(json, "inputs");
        for(JsonElement e : array) {
            JsonObject o = e.getAsJsonObject();
            lst.add(FormsCombinedIngredient.parseFrom(id, o));
        }
        while(lst.size() < sizeInp) {
            lst.add(EMPTY());
        }
        return lst;
    }

    private static FormsCombinedIngredient EMPTY()
    {
        FormsCombinedIngredient ig = FormsCombinedIngredient.create(0, "item", "static", ResourceLocation.parse("air"), ResourceLocation.fromNamespaceAndPath(".old/ten4", "empty_recipe"), 0);
        ig.ALLOW_ALL = true;
        return ig;
    }

    public List<FormsCombinedIngredient> getOutputs(ResourceLocation id, JsonObject json)
    {
        List<FormsCombinedIngredient> lst = new ArrayList<>();
        JsonArray array = GsonHelper.getAsJsonArray(json, "outputs");
        for(JsonElement e : array) {
            JsonObject o = e.getAsJsonObject();
            String form = JsonParser.getString(o, "form");
            String key = JsonParser.getString(o, "key");
            int count = 0;
            switch(form) {
                case "fluid":
                    count = JsonParser.getIntOr(o, "amount", 0);
                    break;
                case "item":
                    count = JsonParser.getIntOr(o, "count", 1);
                    break;
            }
            double chance = JsonParser.getFloatOr(o, "chance", 1);
            lst.add(FormsCombinedIngredient.create(count, form, "static", ResourceLocation.parse(key), id, chance));
        }
        while(lst.size() < sizeOut) {
            lst.add(EMPTY());
        }
        return lst;
    }

}
