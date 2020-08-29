package mekanism.api.chemical.slurry;

import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalBuilder;
import net.minecraft.item.Item;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SlurryBuilder extends ChemicalBuilder<Slurry, SlurryBuilder> {

    @Nullable
    private Tag<Item> oreTag;

    protected SlurryBuilder(Identifier texture) {
        super(texture);
    }

    public static SlurryBuilder clean() {
        return builder(new Identifier(MekanismAPI.MEKANISM_MODID, "slurry/clean"));
    }

    public static SlurryBuilder dirty() {
        return builder(new Identifier(MekanismAPI.MEKANISM_MODID, "slurry/dirty"));
    }

    public static SlurryBuilder builder(Identifier texture) {
        return new SlurryBuilder(Objects.requireNonNull(texture));
    }

    public SlurryBuilder ore(Identifier oreTagLocation) {
        return ore(ItemTags.register(Objects.requireNonNull(oreTagLocation).toString()));
    }

    public SlurryBuilder ore(Tag<Item> oreTag) {
        this.oreTag = Objects.requireNonNull(oreTag);
        return this;
    }

    @Nullable
    public Tag<Item> getOreTag() {
        return oreTag;
    }
}