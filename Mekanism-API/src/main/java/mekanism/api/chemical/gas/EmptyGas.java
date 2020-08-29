package mekanism.api.chemical.gas;

import java.util.Collections;
import java.util.Set;
import javax.annotation.Nonnull;
import mekanism.api.MekanismAPI;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public final class EmptyGas extends Gas {

    public EmptyGas() {
        super(GasBuilder.builder().hidden());
        setRegistryName(new Identifier(MekanismAPI.MEKANISM_MODID, "empty_gas"));
    }

    @Override
    public boolean isIn(@Nonnull Tag<Gas> tags) {
        //Empty gas is in no tags
        return false;
    }

    @Nonnull
    @Override
    public Set<Identifier> getTags() {
        return Collections.emptySet();
    }
}