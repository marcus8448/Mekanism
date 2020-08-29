package mekanism.api.chemical.gas;

import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalBuilder;
import net.minecraft.util.Identifier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GasBuilder extends ChemicalBuilder<Gas, GasBuilder> {

    protected GasBuilder(Identifier texture) {
        super(texture);
    }

    public static GasBuilder builder() {
        return builder(new Identifier(MekanismAPI.MEKANISM_MODID, "liquid/liquid"));
    }

    public static GasBuilder builder(Identifier texture) {
        return new GasBuilder(Objects.requireNonNull(texture));
    }
}