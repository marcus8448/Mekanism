package mekanism.generators.common;

import mekanism.api.chemical.ChemicalTags;
import mekanism.api.chemical.gas.Gas;
import mekanism.common.Mekanism;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag.Identified;

public class GeneratorTags {

    public static class Fluids {

        public static final Identified<Fluid> BIOETHANOL = forgeTag("bioethanol");
        public static final Identified<Fluid> DEUTERIUM = forgeTag("deuterium");
        public static final Identified<Fluid> FUSION_FUEL = forgeTag("fusion_fuel");
        public static final Identified<Fluid> TRITIUM = forgeTag("tritium");

        private static Identified<Fluid> forgeTag(String name) {
            return FluidTags.register("forge:" + name);
        }
    }

    public static class Gases {

        public static final Identified<Gas> DEUTERIUM = tag("deuterium");
        public static final Identified<Gas> TRITIUM = tag("tritium");
        public static final Identified<Gas> FUSION_FUEL = tag("fusion_fuel");

        private static Identified<Gas> tag(String name) {
            return ChemicalTags.gasTag(Mekanism.rl(name));
        }
    }
}