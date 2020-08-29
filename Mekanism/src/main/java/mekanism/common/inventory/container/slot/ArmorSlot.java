package mekanism.common.inventory.container.slot;

import com.mojang.datafixers.util.Pair;
import javax.annotation.Nonnull;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ArmorSlot extends InsertableSlot {

    public static final Identifier[] ARMOR_SLOT_TEXTURES = new Identifier[]{PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE,
                                                                                        PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE,
                                                                                        PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE,
                                                                                        PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE};

    private final EquipmentSlot slotType;

    public ArmorSlot(PlayerInventory inventory, int index, int x, int y, EquipmentSlot slotType) {
        super(inventory, index, x, y);
        this.slotType = slotType;
    }

    @Override
    public int getMaxStackAmount() {
        return 1;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.canEquip(slotType, ((PlayerInventory) inventory).player);
    }

    @Override
    public boolean canTakeItems(@Nonnull PlayerEntity player) {
        ItemStack itemstack = getStack();
        if (!itemstack.isEmpty() && !player.isCreative() && EnchantmentHelper.hasBindingCurse(itemstack)) {
            return false;
        }
        return super.canTakeItems(player);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Pair<Identifier, Identifier> getBackgroundSprite() {
        return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, ARMOR_SLOT_TEXTURES[slotType.getEntitySlotId()]);
    }
}
