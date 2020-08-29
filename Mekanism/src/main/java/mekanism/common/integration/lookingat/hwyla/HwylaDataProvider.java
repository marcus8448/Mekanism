package mekanism.common.integration.lookingat.hwyla;

import java.util.Optional;
import mcp.mobius.waila.api.IServerDataProvider;
import mekanism.api.NBTConstants;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.merged.MergedChemicalTank.Current;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.math.FloatingLong;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.merged.MergedTank.CurrentType;
import mekanism.common.integration.lookingat.LookingAtHelper;
import mekanism.common.integration.lookingat.LookingAtUtils;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.tile.TileEntityBoundingBlock;
import mekanism.common.tile.base.TileEntityUpdateable;
import mekanism.common.util.CapabilityUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class HwylaDataProvider implements IServerDataProvider<BlockEntity> {

    static final HwylaDataProvider INSTANCE = new HwylaDataProvider();

    @Override
    public void appendServerData(CompoundTag data, ServerPlayerEntity player, World world, BlockEntity tile) {
        if (tile instanceof TileEntityBoundingBlock) {
            //If we are a bounding block that has a position set, redirect the check to the main location
            TileEntityBoundingBlock boundingBlock = (TileEntityBoundingBlock) tile;
            if (!boundingBlock.receivedCoords || tile.getPos().equals(boundingBlock.getMainPos())) {
                //If the coords haven't been received, exit
                return;
            }
            tile = MekanismUtils.getTileEntity(world, boundingBlock.getMainPos());
            if (tile == null) {
                //If there is no tile where the bounding block thinks the main tile is, exit
                return;
            }
        }
        HwylaLookingAtHelper helper = new HwylaLookingAtHelper();
        MultiblockData structure = LookingAtUtils.getMultiblock(tile);
        Optional<IStrictEnergyHandler> energyCapability = MekanismUtils.toOptional(CapabilityUtils.getCapability(tile, Capabilities.STRICT_ENERGY_CAPABILITY, null));
        if (energyCapability.isPresent()) {
            LookingAtUtils.displayEnergy(helper, energyCapability.get());
        } else if (structure != null && structure.isFormed()) {
            //Special handling to allow viewing the energy of multiblock's when looking at things other than the ports
            LookingAtUtils.displayEnergy(helper, structure);
        }
        //Fluid - only add it to our own tiles in which we disable the default display for
        if (tile instanceof TileEntityUpdateable) {
            Optional<IFluidHandler> fluidCapability = MekanismUtils.toOptional(CapabilityUtils.getCapability(tile, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null));
            if (fluidCapability.isPresent()) {
                LookingAtUtils.displayFluid(helper, fluidCapability.get());
            } else if (structure != null && structure.isFormed()) {
                //Special handling to allow viewing the fluid in a multiblock when looking at things other than the ports
                LookingAtUtils.displayFluid(helper, structure);
            }
        }
        //Chemicals
        LookingAtUtils.addInfo(tile, structure, Capabilities.GAS_HANDLER_CAPABILITY, multiblock -> multiblock.getGasTanks(null), helper, MekanismLang.GAS, Current.GAS, CurrentType.GAS);
        LookingAtUtils.addInfo(tile, structure, Capabilities.INFUSION_HANDLER_CAPABILITY, multiblock -> multiblock.getInfusionTanks(null), helper, MekanismLang.INFUSE_TYPE, Current.INFUSION, CurrentType.INFUSION);
        LookingAtUtils.addInfo(tile, structure, Capabilities.PIGMENT_HANDLER_CAPABILITY, multiblock -> multiblock.getPigmentTanks(null), helper, MekanismLang.PIGMENT, Current.PIGMENT, CurrentType.PIGMENT);
        LookingAtUtils.addInfo(tile, structure, Capabilities.SLURRY_HANDLER_CAPABILITY, multiblock -> multiblock.getSlurryTanks(null), helper, MekanismLang.SLURRY, Current.SLURRY, CurrentType.SLURRY);
        //Add our data if we have any
        helper.finalize(data);
    }

    private static class HwylaLookingAtHelper implements LookingAtHelper {

        private final ListTag data = new ListTag();

        @Override
        public void addText(Text text) {
            CompoundTag textData = new CompoundTag();
            textData.putString(MekanismHwylaPlugin.TEXT, Text.Serializer.toJson(text));
            data.add(textData);
        }

        @Override
        public void addEnergyElement(FloatingLong energy, FloatingLong maxEnergy) {
            CompoundTag energyData = new CompoundTag();
            energyData.putString(NBTConstants.ENERGY_STORED, energy.toString());
            energyData.putString(NBTConstants.MAX, maxEnergy.toString());
            data.add(energyData);
        }

        @Override
        public void addFluidElement(FluidStack stored, int capacity) {
            CompoundTag fluidData = new CompoundTag();
            fluidData.put(NBTConstants.FLUID_STORED, stored.writeToNBT(new CompoundTag()));
            fluidData.putInt(NBTConstants.MAX, capacity);
            data.add(fluidData);
        }

        @Override
        public void addChemicalElement(ChemicalStack<?> stored, long capacity) {
            CompoundTag chemicalData = new CompoundTag();
            chemicalData.put(MekanismHwylaPlugin.CHEMICAL_STACK, stored.write(new CompoundTag()));
            chemicalData.putLong(NBTConstants.MAX, capacity);
            data.add(chemicalData);
        }

        private void finalize(CompoundTag data) {
            if (!this.data.isEmpty()) {
                data.put(NBTConstants.MEK_DATA, this.data);
            }
        }
    }
}