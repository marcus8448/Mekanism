package mekanism.common.block.attribute;

import java.util.function.Function;
import java.util.function.Supplier;
import mekanism.api.text.TextComponentUtil;
import mekanism.common.inventory.container.ContainerProvider;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.screen.NamedScreenHandlerFactory;

public class AttributeGui implements Attribute {

    private final Supplier<ContainerTypeRegistryObject<? extends MekanismContainer>> containerRegistrar;
    private Function<TileEntityMekanism, NamedScreenHandlerFactory> containerSupplier = (tile) -> new ContainerProvider(TextComponentUtil.translate(tile.getBlockType().getTranslationKey()),
          (i, inv, player) -> new MekanismTileContainer<>(getContainerType(), i, inv, tile));

    public AttributeGui(Supplier<ContainerTypeRegistryObject<? extends MekanismContainer>> containerRegistrar) {
        this.containerRegistrar = containerRegistrar;
    }

    public void setCustomContainer(Function<TileEntityMekanism, NamedScreenHandlerFactory> containerSupplier) {
        this.containerSupplier = containerSupplier;
    }

    public ContainerTypeRegistryObject<? extends MekanismContainer> getContainerType() {
        return containerRegistrar.get();
    }

    public NamedScreenHandlerFactory getProvider(TileEntityMekanism tile) {
        return containerSupplier.apply(tile);
    }
}
