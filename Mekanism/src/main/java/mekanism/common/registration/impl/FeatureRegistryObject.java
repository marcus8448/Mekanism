package mekanism.common.registration.impl;

import javax.annotation.Nonnull;
import mekanism.common.registration.WrappedRegistryObject;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraftforge.fml.RegistryObject;

public class FeatureRegistryObject<CONFIG extends FeatureConfig, FEATURE extends Feature<CONFIG>> extends WrappedRegistryObject<FEATURE> {

    public FeatureRegistryObject(RegistryObject<FEATURE> registryObject) {
        super(registryObject);
    }

    @Nonnull
    public FEATURE getFeature() {
        return get();
    }
}