package mekanism.common.recipe.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nonnull;
import mekanism.api.JsonConstants;
import mekanism.api.SerializerHelper;
import mekanism.api.recipes.NucleosynthesizingRecipe;
import mekanism.api.recipes.inputs.ItemStackIngredient;
import mekanism.api.recipes.inputs.chemical.GasStackIngredient;
import mekanism.common.Mekanism;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class NucleosynthesizingRecipeSerializer<RECIPE extends NucleosynthesizingRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<RECIPE> {

    private final IFactory<RECIPE> factory;

    public NucleosynthesizingRecipeSerializer(IFactory<RECIPE> factory) {
        this.factory = factory;
    }

    @Nonnull
    @Override
    public RECIPE read(@Nonnull Identifier recipeId, @Nonnull JsonObject json) {
        JsonElement itemInput = JsonHelper.hasArray(json, JsonConstants.ITEM_INPUT) ? JsonHelper.getArray(json, JsonConstants.ITEM_INPUT) :
                                JsonHelper.getObject(json, JsonConstants.ITEM_INPUT);
        ItemStackIngredient itemIngredient = ItemStackIngredient.deserialize(itemInput);
        JsonElement gasInput = JsonHelper.hasArray(json, JsonConstants.GAS_INPUT) ? JsonHelper.getArray(json, JsonConstants.GAS_INPUT) :
                               JsonHelper.getObject(json, JsonConstants.GAS_INPUT);
        GasStackIngredient gasIngredient = GasStackIngredient.deserialize(gasInput);

        int duration;
        JsonElement ticks = json.get(JsonConstants.DURATION);
        if (!JsonHelper.isNumber(ticks)) {
            throw new JsonSyntaxException("Expected duration to be a number greater than zero.");
        }
        duration = ticks.getAsJsonPrimitive().getAsInt();
        if (duration <= 0) {
            throw new JsonSyntaxException("Expected duration to be a number greater than zero.");
        }
        ItemStack itemOutput = SerializerHelper.getItemStack(json, JsonConstants.OUTPUT);
        if (itemOutput.isEmpty()) {
            throw new JsonSyntaxException("Nucleosynthesizing item output must not be empty.");
        }
        return this.factory.create(recipeId, itemIngredient, gasIngredient, itemOutput, duration);
    }

    @Override
    public RECIPE read(@Nonnull Identifier recipeId, @Nonnull PacketByteBuf buffer) {
        try {
            ItemStackIngredient inputSolid = ItemStackIngredient.read(buffer);
            GasStackIngredient inputGas = GasStackIngredient.read(buffer);
            ItemStack outputItem = buffer.readItemStack();
            int duration = buffer.readVarInt();
            return this.factory.create(recipeId, inputSolid, inputGas, outputItem, duration);
        } catch (Exception e) {
            Mekanism.logger.error("Error reading nucleosynthesizing recipe from packet.", e);
            throw e;
        }
    }

    @Override
    public void write(@Nonnull PacketByteBuf buffer, @Nonnull RECIPE recipe) {
        try {
            recipe.write(buffer);
        } catch (Exception e) {
            Mekanism.logger.error("Error writing nucleosynthesizing recipe to packet.", e);
            throw e;
        }
    }

    @FunctionalInterface
    public interface IFactory<RECIPE extends NucleosynthesizingRecipe> {

        RECIPE create(Identifier id, ItemStackIngredient itemInput, GasStackIngredient gasInput, ItemStack outputItem, int duration);
    }
}