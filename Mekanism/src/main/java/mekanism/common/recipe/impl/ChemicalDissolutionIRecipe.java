package mekanism.common.recipe.impl;

import javax.annotation.Nonnull;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.ChemicalDissolutionRecipe;
import mekanism.api.recipes.inputs.ItemStackIngredient;
import mekanism.api.recipes.inputs.chemical.GasStackIngredient;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismRecipeSerializers;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class ChemicalDissolutionIRecipe extends ChemicalDissolutionRecipe {

    public ChemicalDissolutionIRecipe(Identifier id, ItemStackIngredient itemInput, GasStackIngredient gasInput, ChemicalStack<?> output) {
        super(id, itemInput, gasInput, output);
    }

    @Nonnull
    @Override
    public RecipeType<ChemicalDissolutionRecipe> getType() {
        return MekanismRecipeType.DISSOLUTION;
    }

    @Nonnull
    @Override
    public RecipeSerializer<ChemicalDissolutionRecipe> getSerializer() {
        return MekanismRecipeSerializers.DISSOLUTION.getRecipeSerializer();
    }

    @Nonnull
    @Override
    public String getGroup() {
        return MekanismBlocks.CHEMICAL_DISSOLUTION_CHAMBER.getName();
    }

    @Nonnull
    @Override
    public ItemStack getRecipeKindIcon() {
        return MekanismBlocks.CHEMICAL_DISSOLUTION_CHAMBER.getItemStack();
    }
}