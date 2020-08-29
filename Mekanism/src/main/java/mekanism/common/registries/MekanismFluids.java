package mekanism.common.registries;

import mekanism.common.ChemicalConstants;
import mekanism.common.Mekanism;
import mekanism.common.registration.impl.FluidDeferredRegister;
import mekanism.common.registration.impl.FluidRegistryObject;
import net.minecraft.block.FluidBlock;
import net.minecraft.item.BucketItem;
import net.minecraft.util.Identifier;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid.Flowing;
import net.minecraftforge.fluids.ForgeFlowingFluid.Source;

public class MekanismFluids {

    public static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(Mekanism.MODID);

    public static final FluidRegistryObject<Source, Flowing, FluidBlock, BucketItem> HYDROGEN = FLUIDS.registerLiquidChemical(ChemicalConstants.HYDROGEN);
    public static final FluidRegistryObject<Source, Flowing, FluidBlock, BucketItem> OXYGEN = FLUIDS.registerLiquidChemical(ChemicalConstants.OXYGEN);
    public static final FluidRegistryObject<Source, Flowing, FluidBlock, BucketItem> CHLORINE = FLUIDS.registerLiquidChemical(ChemicalConstants.CHLORINE);
    public static final FluidRegistryObject<Source, Flowing, FluidBlock, BucketItem> SULFUR_DIOXIDE = FLUIDS.registerLiquidChemical(ChemicalConstants.SULFUR_DIOXIDE);
    public static final FluidRegistryObject<Source, Flowing, FluidBlock, BucketItem> SULFUR_TRIOXIDE = FLUIDS.registerLiquidChemical(ChemicalConstants.SULFUR_TRIOXIDE);
    public static final FluidRegistryObject<Source, Flowing, FluidBlock, BucketItem> SULFURIC_ACID = FLUIDS.registerLiquidChemical(ChemicalConstants.SULFURIC_ACID);
    public static final FluidRegistryObject<Source, Flowing, FluidBlock, BucketItem> HYDROGEN_CHLORIDE = FLUIDS.registerLiquidChemical(ChemicalConstants.HYDROGEN_CHLORIDE);
    public static final FluidRegistryObject<Source, Flowing, FluidBlock, BucketItem> HYDROFLUORIC_ACID = FLUIDS.registerLiquidChemical(ChemicalConstants.HYDROFLUORIC_ACID);
    //Internal gases
    public static final FluidRegistryObject<Source, Flowing, FluidBlock, BucketItem> ETHENE = FLUIDS.registerLiquidChemical(ChemicalConstants.ETHENE);
    public static final FluidRegistryObject<Source, Flowing, FluidBlock, BucketItem> SODIUM = FLUIDS.registerLiquidChemical(ChemicalConstants.SODIUM);
    public static final FluidRegistryObject<Source, Flowing, FluidBlock, BucketItem> BRINE = FLUIDS.register("brine",
          fluidAttributes -> fluidAttributes.gaseous().color(0xFFFEEF9C));
    public static final FluidRegistryObject<Source, Flowing, FluidBlock, BucketItem> LITHIUM = FLUIDS.registerLiquidChemical(ChemicalConstants.LITHIUM);

    public static final FluidRegistryObject<Source, Flowing, FluidBlock, BucketItem> STEAM = FLUIDS.register("steam",
          FluidAttributes.builder(Mekanism.rl("liquid/steam"), Mekanism.rl("liquid/steam_flow")).gaseous().temperature(373));
    public static final FluidRegistryObject<Source, Flowing, FluidBlock, BucketItem> HEAVY_WATER = FLUIDS.register("heavy_water",
          FluidAttributes.builder(new Identifier("block/water_still"), new Identifier("block/water_flow")).color(0xFF0D1455));
}