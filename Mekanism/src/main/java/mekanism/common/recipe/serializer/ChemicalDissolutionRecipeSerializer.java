package mekanism.common.recipe.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nonnull;
import mekanism.api.JsonConstants;
import mekanism.api.SerializerHelper;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.recipes.ChemicalDissolutionRecipe;
import mekanism.api.recipes.inputs.ItemStackIngredient;
import mekanism.api.recipes.inputs.chemical.GasStackIngredient;
import mekanism.common.Mekanism;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ChemicalDissolutionRecipeSerializer<RECIPE extends ChemicalDissolutionRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>>
      implements RecipeSerializer<RECIPE> {

    private final IFactory<RECIPE> factory;

    public ChemicalDissolutionRecipeSerializer(IFactory<RECIPE> factory) {
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
        ChemicalStack<?> output = SerializerHelper.getBoxedChemicalStack(json, JsonConstants.OUTPUT);
        if (output.isEmpty()) {
            throw new JsonSyntaxException("Recipe output must not be empty.");
        }
        return this.factory.create(recipeId, itemIngredient, gasIngredient, output);
    }

    @Override
    public RECIPE read(@Nonnull Identifier recipeId, @Nonnull PacketByteBuf buffer) {
        try {
            ItemStackIngredient itemInput = ItemStackIngredient.read(buffer);
            GasStackIngredient gasInput = GasStackIngredient.read(buffer);
            ChemicalType chemicalType = buffer.readEnumConstant(ChemicalType.class);
            ChemicalStack<?> output;
            if (chemicalType == ChemicalType.GAS) {
                output = GasStack.readFromPacket(buffer);
            } else if (chemicalType == ChemicalType.INFUSION) {
                output = InfusionStack.readFromPacket(buffer);
            } else if (chemicalType == ChemicalType.PIGMENT) {
                output = PigmentStack.readFromPacket(buffer);
            } else if (chemicalType == ChemicalType.SLURRY) {
                output = SlurryStack.readFromPacket(buffer);
            } else {
                throw new IllegalStateException("Unknown chemical type");
            }
            return this.factory.create(recipeId, itemInput, gasInput, output);
        } catch (Exception e) {
            Mekanism.logger.error("Error reading itemstack gas to gas recipe from packet.", e);
            throw e;
        }
    }

    @Override
    public void write(@Nonnull PacketByteBuf buffer, @Nonnull RECIPE recipe) {
        try {
            recipe.write(buffer);
        } catch (Exception e) {
            Mekanism.logger.error("Error writing itemstack gas to gas recipe to packet.", e);
            throw e;
        }
    }

    @FunctionalInterface
    public interface IFactory<RECIPE extends ChemicalDissolutionRecipe> {

        RECIPE create(Identifier id, ItemStackIngredient itemInput, GasStackIngredient gasInput, ChemicalStack<?> output);
    }
}