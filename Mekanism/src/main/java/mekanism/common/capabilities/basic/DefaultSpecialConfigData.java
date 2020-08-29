package mekanism.common.capabilities.basic;

import mekanism.api.IConfigCardAccess.ISpecialConfigData;
import mekanism.common.capabilities.basic.DefaultStorageHelper.NullStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class DefaultSpecialConfigData extends DefaultConfigCardAccess implements ISpecialConfigData {

    public static void register() {
        CapabilityManager.INSTANCE.register(ISpecialConfigData.class, new NullStorage<>(), DefaultSpecialConfigData::new);
    }

    @Override
    public CompoundTag getConfigurationData(CompoundTag nbtTags) {
        return null;
    }

    @Override
    public void setConfigurationData(CompoundTag nbtTags) {
    }

    @Override
    public String getDataType() {
        return null;
    }
}