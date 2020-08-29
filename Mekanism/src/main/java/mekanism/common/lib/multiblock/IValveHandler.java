package mekanism.common.lib.multiblock;

import java.util.Collection;
import mekanism.api.NBTConstants;
import mekanism.common.util.NBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraftforge.common.util.Constants.NBT;

public interface IValveHandler {

    default void writeValves(CompoundTag updateTag) {
        ListTag valves = new ListTag();
        for (ValveData valveData : getValveData()) {
            if (valveData.activeTicks > 0) {
                CompoundTag valveNBT = new CompoundTag();
                valveNBT.put(NBTConstants.POSITION, NbtHelper.fromBlockPos(valveData.location));
                valveNBT.putInt(NBTConstants.SIDE, valveData.side.ordinal());
                valves.add(valveNBT);
            }
        }
        updateTag.put(NBTConstants.VALVE, valves);
    }

    default void readValves(CompoundTag updateTag) {
        getValveData().clear();
        if (updateTag.contains(NBTConstants.VALVE, NBT.TAG_LIST)) {
            ListTag valves = updateTag.getList(NBTConstants.VALVE, NBT.TAG_COMPOUND);
            for (int i = 0; i < valves.size(); i++) {
                CompoundTag valveNBT = valves.getCompound(i);
                ValveData data = new ValveData();
                NBTUtils.setBlockPosIfPresent(valveNBT, NBTConstants.POSITION, (pos) -> data.location = pos);
                data.side = Direction.byId(valveNBT.getInt(NBTConstants.SIDE));
                getValveData().add(data);
            }
        }
    }

    default void triggerValveTransfer(IMultiblock<?> multiblock) {
        if (multiblock.getMultiblock().isFormed()) {
            for (ValveData data : getValveData()) {
                if (multiblock.getTilePos().equals(data.location)) {
                    data.onTransfer();
                    break;
                }
            }
        }
    }

    Collection<ValveData> getValveData();

    class ValveData {

        public Direction side;
        public BlockPos location;

        public boolean prevActive;
        public int activeTicks;

        public void onTransfer() {
            activeTicks = 30;
        }

        @Override
        public int hashCode() {
            int code = 1;
            code = 31 * code + side.ordinal();
            code = 31 * code + location.hashCode();
            return code;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ValveData && ((ValveData) obj).side == side && ((ValveData) obj).location.equals(location);
        }
    }
}
