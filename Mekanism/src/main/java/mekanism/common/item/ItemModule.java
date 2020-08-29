package mekanism.common.item;

import java.util.List;
import javax.annotation.Nonnull;
import mekanism.api.text.EnumColor;
import mekanism.client.MekKeyHandler;
import mekanism.client.MekanismKeyHandler;
import mekanism.common.MekanismLang;
import mekanism.common.content.gear.IModuleItem;
import mekanism.common.content.gear.Modules;
import mekanism.common.content.gear.Modules.ModuleData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemModule extends Item implements IModuleItem {

    private final ModuleData<?> moduleData;

    public ItemModule(ModuleData<?> moduleData, Settings properties) {
        super(properties.maxCount(moduleData.getMaxStackSize()).rarity(Rarity.UNCOMMON));
        moduleData.setStack(this);
        this.moduleData = moduleData;
    }

    @Override
    public ModuleData<?> getModuleData() {
        return moduleData;
    }

    @Nonnull
    @Override
    public Rarity getRarity(@Nonnull ItemStack stack) {
        return moduleData.getRarity();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(@Nonnull ItemStack stack, World world, @Nonnull List<Text> tooltip, @Nonnull TooltipContext flag) {
        if (MekKeyHandler.getIsKeyPressed(MekanismKeyHandler.detailsKey)) {
            for (Item item : Modules.getSupported(getModuleData())) {
                tooltip.add(item.getName(new ItemStack(item)));
            }
        } else {
            tooltip.add(moduleData.getDescription());
            tooltip.add(MekanismLang.MODULE_STACKABLE.translateColored(EnumColor.GRAY, EnumColor.AQUA, moduleData.getMaxStackSize()));
            tooltip.add(MekanismLang.HOLD_FOR_SUPPORTED_ITEMS.translateColored(EnumColor.GRAY, EnumColor.INDIGO, MekanismKeyHandler.detailsKey.getBoundKeyLocalizedText()));
        }
    }

    @Nonnull
    @Override
    public String getTranslationKey() {
        return moduleData.getTranslationKey();
    }
}
