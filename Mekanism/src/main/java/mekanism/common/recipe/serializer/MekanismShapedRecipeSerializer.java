package mekanism.common.recipe.serializer;

import com.google.gson.JsonObject;
import javax.annotation.Nonnull;
import mekanism.common.Mekanism;
import mekanism.common.recipe.upgrade.MekanismShapedRecipe;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class MekanismShapedRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<MekanismShapedRecipe> {

    @Nonnull
    @Override
    public MekanismShapedRecipe read(@Nonnull Identifier recipeId, @Nonnull JsonObject json) {
        return new MekanismShapedRecipe(RecipeSerializer.SHAPED.read(recipeId, json));
    }

    @Override
    public MekanismShapedRecipe read(@Nonnull Identifier recipeId, @Nonnull PacketByteBuf buffer) {
        try {
            return new MekanismShapedRecipe(RecipeSerializer.SHAPED.read(recipeId, buffer));
        } catch (Exception e) {
            Mekanism.logger.error("Error reading mekanism shaped recipe from packet.", e);
            throw e;
        }
    }

    @Override
    public void write(@Nonnull PacketByteBuf buffer, @Nonnull MekanismShapedRecipe recipe) {
        try {
            RecipeSerializer.SHAPED.write(buffer, recipe.getInternal());
        } catch (Exception e) {
            Mekanism.logger.error("Error writing mekanism shaped recipe to packet.", e);
            throw e;
        }
    }
}