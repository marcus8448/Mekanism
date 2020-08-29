package mekanism.api.datagen.tag;

import java.util.Arrays;
import java.util.Collection;
import net.minecraft.tag.Tag;
import net.minecraft.tag.Tag.Identified;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;

//Based off of TagsProvider.Builder
public class ForgeRegistryTagBuilder<TYPE extends IForgeRegistryEntry<TYPE>> {

    private final Tag.Builder builder;
    private final String modID;

    public ForgeRegistryTagBuilder(Tag.Builder builder, String modID) {
        this.builder = builder;
        this.modID = modID;
    }

    public ForgeRegistryTagBuilder<TYPE> add(TYPE element) {
        this.builder.add(element.getRegistryName(), modID);
        return this;
    }

    @SafeVarargs
    public final ForgeRegistryTagBuilder<TYPE> add(TYPE... elements) {
        for (TYPE element : elements) {
            add(element);
        }
        return this;
    }

    public ForgeRegistryTagBuilder<TYPE> add(Identified<TYPE> tag) {
        this.builder.addTag(tag.getId(), modID);
        return this;
    }

    @SafeVarargs
    public final ForgeRegistryTagBuilder<TYPE> add(Identified<TYPE>... tags) {
        for (Identified<TYPE> tag : tags) {
            add(tag);
        }
        return this;
    }

    public ForgeRegistryTagBuilder<TYPE> add(Tag.Entry tag) {
        builder.add(tag, modID);
        return this;
    }

    public ForgeRegistryTagBuilder<TYPE> replace() {
        return replace(true);
    }

    public ForgeRegistryTagBuilder<TYPE> replace(boolean value) {
        builder.replace(value);
        return this;
    }

    public ForgeRegistryTagBuilder<TYPE> addOptional(final Identifier... locations) {
        return addOptional(Arrays.asList(locations));
    }

    public ForgeRegistryTagBuilder<TYPE> addOptional(final Collection<Identifier> locations) {
        return add(ForgeHooks.makeOptionalTag(true, locations));
    }

    public ForgeRegistryTagBuilder<TYPE> addOptionalTag(final Identifier... locations) {
        return addOptionalTag(Arrays.asList(locations));
    }

    public ForgeRegistryTagBuilder<TYPE> addOptionalTag(final Collection<Identifier> locations) {
        return add(ForgeHooks.makeOptionalTag(false, locations));
    }
}