package mekanism.common.item.interfaces;

import javax.annotation.Nonnull;
import mekanism.client.render.armor.CustomArmor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface ISpecialGear {

    @Nonnull
    @Environment(EnvType.CLIENT)
    CustomArmor getGearModel();
}