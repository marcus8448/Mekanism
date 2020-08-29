package mekanism.common.recipe.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nonnull;
import mekanism.api.JsonConstants;
import mekanism.api.SerializerHelper;
import mekanism.api.chemical.ChemicalType;
import mekanism.api.recipes.ChemicalCrystallizerRecipe;
import mekanism.api.recipes.inputs.chemical.ChemicalStackIngredient;
import mekanism.common.Mekanism;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ChemicalCrystallizerRecipeSerializer<RECIPE extends ChemicalCrystallizerRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>>
      implements RecipeSerializer<RECIPE> {

    private final IFactory<RECIPE> factory;

    public ChemicalCrystallizerRecipeSerializer(IFactory<RECIPE> factory) {
        this.factory = factory;
    }

    @Nonnull
    @Override
    public RECIPE read(@Nonnull Identifier recipeId, @Nonnull JsonObject json) {
        ChemicalType chemicalType = SerializerHelper.getChemicalType(json);
        JsonElement input = JsonHelper.hasArray(json, JsonConstants.INPUT) ? JsonHelper.getArray(json, JsonConstants.INPUT) :
                            JsonHelper.getObject(json, JsonConstants.INPUT);
        ChemicalStackIngredient<?, ?> inputIngredient = (ChemicalStackIngredient<?, ?>) SerializerHelper.getDeserializerForType(chemicalType).deserialize(input);
        ItemStack output = SerializerHelper.getItemStack(json, JsonConstants.OUTPUT);
        if (output.isEmpty()) {
            throw new JsonSyntaxException("Recipe output must not be empty.");
        }
        return this.factory.create(recipeId, inputIngredient, output);
    }

    @Override
    public RECIPE read(@Nonnull Identifier recipeId, @Nonnull PacketByteBuf buffer) {
        try {
            ChemicalType chemicalType = buffer.readEnumConstant(ChemicalType.class);
            ChemicalStackIngredient<?, ?> inputIngredient = (ChemicalStackIngredient<?, ?>) SerializerHelper.getDeserializerForType(chemicalType).read(buffer);
            ItemStack output = buffer.readItemStack();
            return this.factory.create(recipeId, inputIngredient, output);
        } catch (Exception e) {
            Mekanism.logger.error("Error reading boxed chemical to itemstack recipe from packet.", e);
            throw e;
        }
    }

    @Override
    public void write(@Nonnull PacketByteBuf buffer, @Nonnull RECIPE recipe) {
        try {
            recipe.write(buffer);
        } catch (Exception e) {
            Mekanism.logger.error("Error writing boxed chemical to itemstack recipe to packet.", e);
            throw e;
        }
    }

    @FunctionalInterface
    public interface IFactory<RECIPE extends ChemicalCrystallizerRecipe> {

        RECIPE create(Identifier id, ChemicalStackIngredient<?, ?> input, ItemStack output);
    }
}