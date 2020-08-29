package mekanism.client.render.obj;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public class ContentsModelConfiguration implements IModelConfiguration {

    @Nullable
    @Override
    public UnbakedModel getOwnerModel() {
        return null;
    }

    @Nonnull
    @Override
    public String getModelName() {
        return "transmitter_contents";
    }

    @Override
    public boolean isTexturePresent(@Nonnull String name) {
        return false;
    }

    @Nonnull
    @Override
    public SpriteIdentifier resolveTexture(@Nonnull String name) {
        return ModelLoaderRegistry.blockMaterial(name);
    }

    @Override
    public boolean isShadedInGui() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public boolean useSmoothLighting() {
        return false;
    }

    @Nonnull
    @Override
    @Deprecated
    public ModelTransformation getCameraTransforms() {
        return ModelTransformation.NONE;
    }

    @Nonnull
    @Override
    public ModelBakeSettings getCombinedTransform() {
        return ModelRotation.X0_Y0;
    }
}