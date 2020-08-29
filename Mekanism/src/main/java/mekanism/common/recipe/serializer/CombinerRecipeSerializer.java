package mekanism.common.recipe.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nonnull;
import mekanism.api.JsonConstants;
import mekanism.api.SerializerHelper;
import mekanism.api.recipes.CombinerRecipe;
import mekanism.api.recipes.inputs.ItemStackIngredient;
import mekanism.common.Mekanism;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class CombinerRecipeSerializer<RECIPE extends CombinerRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<RECIPE> {

    private final IFactory<RECIPE> factory;

    public CombinerRecipeSerializer(IFactory<RECIPE> factory) {
        this.factory = factory;
    }

    @Nonnull
    @Override
    public RECIPE read(@Nonnull Identifier recipeId, @Nonnull JsonObject json) {
        JsonElement mainInput = JsonHelper.hasArray(json, JsonConstants.MAIN_INPUT) ? JsonHelper.getArray(json, JsonConstants.MAIN_INPUT) :
                                JsonHelper.getObject(json, JsonConstants.MAIN_INPUT);
        ItemStackIngredient mainIngredient = ItemStackIngredient.deserialize(mainInput);
        JsonElement extraInput = JsonHelper.hasArray(json, JsonConstants.EXTRA_INPUT) ? JsonHelper.getArray(json, JsonConstants.EXTRA_INPUT) :
                                 JsonHelper.getObject(json, JsonConstants.EXTRA_INPUT);
        ItemStackIngredient extraIngredient = ItemStackIngredient.deserialize(extraInput);
        ItemStack output = SerializerHelper.getItemStack(json, JsonConstants.OUTPUT);
        if (output.isEmpty()) {
            throw new JsonSyntaxException("Combiner recipe output must not be empty.");
        }
        return this.factory.create(recipeId, mainIngredient, extraIngredient, output);
    }

    @Override
    public RECIPE read(@Nonnull Identifier recipeId, @Nonnull PacketByteBuf buffer) {
        try {
            ItemStackIngredient mainInput = ItemStackIngredient.read(buffer);
            ItemStackIngredient extraInput = ItemStackIngredient.read(buffer);
            ItemStack output = buffer.readItemStack();
            return this.factory.create(recipeId, mainInput, extraInput, output);
        } catch (Exception e) {
            Mekanism.logger.error("Error reading combiner recipe from packet.", e);
            throw e;
        }
    }

    @Override
    public void write(@Nonnull PacketByteBuf buffer, @Nonnull RECIPE recipe) {
        try {
            recipe.write(buffer);
        } catch (Exception e) {
            Mekanism.logger.error("Error writing combiner recipe to packet.", e);
            throw e;
        }
    }

    @FunctionalInterface
    public interface IFactory<RECIPE extends CombinerRecipe> {

        RECIPE create(Identifier id, ItemStackIngredient mainInput, ItemStackIngredient extraInput, ItemStack output);
    }
}