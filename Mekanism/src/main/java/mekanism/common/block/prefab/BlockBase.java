package mekanism.common.block.prefab;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.text.ILangEntry;
import mekanism.common.block.BlockMekanism;
import mekanism.common.block.attribute.AttributeCustomShape;
import mekanism.common.block.attribute.AttributeStateFacing;
import mekanism.common.block.attribute.Attributes.AttributeCustomResistance;
import mekanism.common.block.attribute.Attributes.AttributeNoMobSpawn;
import mekanism.common.block.interfaces.IHasDescription;
import mekanism.common.block.interfaces.ITypeBlock;
import mekanism.common.block.states.IStateFluidLoggable;
import mekanism.common.content.blocktype.BlockType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction.Location;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;

public class BlockBase<TYPE extends BlockType> extends BlockMekanism implements IHasDescription, ITypeBlock {

    protected final TYPE type;

    public BlockBase(TYPE type) {
        this(type, Block.Properties.of(Material.METAL).strength(3.5F, 16F).requiresTool());
    }

    public BlockBase(TYPE type, Block.Properties properties) {
        super(hack(type, properties));
        this.type = type;
    }

    // ugly hack but required to have a reference to our block type before setting state info; assumes single-threaded startup
    private static BlockType cacheType;

    private static <TYPE extends BlockType> Block.Properties hack(TYPE type, Block.Properties props) {
        cacheType = type;
        type.getAll().forEach(a -> a.adjustProperties(props));
        return props;
    }

    @Override
    public BlockType getType() {
        return type == null ? cacheType : type;
    }

    @Nonnull
    @Override
    public ILangEntry getDescription() {
        return type.getDescription();
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockView world, BlockPos pos, Explosion explosion) {
        return type.has(AttributeCustomResistance.class) ? type.get(AttributeCustomResistance.class).getResistance()
                                                         : super.getExplosionResistance(state, world, pos, explosion);
    }

    @Override
    public boolean canCreatureSpawn(@Nonnull BlockState state, @Nonnull BlockView world, @Nonnull BlockPos pos, Location placement, @Nullable EntityType<?> entityType) {
        return !type.has(AttributeNoMobSpawn.class) && super.canCreatureSpawn(state, world, pos, placement, entityType);
    }

    @Nonnull
    @Override
    @Deprecated
    public VoxelShape getOutlineShape(@Nonnull BlockState state, @Nonnull BlockView world, @Nonnull BlockPos pos, @Nonnull ShapeContext context) {
        if (type.has(AttributeCustomShape.class)) {
            AttributeStateFacing attr = type.get(AttributeStateFacing.class);
            int index = attr == null ? 0 : (attr.getDirection(state).ordinal() - (attr.getFacingProperty() == Properties.FACING ? 0 : 2));
            return type.get(AttributeCustomShape.class).getBounds()[index];
        }
        return super.getOutlineShape(state, world, pos, context);
    }

    public static class BlockBaseModel<BLOCK extends BlockType> extends BlockBase<BLOCK> implements IStateFluidLoggable {

        public BlockBaseModel(BLOCK blockType) {
            super(blockType);
        }

        public BlockBaseModel(BLOCK blockType, Block.Properties properties) {
            super(blockType, properties);
        }
    }
}
