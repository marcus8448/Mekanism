package mekanism.common.recipe.impl;

import javax.annotation.Nonnull;
import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.api.recipes.inputs.ItemStackIngredient;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.registries.MekanismRecipeSerializers;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class EnrichingIRecipe extends ItemStackToItemStackRecipe {

    public EnrichingIRecipe(Identifier id, ItemStackIngredient input, ItemStack output) {
        super(id, input, output);
    }

    @Nonnull
    @Override
    public RecipeType<ItemStackToItemStackRecipe> getType() {
        return MekanismRecipeType.ENRICHING;
    }

    @Nonnull
    @Override
    public RecipeSerializer<ItemStackToItemStackRecipe> getSerializer() {
        return MekanismRecipeSerializers.ENRICHING.getRecipeSerializer();
    }

    @Nonnull
    @Override
    public String getGroup() {
        return MekanismBlocks.ENRICHMENT_CHAMBER.getName();
    }

    @Nonnull
    @Override
    public ItemStack getRecipeKindIcon() {
        return MekanismBlocks.ENRICHMENT_CHAMBER.getItemStack();
    }
}