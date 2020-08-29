package mekanism.common.registration.impl;

import java.util.function.Supplier;
import mekanism.common.registration.WrappedDeferredRegister;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraftforge.registries.ForgeRegistries;

public class PlacementDeferredRegister extends WrappedDeferredRegister<Decorator<?>> {

    public PlacementDeferredRegister(String modid) {
        super(modid, ForgeRegistries.DECORATORS);
    }

    public <CONFIG extends DecoratorConfig, PLACEMENT extends Decorator<CONFIG>> PlacementRegistryObject<CONFIG, PLACEMENT> register(String name, Supplier<PLACEMENT> sup) {
        return register(name, sup, PlacementRegistryObject::new);
    }
}