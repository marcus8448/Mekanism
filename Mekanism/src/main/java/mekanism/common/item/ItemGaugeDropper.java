package mekanism.common.item;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.fluid.IExtendedFluidHandler;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.capabilities.merged.GaugeDropperContentsHandler;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.StorageUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class ItemGaugeDropper extends Item {

    public ItemGaugeDropper(Settings properties) {
        super(properties.maxCount(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return StorageUtils.getDurabilityForDisplay(stack);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        //TODO: Technically doesn't support things where the color is part of the texture such as lava
        FluidStack fluidStack = StorageUtils.getStoredFluidFromNBT(stack);
        if (!fluidStack.isEmpty()) {
            return fluidStack.getFluid().getAttributes().getColor(fluidStack);
        }
        return ChemicalUtil.getRGBDurabilityForDisplay(stack);
    }

    @Nonnull
    @Override
    public TypedActionResult<ItemStack> use(@Nonnull World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (player.isSneaking() && !world.isClient) {
            Optional<IFluidHandlerItem> fluidCapability = MekanismUtils.toOptional(stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY));
            if (fluidCapability.isPresent()) {
                IFluidHandlerItem fluidHandler = fluidCapability.get();
                if (fluidHandler instanceof IExtendedFluidHandler) {
                    IExtendedFluidHandler fluidHandlerItem = (IExtendedFluidHandler) fluidHandler;
                    for (int tank = 0; tank < fluidHandlerItem.getTanks(); tank++) {
                        fluidHandlerItem.setFluidInTank(tank, FluidStack.EMPTY);
                    }
                }
            }
            clearChemicalTanks(stack, GasStack.EMPTY);
            clearChemicalTanks(stack, InfusionStack.EMPTY);
            clearChemicalTanks(stack, PigmentStack.EMPTY);
            clearChemicalTanks(stack, SlurryStack.EMPTY);
            ((ServerPlayerEntity) player).openHandledScreen(player.currentScreenHandler);
            return new TypedActionResult<>(ActionResult.SUCCESS, stack);
        }
        return new TypedActionResult<>(ActionResult.PASS, stack);
    }

    private static <CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>> void clearChemicalTanks(ItemStack stack, STACK empty) {
        Optional<IChemicalHandler<CHEMICAL, STACK>> cap = MekanismUtils.toOptional(stack.getCapability(ChemicalUtil.getCapabilityForChemical(empty)));
        if (cap.isPresent()) {
            IChemicalHandler<CHEMICAL, STACK> handler = cap.get();
            for (int tank = 0; tank < handler.getTanks(); tank++) {
                handler.setChemicalInTank(tank, empty);
            }
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(@Nonnull ItemStack stack, World world, @Nonnull List<Text> tooltip, @Nonnull TooltipContext flag) {
        StorageUtils.addStoredSubstance(stack, tooltip, false);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        return new ItemCapabilityWrapper(stack, GaugeDropperContentsHandler.create());
    }
}