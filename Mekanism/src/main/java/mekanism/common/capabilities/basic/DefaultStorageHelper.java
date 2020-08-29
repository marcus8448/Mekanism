package mekanism.common.capabilities.basic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.math.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Created by ben on 03/05/16.
 */
public class DefaultStorageHelper {

    public static class DefaultStorage<T> implements IStorage<T> {

        @Override
        public Tag writeNBT(Capability<T> capability, T instance, Direction side) {
            if (instance instanceof INBTSerializable) {
                return ((INBTSerializable<?>) instance).serializeNBT();
            }
            return new CompoundTag();
        }

        @Override
        public void readNBT(Capability<T> capability, T instance, Direction side, Tag nbt) {
            if (instance instanceof INBTSerializable) {
                Class<? extends Tag> nbtClass = ((INBTSerializable<? extends Tag>) instance).serializeNBT().getClass();
                if (nbtClass.isInstance(nbt)) {
                    ((INBTSerializable) instance).deserializeNBT(nbtClass.cast(nbt));
                }
            }
        }
    }

    public static class NullStorage<T> implements IStorage<T> {

        @Override
        public Tag writeNBT(Capability<T> capability, T instance, Direction side) {
            return new CompoundTag();
        }

        @Override
        public void readNBT(Capability<T> capability, T instance, Direction side, Tag nbt) {
        }
    }
}