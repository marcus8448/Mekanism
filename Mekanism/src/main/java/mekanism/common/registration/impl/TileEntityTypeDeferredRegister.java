package mekanism.common.registration.impl;

import java.util.function.Supplier;
import mekanism.common.registration.WrappedDeferredRegister;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityTypeDeferredRegister extends WrappedDeferredRegister<BlockEntityType<?>> {

    public TileEntityTypeDeferredRegister(String modid) {
        super(modid, ForgeRegistries.TILE_ENTITIES);
    }

    @SuppressWarnings("ConstantConditions")
    public <TILE extends BlockEntity> TileEntityTypeRegistryObject<TILE> register(BlockRegistryObject<?, ?> block, Supplier<? extends TILE> factory) {
        //Note: There is no data fixer type as forge does not currently have a way exposing data fixers to mods yet
        return register(block.getInternalRegistryName(), () -> BlockEntityType.Builder.<TILE>create(factory, block.getBlock()).build(null),
              TileEntityTypeRegistryObject::new);
    }
}