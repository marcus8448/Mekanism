package mekanism.common.block.prefab;

import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nonnull;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeGui;
import mekanism.common.block.attribute.AttributeParticleFX;
import mekanism.common.block.attribute.AttributeParticleFX.Particle;
import mekanism.common.block.attribute.AttributeStateActive;
import mekanism.common.block.attribute.Attributes.AttributeRedstoneEmitter;
import mekanism.common.block.interfaces.IHasTileEntity;
import mekanism.common.block.states.IStateFluidLoggable;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.base.WrenchResult;
import mekanism.common.tile.interfaces.IActiveState;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.SecurityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BlockTile<TILE extends TileEntityMekanism, TYPE extends BlockTypeTile<TILE>> extends BlockBase<TYPE> implements IHasTileEntity<TILE> {

    public BlockTile(TYPE type) {
        this(type, Block.Properties.of(Material.METAL).strength(3.5F, 16F).requiresTool());
    }

    public BlockTile(TYPE type, Block.Properties properties) {
        super(type, properties);
    }

    @Override
    public BlockEntityType<TILE> getTileType() {
        return type.getTileType();
    }

    @Nonnull
    @Override
    @Deprecated
    public ActionResult onUse(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand,
          @Nonnull BlockHitResult hit) {
        TileEntityMekanism tile = MekanismUtils.getTileEntity(TileEntityMekanism.class, world, pos);
        if (tile == null) {
            return ActionResult.PASS;
        }
        if (world.isClient) {
            return genericClientActivated(player, hand, hit);
        }
        if (tile.tryWrench(state, player, hand, hit) != WrenchResult.PASS) {
            return ActionResult.SUCCESS;
        }
        return type.has(AttributeGui.class) ? tile.openGui(player) : ActionResult.PASS;
    }

    @Override
    public int getLightValue(BlockState state, BlockView world, BlockPos pos) {
        if (MekanismConfig.client.enableAmbientLighting.get() && type.has(AttributeStateActive.class)) {
            BlockEntity tile = MekanismUtils.getTileEntity(world, pos);
            if (tile instanceof IActiveState && ((IActiveState) tile).lightUpdate() && ((IActiveState) tile).getActive()) {
                return ((IActiveState) tile).getActiveLightValue();
            }
        }
        return super.getLightValue(state, world, pos);
    }

    @Override
    @Deprecated
    public float calcBlockBreakingDelta(@Nonnull BlockState state, @Nonnull PlayerEntity player, @Nonnull BlockView world, @Nonnull BlockPos pos) {
        return SecurityUtils.canAccess(player, MekanismUtils.getTileEntity(world, pos)) ? super.calcBlockBreakingDelta(state, player, world, pos) : 0.0F;
    }

    @Override
    public void randomDisplayTick(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random random) {
        TileEntityMekanism tile = MekanismUtils.getTileEntity(TileEntityMekanism.class, world, pos);
        if (tile != null && MekanismUtils.isActive(world, pos) && Attribute.has(state.getBlock(), AttributeParticleFX.class)) {
            for (Function<Random, Particle> particleFunction : type.get(AttributeParticleFX.class).getParticleFunctions()) {
                Particle particle = particleFunction.apply(random);
                Vec3d particlePos = particle.getPos();
                if (tile.getDirection() == Direction.WEST) {
                    particlePos = particlePos.rotateY(90);
                } else if (tile.getDirection() == Direction.EAST) {
                    particlePos = particlePos.rotateY(270);
                } else if (tile.getDirection() == Direction.NORTH) {
                    particlePos = particlePos.rotateY(180);
                }
                particlePos = particlePos.add(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                world.addParticle(particle.getType(), particlePos.x, particlePos.y, particlePos.z, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    @Deprecated
    public void neighborUpdate(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block neighborBlock, @Nonnull BlockPos neighborPos,
          boolean isMoving) {
        if (!world.isClient) {
            TileEntityMekanism tile = MekanismUtils.getTileEntity(TileEntityMekanism.class, world, pos);
            if (tile != null) {
                tile.onNeighborChange(neighborBlock, neighborPos);
            }
        }
    }

    @Override
    public boolean emitsRedstonePower(@Nonnull BlockState state) {
        return type.has(AttributeRedstoneEmitter.class);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockView world, BlockPos pos, Direction side) {
        return type.has(AttributeRedstoneEmitter.class) || super.canConnectRedstone(state, world, pos, side);
    }

    @Override
    public int getWeakRedstonePower(@Nonnull BlockState state, @Nonnull BlockView world, @Nonnull BlockPos pos, @Nonnull Direction side) {
        if (type.has(AttributeRedstoneEmitter.class)) {
            TileEntityMekanism tile = MekanismUtils.getTileEntity(TileEntityMekanism.class, world, pos);
            return type.get(AttributeRedstoneEmitter.class).getRedstoneLevel(tile);
        }
        return 0;
    }

    public static class BlockTileModel<TILE extends TileEntityMekanism, BLOCK extends BlockTypeTile<TILE>> extends BlockTile<TILE, BLOCK> implements IStateFluidLoggable {

        public BlockTileModel(BLOCK type) {
            super(type);
        }

        public BlockTileModel(BLOCK type, Block.Properties properties) {
            super(type, properties);
        }
    }
}
