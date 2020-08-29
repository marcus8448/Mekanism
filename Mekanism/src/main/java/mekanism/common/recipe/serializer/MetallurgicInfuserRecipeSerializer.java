package mekanism.common.recipe.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nonnull;
import mekanism.api.JsonConstants;
import mekanism.api.SerializerHelper;
import mekanism.api.recipes.MetallurgicInfuserRecipe;
import mekanism.api.recipes.inputs.ItemStackIngredient;
import mekanism.api.recipes.inputs.chemical.InfusionStackIngredient;
import mekanism.common.Mekanism;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class MetallurgicInfuserRecipeSerializer<RECIPE extends MetallurgicInfuserRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<RECIPE> {

    private final IFactory<RECIPE> factory;

    public MetallurgicInfuserRecipeSerializer(IFactory<RECIPE> factory) {
        this.factory = factory;
    }

    @Nonnull
    @Override
    public RECIPE read(@Nonnull Identifier recipeId, @Nonnull JsonObject json) {
        JsonElement itemInput = JsonHelper.hasArray(json, JsonConstants.ITEM_INPUT) ? JsonHelper.getArray(json, JsonConstants.ITEM_INPUT) :
                                JsonHelper.getObject(json, JsonConstants.ITEM_INPUT);
        ItemStackIngredient itemIngredient = ItemStackIngredient.deserialize(itemInput);
        JsonElement infusionInput = JsonHelper.hasArray(json, JsonConstants.INFUSION_INPUT) ? JsonHelper.getArray(json, JsonConstants.INFUSION_INPUT) :
                                    JsonHelper.getObject(json, JsonConstants.INFUSION_INPUT);
        InfusionStackIngredient infusionIngredient = InfusionStackIngredient.deserialize(infusionInput);
        ItemStack output = SerializerHelper.getItemStack(json, JsonConstants.OUTPUT);
        if (output.isEmpty()) {
            throw new JsonSyntaxException("Recipe output must not be empty.");
        }
        return this.factory.create(recipeId, itemIngredient, infusionIngredient, output);
    }

    @Override
    public RECIPE read(@Nonnull Identifier recipeId, @Nonnull PacketByteBuf buffer) {
        try {
            ItemStackIngredient itemInput = ItemStackIngredient.read(buffer);
            InfusionStackIngredient infusionInput = InfusionStackIngredient.read(buffer);
            ItemStack output = buffer.readItemStack();
            return this.factory.create(recipeId, itemInput, infusionInput, output);
        } catch (Exception e) {
            Mekanism.logger.error("Error reading metallurgic infuser recipe from packet.", e);
            throw e;
        }
    }

    @Override
    public void write(@Nonnull PacketByteBuf buffer, @Nonnull RECIPE recipe) {
        try {
            recipe.write(buffer);
        } catch (Exception e) {
            Mekanism.logger.error("Error writing metallurgic infuser recipe to packet.", e);
            throw e;
        }
    }

    @FunctionalInterface
    public interface IFactory<RECIPE extends MetallurgicInfuserRecipe> {

        RECIPE create(Identifier id, ItemStackIngredient itemInput, InfusionStackIngredient infusionInput, ItemStack output);
    }
}