package mekanism.common.lib.chunkloading;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * MultiMap-style Map which uses a fastutils Long2ObjectMap
 */
public class ChunkMultimap extends Long2ObjectOpenHashMap<Set<BlockPos>> implements INBTSerializable<ListTag> {

    private static final String ENTRIES_KEY = "entries";
    private static final String KEY_KEY = "key";

    public ChunkMultimap() {
    }

    public boolean add(ChunkPos key, BlockPos value) {
        return computeIfAbsent(key.toLong(), k -> new ObjectOpenHashSet<>()).add(value);
    }

    public void remove(ChunkPos key, BlockPos value) {
        Set<BlockPos> chunkEntries = this.get(key.toLong());
        if (chunkEntries != null) {
            chunkEntries.remove(value);
            if (chunkEntries.isEmpty()) {
                this.remove(key.toLong());
            }
        }
    }

    @Override
    public ListTag serializeNBT() {
        ListTag listOut = new ListTag();
        this.long2ObjectEntrySet().fastForEach(entry -> {
            if (!entry.getValue().isEmpty()) {
                CompoundTag nbtEntry = new CompoundTag();
                listOut.add(nbtEntry);
                nbtEntry.putLong(KEY_KEY, entry.getLongKey());
                ListTag nbtEntryList = new ListTag();
                nbtEntry.put(ENTRIES_KEY, nbtEntryList);
                for (BlockPos blockPos : entry.getValue()) {
                    nbtEntryList.add(NbtHelper.fromBlockPos(blockPos));
                }
            }
        });
        return listOut;
    }

    @Override
    public void deserializeNBT(ListTag entryList) {
        for (int i = 0; i < entryList.size(); i++) {
            CompoundTag entry = entryList.getCompound(i);
            long key = entry.getLong(KEY_KEY);
            ListTag blockPosList = entry.getList(ENTRIES_KEY, NBT.TAG_COMPOUND);
            Set<BlockPos> blockPosSet = new ObjectOpenHashSet<>();
            this.put(key, blockPosSet);
            for (int j = 0; j < blockPosList.size(); j++) {
                blockPosSet.add(NbtHelper.toBlockPos(blockPosList.getCompound(j)));
            }
        }
    }
}