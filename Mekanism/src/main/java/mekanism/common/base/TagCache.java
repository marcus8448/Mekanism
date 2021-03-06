package mekanism.common.base;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import mekanism.api.chemical.ChemicalStack;
import mekanism.common.block.BlockBounding;
import mekanism.common.lib.WildcardMatcher;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public final class TagCache {

    private static final Map<String, List<ItemStack>> blockTagStacks = new Object2ObjectOpenHashMap<>();
    private static final Map<String, List<ItemStack>> itemTagStacks = new Object2ObjectOpenHashMap<>();
    private static final Map<String, List<ItemStack>> modIDStacks = new Object2ObjectOpenHashMap<>();

    public static void resetTagCaches() {
        blockTagStacks.clear();
        itemTagStacks.clear();
    }

    public static List<String> getItemTags(ItemStack check) {
        return check == null || check.isEmpty() ? Collections.emptyList() : getTagsAsStrings(check.getItem().getTags());
    }

    public static List<String> getBlockTags(ItemStack check) {
        if (check == null || check.isEmpty()) {
            return Collections.emptyList();
        }
        Item item = check.getItem();
        if (item instanceof BlockItem) {
            return getTagsAsStrings(((BlockItem) item).getBlock().getTags());
        }
        return Collections.emptyList();
    }

    public static List<String> getFluidTags(FluidStack check) {
        return check == null || check.isEmpty() ? Collections.emptyList() : getTagsAsStrings(check.getFluid().getTags());
    }

    public static List<String> getChemicalTags(ChemicalStack<?> check) {
        return check == null || check.isEmpty() ? Collections.emptyList() : getTagsAsStrings(check.getType().getTags());
    }

    public static List<String> getTagsAsStrings(Set<Identifier> tags) {
        return tags.stream().map(Identifier::toString).collect(Collectors.toList());
    }

    public static List<ItemStack> getItemTagStacks(String oreName) {
        if (itemTagStacks.get(oreName) != null) {
            return itemTagStacks.get(oreName);
        }
        TagContainer<Item> tagCollection = ItemTags.getContainer();
        List<Identifier> keys = tagCollection.getKeys().stream().filter(rl -> WildcardMatcher.matches(oreName, rl.toString())).collect(Collectors.toList());
        Set<Item> items = new HashSet<>();
        for (Identifier key : keys) {
            Tag<Item> itemTag = tagCollection.get(key);
            if (itemTag != null) {
                items.addAll(itemTag.values());
            }
        }
        List<ItemStack> stacks = items.stream().map(ItemStack::new).collect(Collectors.toList());
        itemTagStacks.put(oreName, stacks);
        return stacks;
    }

    public static List<ItemStack> getBlockTagStacks(String oreName) {
        if (blockTagStacks.get(oreName) != null) {
            return blockTagStacks.get(oreName);
        }
        TagContainer<Block> tagCollection = BlockTags.getContainer();
        List<Identifier> keys = tagCollection.getKeys().stream().filter(rl -> WildcardMatcher.matches(oreName, rl.toString())).collect(Collectors.toList());
        Set<Block> blocks = new HashSet<>();
        for (Identifier key : keys) {
            Tag<Block> blockTag = tagCollection.get(key);
            if (blockTag != null) {
                blocks.addAll(blockTag.values());
            }
        }
        List<ItemStack> stacks = blocks.stream().map(ItemStack::new).collect(Collectors.toList());
        blockTagStacks.put(oreName, stacks);
        return stacks;
    }

    public static List<ItemStack> getModIDStacks(String modName, boolean forceBlock) {
        if (modIDStacks.get(modName) != null) {
            return modIDStacks.get(modName);
        }
        List<ItemStack> stacks = new ArrayList<>();
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            if (!forceBlock || item instanceof BlockItem) {
                //Ugly check to make sure we don't include our bounding block in render list. Eventually this should use getRenderType() with a dummy BlockState
                if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof BlockBounding) {
                    continue;
                }
                if (WildcardMatcher.matches(modName, item.getRegistryName().getNamespace())) {
                    stacks.add(new ItemStack(item));
                }
            }
        }
        modIDStacks.put(modName, stacks);
        return stacks;
    }
}