package mekanism.additions.common;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag.Identified;

public class AdditionsTags {

    public static class Items {

        public static final Identified<Item> BALLOONS = tag("balloons");

        public static final Identified<Item> FENCES_PLASTIC = forgeTag("fences/plastic");
        public static final Identified<Item> FENCE_GATES_PLASTIC = forgeTag("fence_gates/plastic");
        public static final Identified<Item> STAIRS_PLASTIC = forgeTag("stairs/plastic");
        public static final Identified<Item> SLABS_PLASTIC = forgeTag("slabs/plastic");
        public static final Identified<Item> STAIRS_PLASTIC_GLOW = forgeTag("stairs/plastic/glow");
        public static final Identified<Item> SLABS_PLASTIC_GLOW = forgeTag("slabs/plastic/glow");
        public static final Identified<Item> STAIRS_PLASTIC_TRANSPARENT = forgeTag("stairs/plastic/transparent");
        public static final Identified<Item> SLABS_PLASTIC_TRANSPARENT = forgeTag("slabs/plastic/transparent");

        public static final Identified<Item> GLOW_PANELS = tag("glow_panels");

        public static final Identified<Item> PLASTIC_BLOCKS = tag("plastic_blocks");
        public static final Identified<Item> PLASTIC_BLOCKS_GLOW = tag("plastic_blocks/glow");
        public static final Identified<Item> PLASTIC_BLOCKS_PLASTIC = tag("plastic_blocks/plastic");
        public static final Identified<Item> PLASTIC_BLOCKS_REINFORCED = tag("plastic_blocks/reinforced");
        public static final Identified<Item> PLASTIC_BLOCKS_ROAD = tag("plastic_blocks/road");
        public static final Identified<Item> PLASTIC_BLOCKS_SLICK = tag("plastic_blocks/slick");
        public static final Identified<Item> PLASTIC_BLOCKS_TRANSPARENT = tag("plastic_blocks/transparent");

        private static Identified<Item> forgeTag(String name) {
            return ItemTags.register("forge:" + name);
        }

        private static Identified<Item> tag(String name) {
            return ItemTags.register(MekanismAdditions.rl(name).toString());
        }
    }

    public static class Blocks {

        public static final Identified<Block> FENCES_PLASTIC = forgeTag("fences/plastic");
        public static final Identified<Block> FENCE_GATES_PLASTIC = forgeTag("fence_gates/plastic");
        public static final Identified<Block> STAIRS_PLASTIC = forgeTag("stairs/plastic");
        public static final Identified<Block> SLABS_PLASTIC = forgeTag("slabs/plastic");
        public static final Identified<Block> STAIRS_PLASTIC_GLOW = forgeTag("stairs/plastic/glow");
        public static final Identified<Block> SLABS_PLASTIC_GLOW = forgeTag("slabs/plastic/glow");
        public static final Identified<Block> STAIRS_PLASTIC_TRANSPARENT = forgeTag("stairs/plastic/transparent");
        public static final Identified<Block> SLABS_PLASTIC_TRANSPARENT = forgeTag("slabs/plastic/transparent");

        public static final Identified<Block> GLOW_PANELS = tag("glow_panels");

        public static final Identified<Block> PLASTIC_BLOCKS = tag("plastic_blocks");
        public static final Identified<Block> PLASTIC_BLOCKS_GLOW = tag("plastic_blocks/glow");
        public static final Identified<Block> PLASTIC_BLOCKS_PLASTIC = tag("plastic_blocks/plastic");
        public static final Identified<Block> PLASTIC_BLOCKS_REINFORCED = tag("plastic_blocks/reinforced");
        public static final Identified<Block> PLASTIC_BLOCKS_ROAD = tag("plastic_blocks/road");
        public static final Identified<Block> PLASTIC_BLOCKS_SLICK = tag("plastic_blocks/slick");
        public static final Identified<Block> PLASTIC_BLOCKS_TRANSPARENT = tag("plastic_blocks/transparent");

        private static Identified<Block> forgeTag(String name) {
            return BlockTags.register("forge:" + name);
        }

        private static Identified<Block> tag(String name) {
            return BlockTags.register(MekanismAdditions.rl(name).toString());
        }
    }

    public static class Entities {

        public static final Identified<EntityType<?>> CREEPERS = forgeTag("creepers");
        public static final Identified<EntityType<?>> ENDERMEN = forgeTag("endermen");

        private static Identified<EntityType<?>> forgeTag(String name) {
            return EntityTypeTags.register("forge:" + name);
        }
    }
}