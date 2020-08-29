package mekanism.common.content.gear.mekasuit;

import java.util.List;
import java.util.Objects;
import mekanism.api.math.FloatingLong;
import mekanism.api.text.IHasTextComponent;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.ModuleConfigItem;
import mekanism.common.content.gear.ModuleConfigItem.EnumData;
import mekanism.common.network.PacketLightningRender;
import mekanism.common.network.PacketLightningRender.LightningPreset;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class ModuleMagneticAttractionUnit extends ModuleMekaSuit {

    private ModuleConfigItem<Range> range;

    @Override
    public void init() {
        super.init();
        addConfigItem(range = new ModuleConfigItem<>(this, "range", MekanismLang.MODULE_RANGE, new EnumData<>(Range.class, getInstalledCount() + 1), Range.LOW));
    }

    @Override
    public void tickServer(PlayerEntity player) {
        super.tickServer(player);
        if (range.get() != Range.OFF) {
            float size = 4 + range.get().getRange();
            List<ItemEntity> items = player.world.getNonSpectatingEntities(ItemEntity.class, player.getBoundingBox().expand(size, size, size));
            FloatingLong usage = MekanismConfig.gear.mekaSuitEnergyUsageItemAttraction.get().multiply(range.get().getRange());
            for (ItemEntity item : items) {
                if (!getContainerEnergy().greaterOrEqual(usage)) {
                    break;
                }
                if (item.distanceTo(player) > 0.001) {
                    useEnergy(player, usage);
                    Vec3d diff = player.getPos().subtract(item.getPos());
                    Vec3d motionNeeded = new Vec3d(Math.min(diff.x, 1), Math.min(diff.y, 1), Math.min(diff.z, 1));
                    Vec3d motionDiff = motionNeeded.subtract(player.getVelocity());
                    item.setVelocity(motionDiff.multiply(0.2));
                    Mekanism.packetHandler.sendToAllTrackingAndSelf(new PacketLightningRender(LightningPreset.MAGNETIC_ATTRACTION, Objects.hash(player, item),
                          player.getPos().add(0, 0.2, 0), item.getPos(), (int) (diff.length() * 4)), player);
                }
            }
        }
    }

    public enum Range implements IHasTextComponent {
        OFF(0),
        LOW(1F),
        MED(3F),
        HIGH(5),
        ULTRA(10);

        private final float range;
        private final Text label;

        Range(float boost) {
            this.range = boost;
            this.label = new LiteralText(Float.toString(boost));
        }

        @Override
        public Text getTextComponent() {
            return label;
        }

        public float getRange() {
            return range;
        }
    }
}