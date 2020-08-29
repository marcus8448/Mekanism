package mekanism.client.render.obj;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.client.model.data.TransmitterModelData;
import mekanism.client.render.obj.TransmitterModelConfiguration.IconStatus;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import mekanism.common.util.EnumUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.BlockRenderView;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.minecraftforge.client.model.obj.OBJModel;

public class TransmitterBakedModel implements BakedModel {

    private final OBJModel internal;
    @Nullable
    private final OBJModel glass;
    private final IModelConfiguration owner;
    private final ModelLoader bakery;
    private final Function<SpriteIdentifier, Sprite> spriteGetter;
    private final ModelBakeSettings modelTransform;
    private final ModelOverrideList overrides;
    private final Identifier modelLocation;
    private final BakedModel bakedVariant;

    private final Map<QuickHash, List<BakedQuad>> modelCache;

    public TransmitterBakedModel(OBJModel internal, @Nullable OBJModel glass, IModelConfiguration owner, ModelLoader bakery,
          Function<SpriteIdentifier, Sprite> spriteGetter, ModelBakeSettings modelTransform, ModelOverrideList overrides, Identifier modelLocation) {
        //4^6 number of states, if we have a glass texture (support coloring), multiply by 2
        this.modelCache = new Object2ObjectOpenHashMap<>(glass == null ? 4_096 : 8_192);
        this.internal = internal;
        this.glass = glass;
        this.owner = owner;
        this.bakery = bakery;
        this.spriteGetter = spriteGetter;
        this.modelTransform = modelTransform;
        this.overrides = overrides;
        this.modelLocation = modelLocation;
        //We define our baked variant to be how the item is. As we should always have model data when we have a state
        List<String> visible = Arrays.stream(EnumUtils.DIRECTIONS).map(side -> side.getName() + (side.getAxis() == Axis.Y ? "NORMAL" : "NONE")).collect(Collectors.toList());
        bakedVariant = internal.bake(new VisibleModelConfiguration(owner, visible), bakery, spriteGetter, modelTransform, overrides, modelLocation);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand) {
        return getQuads(state, side, rand, EmptyModelData.INSTANCE);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        if (side != null) {
            return ImmutableList.of();
        }
        if (extraData.hasProperty(TileEntityTransmitter.TRANSMITTER_PROPERTY)) {
            TransmitterModelData data = extraData.getData(TileEntityTransmitter.TRANSMITTER_PROPERTY);
            RenderLayer layer = MinecraftForgeClient.getRenderLayer();
            boolean hasColor = data.getHasColor() && layer == RenderLayer.getTranslucent();
            QuickHash hash = new QuickHash(data.getConnectionsMap(), hasColor);
            if (!modelCache.containsKey(hash)) {
                List<String> visible = new ArrayList<>();
                for (Direction dir : EnumUtils.DIRECTIONS) {
                    visible.add(dir.asString() + data.getConnectionType(dir).asString().toUpperCase(Locale.ROOT));
                }
                List<BakedQuad> result = bake(new TransmitterModelConfiguration(owner, visible, extraData), hasColor).getQuads(state, null, rand, extraData);
                modelCache.put(hash, result);
                return result;
            }
            return modelCache.get(hash);
        }
        //Fallback to our "default" model arrangement. The item variant uses this
        return bakedVariant.getQuads(state, null, rand, extraData);
    }

    /**
     * Rotates the pieces that need rotating.
     */
    private BakedModel bake(TransmitterModelConfiguration configuration, boolean hasColor) {
        Sprite particle = spriteGetter.apply(configuration.resolveTexture("particle"));
        IModelBuilder<?> builder = IModelBuilder.of(configuration, overrides, particle);
        addPartQuads(configuration, builder, internal);
        if (glass != null && hasColor && MinecraftForgeClient.getRenderLayer() == RenderLayer.getTranslucent()) {
            addPartQuads(configuration, builder, glass);
        }
        return builder.build();
    }

    private void addPartQuads(TransmitterModelConfiguration configuration, IModelBuilder<?> builder, OBJModel glass) {
        for (IModelGeometryPart part : glass.getParts()) {
            if (configuration.getPartVisibility(part)) {
                String name = part.name();
                ModelBakeSettings transform = modelTransform;
                if (name.endsWith("NONE")) {
                    Direction dir = directionForPiece(name);
                    //We should not have been able to get here if dir was null but check just in case
                    IconStatus status = configuration.getIconStatus(dir);
                    if (dir != null && status.getAngle() > 0) {
                        //If the part should be rotated, then we need to use a custom IModelTransform
                        transform = new TransmitterModelTransform(transform, dir, status.getAngle());
                    }
                }
                part.addQuads(configuration, builder, bakery, spriteGetter, transform, modelLocation);
            }
        }
    }

    @Nullable
    private static Direction directionForPiece(@Nonnull String piece) {
        return Arrays.stream(EnumUtils.DIRECTIONS).filter(dir -> piece.startsWith(dir.getName())).findFirst().orElse(null);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return bakedVariant.useAmbientOcclusion();
    }

    @Override
    public boolean isAmbientOcclusion(BlockState state) {
        return bakedVariant.isAmbientOcclusion(state);
    }

    @Override
    public boolean hasDepth() {
        return bakedVariant.hasDepth();
    }

    @Override
    public boolean isSideLit() {
        return bakedVariant.isSideLit();
    }

    @Override
    public boolean isBuiltin() {
        return bakedVariant.isBuiltin();
    }

    @Nonnull
    @Override
    @Deprecated
    public Sprite getSprite() {
        return bakedVariant.getSprite();
    }

    @Override
    public Sprite getParticleTexture(@Nonnull IModelData data) {
        return bakedVariant.getParticleTexture(data);
    }

    @Override
    public boolean doesHandlePerspectives() {
        return bakedVariant.doesHandlePerspectives();
    }

    @Override
    public BakedModel handlePerspective(Mode cameraTransformType, MatrixStack mat) {
        return bakedVariant.handlePerspective(cameraTransformType, mat);
    }

    @Nonnull
    @Override
    public ModelOverrideList getOverrides() {
        return bakedVariant.getOverrides();
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull BlockRenderView world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
        return bakedVariant.getModelData(world, pos, state, tileData);
    }

    @Nonnull
    @Override
    @Deprecated
    public ModelTransformation getTransformation() {
        return bakedVariant.getTransformation();
    }

    public static class QuickHash {

        private final Object[] objs;

        public QuickHash(Object... objs) {
            this.objs = objs;
        }

        @Override
        public int hashCode() {
            //TODO: Cache the hashcode?
            return Arrays.hashCode(objs);
        }

        public Object[] get() {
            return objs;
        }

        @Override
        public boolean equals(Object obj) {
            return obj == this || obj instanceof QuickHash && Arrays.deepEquals(objs, ((QuickHash) obj).objs);
        }
    }
}