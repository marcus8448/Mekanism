package mekanism.api.chemical.infuse;

import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalBuilder;
import net.minecraft.util.Identifier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class InfuseTypeBuilder extends ChemicalBuilder<InfuseType, InfuseTypeBuilder> {

    protected InfuseTypeBuilder(Identifier texture) {
        super(texture);
    }

    public static InfuseTypeBuilder builder() {
        return builder(new Identifier(MekanismAPI.MEKANISM_MODID, "infuse_type/base"));
    }

    public static InfuseTypeBuilder builder(Identifier texture) {
        return new InfuseTypeBuilder(Objects.requireNonNull(texture));
    }
}