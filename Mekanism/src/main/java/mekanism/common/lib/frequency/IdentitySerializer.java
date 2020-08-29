package mekanism.common.lib.frequency;

import java.util.UUID;
import mekanism.api.NBTConstants;
import mekanism.common.lib.frequency.Frequency.FrequencyIdentity;
import mekanism.common.network.BasePacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;

public interface IdentitySerializer {

    IdentitySerializer NAME = new IdentitySerializer() {
        @Override
        public FrequencyIdentity read(PacketByteBuf buf) {
            return new FrequencyIdentity(BasePacketHandler.readString(buf), buf.readBoolean());
        }

        @Override
        public FrequencyIdentity load(CompoundTag data) {
            if (!data.getString(NBTConstants.NAME).isEmpty()) {
                return new FrequencyIdentity(data.getString(NBTConstants.NAME), data.getBoolean(NBTConstants.PUBLIC_FREQUENCY));
            }
            return null;
        }

        @Override
        public void write(PacketByteBuf buf, FrequencyIdentity data) {
            buf.writeString(data.getKey().toString());
            buf.writeBoolean(data.isPublic());
        }

        @Override
        public CompoundTag serialize(FrequencyIdentity data) {
            CompoundTag tag = new CompoundTag();
            tag.putString(NBTConstants.NAME, (String) data.getKey());
            tag.putBoolean(NBTConstants.PUBLIC_FREQUENCY, data.isPublic());
            return tag;
        }
    };

    IdentitySerializer UUID = new IdentitySerializer() {
        @Override
        public FrequencyIdentity read(PacketByteBuf buf) {
            return new FrequencyIdentity(buf.readUuid(), buf.readBoolean());
        }

        @Override
        public FrequencyIdentity load(CompoundTag data) {
            if (!data.getString(NBTConstants.OWNER_UUID).isEmpty()) {
                return new FrequencyIdentity(data.getString(NBTConstants.OWNER_UUID), data.getBoolean(NBTConstants.PUBLIC_FREQUENCY));
            }
            return null;
        }

        @Override
        public void write(PacketByteBuf buf, FrequencyIdentity data) {
            buf.writeUuid((UUID) data.getKey());
            buf.writeBoolean(data.isPublic());
        }

        @Override
        public CompoundTag serialize(FrequencyIdentity data) {
            CompoundTag tag = new CompoundTag();
            tag.putUuid(NBTConstants.OWNER_UUID, (UUID) data.getKey());
            tag.putBoolean(NBTConstants.PUBLIC_FREQUENCY, data.isPublic());
            return tag;
        }
    };

    FrequencyIdentity read(PacketByteBuf buf);

    FrequencyIdentity load(CompoundTag data);

    void write(PacketByteBuf buf, FrequencyIdentity data);

    CompoundTag serialize(FrequencyIdentity data);
}
