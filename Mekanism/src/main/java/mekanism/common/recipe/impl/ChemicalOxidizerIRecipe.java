package mekanism.common.recipe.impl;

import javax.annotation.Nonnull;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.ItemStackToGasRecipe;
import mekanism.api.recipes.inputs.ItemStackIngredient;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismRecipeSerializers;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class ChemicalOxidizerIRecipe extends ItemStackToGasRecipe {

    public ChemicalOxidizerIRecipe(Identifier id, ItemStackIngredient input, GasStack output) {
        super(id, input, output);
    }

    @Nonnull
    @Override
    public RecipeType<ItemStackToGasRecipe> getType() {
        return MekanismRecipeType.OXIDIZING;
    }

    @Nonnull
    @Override
    public RecipeSerializer<ItemStackToGasRecipe> getSerializer() {
        return MekanismRecipeSerializers.OXIDIZING.getRecipeSerializer();
    }

    @Nonnull
    @Override
    public String getGroup() {
        return MekanismBlocks.CHEMICAL_OXIDIZER.getName();
    }

    @Nonnull
    @Override
    public ItemStack getRecipeKindIcon() {
        return MekanismBlocks.CHEMICAL_OXIDIZER.getItemStack();
    }
}