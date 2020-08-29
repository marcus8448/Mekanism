package mekanism.common.block.basic;

import javax.annotation.Nonnull;
import mekanism.api.text.EnumColor;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.interfaces.IColoredBlock;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.blocktype.Machine;
import mekanism.common.tile.TileEntityFluidTank;
import mekanism.common.tile.base.WrenchResult;
import mekanism.common.util.FluidUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.SecurityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

public class BlockFluidTank extends BlockTileModel<TileEntityFluidTank, Machine<TileEntityFluidTank>> implements IColoredBlock {

    public BlockFluidTank(Machine<TileEntityFluidTank> type) {
        super(type, Block.Properties.of(Material.METAL).strength(3.5F, 16F).requiresTool());
    }

    @Override
    public int getLightValue(BlockState state, BlockView world, BlockPos pos) {
        int ambientLight = 0;
        TileEntityFluidTank tile = MekanismUtils.getTileEntity(TileEntityFluidTank.class, world, pos);
        if (tile != null) {
            if (MekanismConfig.client.enableAmbientLighting.get() && tile.lightUpdate() && tile.getActive()) {
                ambientLight = MekanismConfig.client.ambientLightingLevel.get();
            }
            FluidStack fluid = tile.fluidTank.getFluid();
            if (!fluid.isEmpty()) {
                FluidAttributes fluidAttributes = fluid.getFluid().getAttributes();
                //TODO: Decide if we want to always be using the luminosity of the stack
                ambientLight = Math.max(ambientLight, world instanceof BlockRenderView ? fluidAttributes.getLuminosity((BlockRenderView) world, pos)
                                                                                           : fluidAttributes.getLuminosity(fluid));
            }
        }
        return ambientLight;
    }

    @Nonnull
    @Override
    @Deprecated
    public ActionResult onUse(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand,
          @Nonnull BlockHitResult hit) {
        TileEntityFluidTank tile = MekanismUtils.getTileEntity(TileEntityFluidTank.class, world, pos, true);
        if (tile == null) {
            return ActionResult.PASS;
        }
        if (world.isClient) {
            return genericClientActivated(player, hand, hit);
        }
        if (tile.tryWrench(state, player, hand, hit) != WrenchResult.PASS) {
            return ActionResult.SUCCESS;
        }
        //Handle filling fluid tank
        if (!player.isSneaking()) {
            if (SecurityUtils.canAccess(player, tile)) {
                ItemStack stack = player.getStackInHand(hand);
                if (!stack.isEmpty() && FluidUtils.handleTankInteraction(player, hand, stack, tile.fluidTank)) {
                    player.inventory.markDirty();
                    return ActionResult.SUCCESS;
                }
            } else {
                SecurityUtils.displayNoAccess(player);
                return ActionResult.SUCCESS;
            }
        }
        return tile.openGui(player);
    }

    @Override
    public EnumColor getColor() {
        return Attribute.getBaseTier(this).getColor();
    }
}