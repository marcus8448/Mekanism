package mekanism.common.recipe.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nonnull;
import mekanism.api.JsonConstants;
import mekanism.api.SerializerHelper;
import mekanism.api.recipes.FluidToFluidRecipe;
import mekanism.api.recipes.inputs.FluidStackIngredient;
import mekanism.common.Mekanism;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class FluidToFluidRecipeSerializer<RECIPE extends FluidToFluidRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<RECIPE> {

    private final IFactory<RECIPE> factory;

    public FluidToFluidRecipeSerializer(IFactory<RECIPE> factory) {
        this.factory = factory;
    }

    @Nonnull
    @Override
    public RECIPE read(@Nonnull Identifier recipeId, @Nonnull JsonObject json) {
        JsonElement input = JsonHelper.hasArray(json, JsonConstants.INPUT) ? JsonHelper.getArray(json, JsonConstants.INPUT) :
                            JsonHelper.getObject(json, JsonConstants.INPUT);
        FluidStackIngredient inputIngredient = FluidStackIngredient.deserialize(input);
        FluidStack output = SerializerHelper.getFluidStack(json, JsonConstants.OUTPUT);
        if (output.isEmpty()) {
            throw new JsonSyntaxException("Recipe output must not be empty.");
        }
        return this.factory.create(recipeId, inputIngredient, output);
    }

    @Override
    public RECIPE read(@Nonnull Identifier recipeId, @Nonnull PacketByteBuf buffer) {
        try {
            FluidStackIngredient inputIngredient = FluidStackIngredient.read(buffer);
            FluidStack output = FluidStack.readFromPacket(buffer);
            return this.factory.create(recipeId, inputIngredient, output);
        } catch (Exception e) {
            Mekanism.logger.error("Error reading fluid to fluid recipe from packet.", e);
            throw e;
        }
    }

    @Override
    public void write(@Nonnull PacketByteBuf buffer, @Nonnull RECIPE recipe) {
        try {
            recipe.write(buffer);
        } catch (Exception e) {
            Mekanism.logger.error("Error writing fluid to fluid recipe to packet.", e);
            throw e;
        }
    }

    @FunctionalInterface
    public interface IFactory<RECIPE extends FluidToFluidRecipe> {

        RECIPE create(Identifier id, FluidStackIngredient input, FluidStack output);
    }
}