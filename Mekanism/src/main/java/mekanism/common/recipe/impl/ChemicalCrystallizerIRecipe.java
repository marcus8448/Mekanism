package mekanism.common.recipe.impl;

import javax.annotation.Nonnull;
import mekanism.api.recipes.ChemicalCrystallizerRecipe;
import mekanism.api.recipes.inputs.chemical.ChemicalStackIngredient;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismRecipeSerializers;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class ChemicalCrystallizerIRecipe extends ChemicalCrystallizerRecipe {

    public ChemicalCrystallizerIRecipe(Identifier id, ChemicalStackIngredient<?, ?> input, ItemStack output) {
        super(id, input, output);
    }

    @Nonnull
    @Override
    public RecipeType<ChemicalCrystallizerRecipe> getType() {
        return MekanismRecipeType.CRYSTALLIZING;
    }

    @Nonnull
    @Override
    public RecipeSerializer<ChemicalCrystallizerRecipe> getSerializer() {
        return MekanismRecipeSerializers.CRYSTALLIZING.getRecipeSerializer();
    }

    @Nonnull
    @Override
    public String getGroup() {
        return MekanismBlocks.CHEMICAL_CRYSTALLIZER.getName();
    }

    @Nonnull
    @Override
    public ItemStack getRecipeKindIcon() {
        return MekanismBlocks.CHEMICAL_CRYSTALLIZER.getItemStack();
    }
}