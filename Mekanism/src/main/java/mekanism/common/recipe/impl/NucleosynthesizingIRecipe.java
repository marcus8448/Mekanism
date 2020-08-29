package mekanism.common.recipe.impl;

import javax.annotation.Nonnull;
import mekanism.api.recipes.NucleosynthesizingRecipe;
import mekanism.api.recipes.inputs.ItemStackIngredient;
import mekanism.api.recipes.inputs.chemical.GasStackIngredient;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismRecipeSerializers;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class NucleosynthesizingIRecipe extends NucleosynthesizingRecipe {

    public NucleosynthesizingIRecipe(Identifier id, ItemStackIngredient itemInput, GasStackIngredient gasInput, ItemStack output, int duration) {
        super(id, itemInput, gasInput, output, duration);
    }

    @Nonnull
    @Override
    public RecipeType<NucleosynthesizingRecipe> getType() {
        return MekanismRecipeType.NUCLEOSYNTHESIZING;
    }

    @Nonnull
    @Override
    public RecipeSerializer<NucleosynthesizingRecipe> getSerializer() {
        return MekanismRecipeSerializers.NUCLEOSYNTHESIZING.getRecipeSerializer();
    }

    @Nonnull
    @Override
    public String getGroup() {
        return MekanismBlocks.ANTIPROTONIC_NUCLEOSYNTHESIZER.getName();
    }

    @Nonnull
    @Override
    public ItemStack getRecipeKindIcon() {
        return MekanismBlocks.ANTIPROTONIC_NUCLEOSYNTHESIZER.getItemStack();
    }
}