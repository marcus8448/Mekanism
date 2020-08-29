package mekanism.common.block.transmitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import mekanism.api.IMekWrench;
import mekanism.common.block.BlockMekanism;
import mekanism.common.block.states.IStateFluidLoggable;
import mekanism.common.block.states.TransmitterType.Size;
import mekanism.common.content.network.transmitter.Transmitter;
import mekanism.common.lib.transmitter.ConnectionType;
import mekanism.common.registries.MekanismItems;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MultipartUtils;
import mekanism.common.util.MultipartUtils.AdvancedRayTraceResult;
import mekanism.common.util.VoxelShapeUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.apache.commons.lang3.tuple.Pair;

public abstract class BlockTransmitter extends BlockMekanism implements IStateFluidLoggable {

    private static final Map<ConnectionInfo, VoxelShape> cachedShapes = new HashMap<>();

    protected BlockTransmitter() {
        super(Block.Properties.of(Material.PISTON).strength(1F, 10F));
    }

    @Nonnull
    @Override
    public ActionResult onUse(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, PlayerEntity player, @Nonnull Hand hand,
          @Nonnull BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isEmpty()) {
            return ActionResult.PASS;
        }
        IMekWrench wrenchHandler = MekanismUtils.getWrench(stack);
        if (wrenchHandler != null) {
            if (wrenchHandler.canUseWrench(stack, player, hit.getBlockPos()) && player.isSneaking()) {
                if (!world.isClient) {
                    MekanismUtils.dismantleBlock(state, world, pos);
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void onPlaced(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull LivingEntity placer, @Nonnull ItemStack stack) {
        TileEntityTransmitter tile = MekanismUtils.getTileEntity(TileEntityTransmitter.class, world, pos);
        if (tile != null) {
            tile.onAdded();
        }
    }

    @Override
    @Deprecated
    public void neighborUpdate(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block neighborBlock, @Nonnull BlockPos neighborPos,
          boolean isMoving) {
        TileEntityTransmitter tile = MekanismUtils.getTileEntity(TileEntityTransmitter.class, world, pos);
        if (tile != null) {
            Direction side = Direction.getFacing(neighborPos.getX() - pos.getX(), neighborPos.getY() - pos.getY(), neighborPos.getZ() - pos.getZ());
            tile.onNeighborBlockChange(side);
        }
    }

    @Override
    public void onNeighborChange(BlockState state, WorldView world, BlockPos pos, BlockPos neighbor) {
        TileEntityTransmitter tile = MekanismUtils.getTileEntity(TileEntityTransmitter.class, world, pos);
        if (tile != null) {
            Direction side = Direction.getFacing(neighbor.getX() - pos.getX(), neighbor.getY() - pos.getY(), neighbor.getZ() - pos.getZ());
            tile.onNeighborTileChange(side);
        }
    }

    @Nonnull
    @Override
    @Deprecated
    public VoxelShape getOutlineShape(@Nonnull BlockState state, @Nonnull BlockView world, @Nonnull BlockPos pos, ShapeContext context) {
        if (!context.isHolding(MekanismItems.CONFIGURATOR.getItem())) {
            return getRealShape(world, pos);
        }
        //Get the partial selection box if we are holding a configurator
        if (context.getEntity() == null) {
            //If we don't have an entity get the full VoxelShape
            return getRealShape(world, pos);
        }
        TileEntityTransmitter tile = MekanismUtils.getTileEntity(TileEntityTransmitter.class, world, pos);
        if (tile == null) {
            //If we failed to get the tile, just give the center shape
            return getCenter();
        }
        //TODO: Try to cache some of this? At the very least the collision boxes
        Pair<Vec3d, Vec3d> vecs = MultipartUtils.getRayTraceVectors(context.getEntity());
        AdvancedRayTraceResult result = MultipartUtils.collisionRayTrace(pos, vecs.getLeft(), vecs.getRight(), tile.getCollisionBoxes());
        if (result != null && result.valid()) {
            return result.bounds;
        }
        //If we failed to figure it out somehow, just fall back to the center. This should never happen
        return getCenter();
    }

    @Nonnull
    @Override
    @Deprecated
    public VoxelShape getCullingShape(@Nonnull BlockState state, @Nonnull BlockView world, @Nonnull BlockPos pos) {
        //Override this so that we ALWAYS have the full collision box, even if a configurator is being held
        return getRealShape(world, pos);
    }

    @Nonnull
    @Override
    @Deprecated
    public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockView world, @Nonnull BlockPos pos, @Nonnull ShapeContext context) {
        //Override this so that we ALWAYS have the full collision box, even if a configurator is being held
        return getRealShape(world, pos);
    }

    protected abstract VoxelShape getCenter();

    protected abstract VoxelShape getSide(ConnectionType type, Direction side);

    private VoxelShape getRealShape(BlockView world, BlockPos pos) {
        TileEntityTransmitter tile = MekanismUtils.getTileEntity(TileEntityTransmitter.class, world, pos);
        if (tile == null) {
            //If we failed to get the tile, just give the center shape
            return getCenter();
        }
        Transmitter<?, ?, ?> transmitter = tile.getTransmitter();
        ConnectionType[] connectionTypes = new ConnectionType[transmitter.connectionTypes.length];
        for (int i = 0; i < EnumUtils.DIRECTIONS.length; i++) {
            //Get the actual connection types
            connectionTypes[i] = transmitter.getConnectionType(EnumUtils.DIRECTIONS[i]);
        }
        ConnectionInfo info = new ConnectionInfo(tile.getTransmitterType().getSize(), connectionTypes);
        if (cachedShapes.containsKey(info)) {
            return cachedShapes.get(info);
        }
        //If we don't have a cached version of our shape, then we need to calculate it
        List<VoxelShape> shapes = new ArrayList<>();
        for (Direction side : EnumUtils.DIRECTIONS) {
            ConnectionType connectionType = connectionTypes[side.ordinal()];
            if (connectionType != ConnectionType.NONE) {
                shapes.add(getSide(connectionType, side));
            }
        }
        VoxelShape center = getCenter();
        if (shapes.isEmpty()) {
            cachedShapes.put(info, center);
            return center;
        }
        shapes.add(center);
        VoxelShape shape = VoxelShapeUtils.combine(shapes);
        cachedShapes.put(info, shape);
        return shape;
    }

    private static class ConnectionInfo {

        private final Size size;
        private final ConnectionType[] connectionTypes;

        private ConnectionInfo(Size size, ConnectionType[] connectionTypes) {
            this.size = size;
            this.connectionTypes = connectionTypes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof ConnectionInfo) {
                ConnectionInfo other = (ConnectionInfo) o;
                return size == other.size && Arrays.equals(connectionTypes, other.connectionTypes);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(size);
            result = 31 * result + Arrays.hashCode(connectionTypes);
            return result;
        }
    }
}