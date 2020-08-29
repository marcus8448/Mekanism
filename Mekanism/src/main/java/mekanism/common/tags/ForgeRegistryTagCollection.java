package mekanism.common.tags;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class ForgeRegistryTagCollection<T extends IForgeRegistryEntry<T>> extends TagContainer<T> {

    private final IForgeRegistry<T> registry;
    private final Consumer<TagContainer<T>> collectionSetter;

    public ForgeRegistryTagCollection(IForgeRegistry<T> registry, String location, String type, Consumer<TagContainer<T>> collectionSetter) {
        super(key -> Optional.ofNullable(registry.getValue(key)), location, type);
        this.registry = registry;
        this.collectionSetter = collectionSetter;
    }

    public void write(PacketByteBuf buffer) {
        Map<Identifier, Tag<T>> tagMap = this.getEntries();
        buffer.writeVarInt(tagMap.size());
        for (Entry<Identifier, Tag<T>> entry : tagMap.entrySet()) {
            buffer.writeIdentifier(entry.getKey());
            Tag<T> tag = entry.getValue();
            List<T> tags = tag.values();
            buffer.writeVarInt(tags.size());
            for (T element : tags) {
                Identifier key = this.registry.getKey(element);
                if (key != null) {
                    buffer.writeIdentifier(key);
                }
            }
        }
    }

    public void read(PacketByteBuf buffer) {
        Map<Identifier, Tag<T>> tagMap = new Object2ObjectOpenHashMap<>();
        int tagCount = buffer.readVarInt();
        for (int i = 0; i < tagCount; ++i) {
            Identifier resourceLocation = buffer.readIdentifier();
            Builder<T> builder = ImmutableSet.builder();
            int elementCount = buffer.readVarInt();
            for (int j = 0; j < elementCount; ++j) {
                T value = registry.getValue(buffer.readIdentifier());
                if (value != null) {
                    //Should never be null anyways
                    builder.add(value);
                }
            }
            tagMap.put(resourceLocation, Tag.of(builder.build()));
        }
        setEntries(tagMap);
    }

    public void setCollection() {
        collectionSetter.accept(this);
    }
}