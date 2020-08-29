package mekanism.api.chemical.slurry;

import java.util.Collections;
import java.util.Set;
import javax.annotation.Nonnull;
import mekanism.api.MekanismAPI;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public final class EmptySlurry extends Slurry {

    public EmptySlurry() {
        super(SlurryBuilder.clean().hidden());
        setRegistryName(new Identifier(MekanismAPI.MEKANISM_MODID, "empty_slurry"));
    }

    @Override
    public boolean isIn(@Nonnull Tag<Slurry> tags) {
        //Empty slurry is in no tags
        return false;
    }

    @Nonnull
    @Override
    public Set<Identifier> getTags() {
        return Collections.emptySet();
    }
}