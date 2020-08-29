package mekanism.common.recipe.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nonnull;
import mekanism.api.JsonConstants;
import mekanism.api.SerializerHelper;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.math.FloatingLong;
import mekanism.api.recipes.ElectrolysisRecipe;
import mekanism.api.recipes.inputs.FluidStackIngredient;
import mekanism.common.Mekanism;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ElectrolysisRecipeSerializer<RECIPE extends ElectrolysisRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<RECIPE> {

    private final IFactory<RECIPE> factory;

    public ElectrolysisRecipeSerializer(IFactory<RECIPE> factory) {
        this.factory = factory;
    }

    @Nonnull
    @Override
    public RECIPE read(@Nonnull Identifier recipeId, @Nonnull JsonObject json) {
        JsonElement input = JsonHelper.hasArray(json, JsonConstants.INPUT) ? JsonHelper.getArray(json, JsonConstants.INPUT) :
                            JsonHelper.getObject(json, JsonConstants.INPUT);
        FluidStackIngredient inputIngredient = FluidStackIngredient.deserialize(input);
        GasStack leftGasOutput = SerializerHelper.getGasStack(json, JsonConstants.LEFT_GAS_OUTPUT);
        GasStack rightGasOutput = SerializerHelper.getGasStack(json, JsonConstants.RIGHT_GAS_OUTPUT);
        FloatingLong energyMultiplier = FloatingLong.ONE;
        if (json.has(JsonConstants.ENERGY_MULTIPLIER)) {
            energyMultiplier = SerializerHelper.getFloatingLong(json, JsonConstants.ENERGY_MULTIPLIER);
            if (energyMultiplier.smallerThan(FloatingLong.ONE)) {
                throw new JsonSyntaxException("Expected energyMultiplier to be at least one.");
            }
        }
        if (leftGasOutput.isEmpty() || rightGasOutput.isEmpty()) {
            throw new JsonSyntaxException("Electrolysis recipe outputs must not be empty.");
        }
        return this.factory.create(recipeId, inputIngredient, energyMultiplier, leftGasOutput, rightGasOutput);
    }

    @Override
    public RECIPE read(@Nonnull Identifier recipeId, @Nonnull PacketByteBuf buffer) {
        try {
            FluidStackIngredient input = FluidStackIngredient.read(buffer);
            FloatingLong energyMultiplier = FloatingLong.readFromBuffer(buffer);
            GasStack leftGasOutput = GasStack.readFromPacket(buffer);
            GasStack rightGasOutput = GasStack.readFromPacket(buffer);
            return this.factory.create(recipeId, input, energyMultiplier, leftGasOutput, rightGasOutput);
        } catch (Exception e) {
            Mekanism.logger.error("Error reading electrolysis recipe from packet.", e);
            throw e;
        }
    }

    @Override
    public void write(@Nonnull PacketByteBuf buffer, @Nonnull RECIPE recipe) {
        try {
            recipe.write(buffer);
        } catch (Exception e) {
            Mekanism.logger.error("Error writing electrolysis recipe to packet.", e);
            throw e;
        }
    }

    @FunctionalInterface
    public interface IFactory<RECIPE extends ElectrolysisRecipe> {

        RECIPE create(Identifier id, FluidStackIngredient input, FloatingLong energyMultiplier, GasStack leftGasOutput, GasStack rightGasOutput);
    }
}