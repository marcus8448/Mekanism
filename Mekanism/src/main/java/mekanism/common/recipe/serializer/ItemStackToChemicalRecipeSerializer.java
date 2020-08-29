package mekanism.common.recipe.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nonnull;
import mekanism.api.JsonConstants;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.chemical.ItemStackToChemicalRecipe;
import mekanism.api.recipes.inputs.ItemStackIngredient;
import mekanism.common.Mekanism;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class ItemStackToChemicalRecipeSerializer<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>,
      RECIPE extends ItemStackToChemicalRecipe<CHEMICAL, STACK>> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<RECIPE> {

    private final IFactory<CHEMICAL, STACK, RECIPE> factory;

    public ItemStackToChemicalRecipeSerializer(IFactory<CHEMICAL, STACK, RECIPE> factory) {
        this.factory = factory;
    }

    protected abstract STACK fromJson(@Nonnull JsonObject json, @Nonnull String key);

    protected abstract STACK fromBuffer(@Nonnull PacketByteBuf buffer);

    @Nonnull
    @Override
    public RECIPE read(@Nonnull Identifier recipeId, @Nonnull JsonObject json) {
        JsonElement input = JsonHelper.hasArray(json, JsonConstants.INPUT) ? JsonHelper.getArray(json, JsonConstants.INPUT) :
                            JsonHelper.getObject(json, JsonConstants.INPUT);
        ItemStackIngredient inputIngredient = ItemStackIngredient.deserialize(input);
        STACK output = fromJson(json, JsonConstants.OUTPUT);
        if (output.isEmpty()) {
            throw new JsonSyntaxException("Recipe output must not be empty.");
        }
        return this.factory.create(recipeId, inputIngredient, output);
    }

    @Override
    public RECIPE read(@Nonnull Identifier recipeId, @Nonnull PacketByteBuf buffer) {
        try {
            ItemStackIngredient inputIngredient = ItemStackIngredient.read(buffer);
            STACK output = fromBuffer(buffer);
            return this.factory.create(recipeId, inputIngredient, output);
        } catch (Exception e) {
            Mekanism.logger.error("Error reading itemstack to chemical recipe from packet.", e);
            throw e;
        }
    }

    @Override
    public void write(@Nonnull PacketByteBuf buffer, @Nonnull RECIPE recipe) {
        try {
            recipe.write(buffer);
        } catch (Exception e) {
            Mekanism.logger.error("Error writing itemstack to chemical recipe to packet.", e);
            throw e;
        }
    }

    @FunctionalInterface
    public interface IFactory<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, RECIPE extends ItemStackToChemicalRecipe<CHEMICAL, STACK>> {

        RECIPE create(Identifier id, ItemStackIngredient input, STACK output);
    }
}