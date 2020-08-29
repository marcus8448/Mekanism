package mekanism.common.recipe.impl;

import javax.annotation.Nonnull;
import mekanism.api.recipes.CombinerRecipe;
import mekanism.api.recipes.inputs.ItemStackIngredient;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismRecipeSerializers;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class CombinerIRecipe extends CombinerRecipe {

    public CombinerIRecipe(Identifier id, ItemStackIngredient mainInput, ItemStackIngredient extraInput, ItemStack output) {
        super(id, mainInput, extraInput, output);
    }

    @Nonnull
    @Override
    public RecipeType<CombinerRecipe> getType() {
        return MekanismRecipeType.COMBINING;
    }

    @Nonnull
    @Override
    public RecipeSerializer<CombinerRecipe> getSerializer() {
        return MekanismRecipeSerializers.COMBINING.getRecipeSerializer();
    }

    @Nonnull
    @Override
    public String getGroup() {
        return MekanismBlocks.COMBINER.getName();
    }

    @Nonnull
    @Override
    public ItemStack getRecipeKindIcon() {
        return MekanismBlocks.COMBINER.getItemStack();
    }
}