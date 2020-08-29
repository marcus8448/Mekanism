package mekanism.api.chemical.pigment;

import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalBuilder;
import net.minecraft.util.Identifier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PigmentBuilder extends ChemicalBuilder<Pigment, PigmentBuilder> {

    protected PigmentBuilder(Identifier texture) {
        super(texture);
    }

    public static PigmentBuilder builder() {
        return builder(new Identifier(MekanismAPI.MEKANISM_MODID, "pigment/base"));
    }

    public static PigmentBuilder builder(Identifier texture) {
        return new PigmentBuilder(Objects.requireNonNull(texture));
    }
}