package mekanism.client;

import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.providers.IItemProvider;
import mekanism.client.gui.machine.GuiAdvancedElectricMachine;
import mekanism.client.gui.machine.GuiElectricMachine;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.registration.impl.ContainerTypeRegistryObject;
import mekanism.common.registration.impl.EntityTypeRegistryObject;
import mekanism.common.registration.impl.FluidRegistryObject;
import mekanism.common.registration.impl.ParticleTypeRegistryObject;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.prefab.TileEntityAdvancedElectricMachine;
import mekanism.common.tile.prefab.TileEntityElectricMachine;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.HandledScreens.Provider;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientRegistrationUtil {

    public static <T extends Entity> void registerEntityRenderingHandler(EntityTypeRegistryObject<T> entityTypeRO, IRenderFactory<? super T> renderFactory) {
        RenderingRegistry.registerEntityRenderingHandler(entityTypeRO.getEntityType(), renderFactory);
    }

    public static synchronized <T extends BlockEntity> void bindTileEntityRenderer(TileEntityTypeRegistryObject<T> tileTypeRO,
          Function<BlockEntityRenderDispatcher, BlockEntityRenderer<? super T>> renderFactory) {
        ClientRegistry.bindTileEntityRenderer(tileTypeRO.getTileEntityType(), renderFactory);
    }

    @SafeVarargs
    public static synchronized <T extends BlockEntity> void bindTileEntityRenderer(Function<BlockEntityRenderDispatcher, BlockEntityRenderer<T>> rendererFactory,
          TileEntityTypeRegistryObject<? extends T>... tileEntityTypeROs) {
        BlockEntityRenderer<T> renderer = rendererFactory.apply(BlockEntityRenderDispatcher.INSTANCE);
        for (TileEntityTypeRegistryObject<? extends T> tileTypeRO : tileEntityTypeROs) {
            ClientRegistry.bindTileEntityRenderer(tileTypeRO.getTileEntityType(), constant -> renderer);
        }
    }

    public static <T extends ParticleEffect> void registerParticleFactory(ParticleTypeRegistryObject<T> particleTypeRO, ParticleManager.SpriteAwareFactory<T> factory) {
        MinecraftClient.getInstance().particleManager.registerFactory(particleTypeRO.getParticleType(), factory);
    }

    public static <C extends ScreenHandler, U extends Screen & ScreenHandlerProvider<C>> void registerScreen(ContainerTypeRegistryObject<C> type, Provider<C, U> factory) {
        HandledScreens.register(type.getContainerType(), factory);
    }

    //Helper method to register GuiElectricMachine due to generics not being able to be resolved through registerScreen
    public static <TILE extends TileEntityElectricMachine, C extends MekanismTileContainer<TILE>> void registerElectricScreen(ContainerTypeRegistryObject<C> type) {
        registerScreen(type, new Provider<C, GuiElectricMachine<TILE, C>>() {
            @Nonnull
            @Override
            public GuiElectricMachine<TILE, C> create(@Nonnull C container, @Nonnull PlayerInventory inv, @Nonnull Text title) {
                return new GuiElectricMachine<>(container, inv, title);
            }
        });
    }

    //Helper method to register GuiAdvancedElectricMachine due to generics not being able to be resolved through registerScreen
    public static <TILE extends TileEntityAdvancedElectricMachine, C extends MekanismTileContainer<TILE>> void registerAdvancedElectricScreen(ContainerTypeRegistryObject<C> type) {
        registerScreen(type, new Provider<C, GuiAdvancedElectricMachine<TILE, C>>() {
            @Nonnull
            @Override
            public GuiAdvancedElectricMachine<TILE, C> create(@Nonnull C container, @Nonnull PlayerInventory inv, @Nonnull Text title) {
                return new GuiAdvancedElectricMachine<>(container, inv, title);
            }
        });
    }

    public static void setPropertyOverride(IItemProvider itemProvider, Identifier override, ModelPredicateProvider propertyGetter) {
        ModelPredicateProviderRegistry.register(itemProvider.getItem(), override, propertyGetter);
    }

    public static void registerItemColorHandler(ItemColors colors, ItemColorProvider itemColor, IItemProvider... items) {
        for (IItemProvider itemProvider : items) {
            colors.register(itemColor, itemProvider.getItem());
        }
    }

    public static void registerBlockColorHandler(BlockColors blockColors, BlockColorProvider blockColor, IBlockProvider... blocks) {
        for (IBlockProvider blockProvider : blocks) {
            blockColors.registerColorProvider(blockColor, blockProvider.getBlock());
        }
    }

    public static void registerBlockColorHandler(BlockColors blockColors, ItemColors itemColors, BlockColorProvider blockColor, ItemColorProvider itemColor, IBlockProvider... blocks) {
        for (IBlockProvider blockProvider : blocks) {
            blockColors.registerColorProvider(blockColor, blockProvider.getBlock());
            itemColors.register(itemColor, blockProvider.getItem());
        }
    }

    public static void setRenderLayer(RenderLayer type, IBlockProvider... blockProviders) {
        for (IBlockProvider blockProvider : blockProviders) {
            RenderLayers.setRenderLayer(blockProvider.getBlock(), type);
        }
    }

    public static synchronized void setRenderLayer(Predicate<RenderLayer> predicate, IBlockProvider... blockProviders) {
        for (IBlockProvider blockProvider : blockProviders) {
            RenderLayers.setRenderLayer(blockProvider.getBlock(), predicate);
        }
    }

    public static void setRenderLayer(RenderLayer type, FluidRegistryObject<?, ?, ?, ?>... fluidROs) {
        for (FluidRegistryObject<?, ?, ?, ?> fluidRO : fluidROs) {
            RenderLayers.setRenderLayer(fluidRO.getStillFluid(), type);
            RenderLayers.setRenderLayer(fluidRO.getFlowingFluid(), type);
        }
    }

    public static synchronized void setRenderLayer(Predicate<RenderLayer> predicate, FluidRegistryObject<?, ?, ?, ?>... fluidROs) {
        for (FluidRegistryObject<?, ?, ?, ?> fluidRO : fluidROs) {
            RenderLayers.setRenderLayer(fluidRO.getStillFluid(), predicate);
            RenderLayers.setRenderLayer(fluidRO.getFlowingFluid(), predicate);
        }
    }
}