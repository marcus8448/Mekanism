package mekanism.client.render.obj;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;

public class WrapperModelConfiguration implements IModelConfiguration {

    protected final IModelConfiguration internal;

    protected WrapperModelConfiguration(IModelConfiguration internal) {
        this.internal = internal;
    }

    @Nullable
    @Override
    public UnbakedModel getOwnerModel() {
        return internal.getOwnerModel();
    }

    @Nonnull
    @Override
    public String getModelName() {
        return internal.getModelName();
    }

    @Override
    public boolean isTexturePresent(@Nonnull String name) {
        return internal.isTexturePresent(name);
    }

    @Nonnull
    @Override
    public SpriteIdentifier resolveTexture(@Nonnull String name) {
        return internal.resolveTexture(name);
    }

    @Override
    public boolean isShadedInGui() {
        return internal.isShadedInGui();
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public boolean useSmoothLighting() {
        return internal.useSmoothLighting();
    }

    @Nonnull
    @Override
    @Deprecated
    public ModelTransformation getCameraTransforms() {
        return internal.getCameraTransforms();
    }

    @Nonnull
    @Override
    public ModelBakeSettings getCombinedTransform() {
        return internal.getCombinedTransform();
    }

    @Override
    public boolean getPartVisibility(@Nonnull IModelGeometryPart part, boolean fallback) {
        return internal.getPartVisibility(part, fallback);
    }

    @Override
    public boolean getPartVisibility(@Nonnull IModelGeometryPart part) {
        return internal.getPartVisibility(part);
    }
}