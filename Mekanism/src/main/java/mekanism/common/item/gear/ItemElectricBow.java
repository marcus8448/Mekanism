package mekanism.common.item.gear;

import java.util.List;
import javax.annotation.Nonnull;
import mekanism.api.Action;
import mekanism.api.NBTConstants;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.AutomationType;
import mekanism.api.math.FloatingLong;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.capabilities.energy.item.RateLimitEnergyHandler;
import mekanism.common.config.MekanismConfig;
import mekanism.common.item.interfaces.IItemHUDProvider;
import mekanism.common.item.interfaces.IModeItem;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.StorageUtils;
import mekanism.common.util.text.BooleanStateDisplay.OnOff;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.ForgeEventFactory;

public class ItemElectricBow extends BowItem implements IModeItem, IItemHUDProvider {

    public ItemElectricBow(Settings properties) {
        super(properties.rarity(Rarity.RARE).setNoRepair().maxCount(1));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(@Nonnull ItemStack stack, World world, @Nonnull List<Text> tooltip, @Nonnull TooltipContext flag) {
        StorageUtils.addStoredEnergy(stack, tooltip, true);
        tooltip.add(MekanismLang.FIRE_MODE.translateColored(EnumColor.PINK, OnOff.of(getFireState(stack))));
    }

    @Override
    public void onStoppedUsing(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entityLiving;
            //Vanilla diff - Get the energy container, because if something went wrong and we don't have one then we can exit early
            IEnergyContainer energyContainer = null;
            boolean fireState = getFireState(stack);
            if (!player.isCreative()) {
                energyContainer = StorageUtils.getEnergyContainer(stack, 0);
                FloatingLong energyNeeded = fireState ? MekanismConfig.gear.electricBowEnergyUsageFire.get() : MekanismConfig.gear.electricBowEnergyUsage.get();
                if (energyContainer == null || energyContainer.extract(energyNeeded, Action.SIMULATE, AutomationType.MANUAL).smallerThan(energyNeeded)) {
                    return;
                }
            }
            boolean infinity = player.isCreative() || EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0;
            ItemStack ammo = player.getArrowType(stack);
            int charge = ForgeEventFactory.onArrowLoose(stack, world, player, getMaxUseTime(stack) - timeLeft, !ammo.isEmpty() || infinity);
            if (charge < 0) {
                return;
            }
            if (!ammo.isEmpty() || infinity) {
                float velocity = getPullProgress(charge);
                if (velocity < 0.1) {
                    return;
                }
                if (ammo.isEmpty()) {
                    ammo = new ItemStack(Items.ARROW);
                }
                boolean noConsume = player.isCreative() || (ammo.getItem() instanceof ArrowItem && ((ArrowItem) ammo.getItem()).isInfinite(ammo, stack, player));
                if (!world.isClient) {
                    ArrowItem arrowitem = (ArrowItem) (ammo.getItem() instanceof ArrowItem ? ammo.getItem() : Items.ARROW);
                    PersistentProjectileEntity arrowEntity = arrowitem.createArrow(world, ammo, player);
                    arrowEntity = customArrow(arrowEntity);
                    arrowEntity.setProperties(player, player.pitch, player.yaw, 0, 3 * velocity, 1);
                    if (velocity == 1) {
                        arrowEntity.setCritical(true);
                    }
                    int power = EnchantmentHelper.getLevel(Enchantments.POWER, stack);
                    if (power > 0) {
                        arrowEntity.setDamage(arrowEntity.getDamage() + 0.5 * power + 0.5);
                    }
                    int punch = EnchantmentHelper.getLevel(Enchantments.PUNCH, stack);
                    if (punch > 0) {
                        arrowEntity.setPunch(punch);
                    }
                    //Vanilla diff - set it on fire if the bow's mode is set to fire, even if there is no flame enchantment
                    if (fireState || EnchantmentHelper.getLevel(Enchantments.FLAME, stack) > 0) {
                        arrowEntity.setOnFireFor(100);
                    }
                    //Vanilla diff - Instead of damaging the item we remove energy from it
                    if (!player.isCreative() && energyContainer != null) {
                        energyContainer.extract(fireState ? MekanismConfig.gear.electricBowEnergyUsageFire.get() : MekanismConfig.gear.electricBowEnergyUsage.get(), Action.EXECUTE, AutomationType.MANUAL);
                    }
                    if (noConsume || player.isCreative() && (ammo.getItem() == Items.SPECTRAL_ARROW || ammo.getItem() == Items.TIPPED_ARROW)) {
                        arrowEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                    }
                    world.spawnEntity(arrowEntity);
                }
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1,
                      1.0F / (RANDOM.nextFloat() * 0.4F + 1.2F) + velocity * 0.5F);
                if (!noConsume && !player.isCreative()) {
                    ammo.decrement(1);
                    if (ammo.isEmpty()) {
                        player.inventory.removeOne(ammo);
                    }
                }
                player.incrementStat(Stats.USED.getOrCreateStat(this));
            }
        }
    }

    public void setFireState(ItemStack stack, boolean state) {
        ItemDataUtils.setBoolean(stack, NBTConstants.MODE, state);
    }

    public boolean getFireState(ItemStack stack) {
        return ItemDataUtils.getBoolean(stack, NBTConstants.MODE);
    }

    @Override
    public void addHUDStrings(List<Text> list, ItemStack stack, EquipmentSlot slotType) {
        list.add(MekanismLang.FIRE_MODE.translateColored(EnumColor.PINK, OnOff.of(getFireState(stack))));
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return StorageUtils.getEnergyDurabilityForDisplay(stack);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return MekanismConfig.client.energyColor.get();
    }

    @Override
    public void appendStacks(@Nonnull ItemGroup group, @Nonnull DefaultedList<ItemStack> items) {
        super.appendStacks(group, items);
        if (isIn(group)) {
            items.add(StorageUtils.getFilledEnergyVariant(new ItemStack(this), MekanismConfig.gear.electricBowMaxEnergy.get()));
        }
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        //Note: We interact with this capability using "manual" as the automation type, to ensure we can properly bypass the energy limit for extracting
        // Internal is used by the "null" side, which is what will get used for most items
        return new ItemCapabilityWrapper(stack, RateLimitEnergyHandler.create(MekanismConfig.gear.electricBowChargeRate, MekanismConfig.gear.electricBowMaxEnergy,
              BasicEnergyContainer.manualOnly, BasicEnergyContainer.alwaysTrue));
    }

    @Override
    public void changeMode(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, int shift, boolean displayChangeMessage) {
        if (Math.abs(shift) % 2 == 1) {
            //We are changing by an odd amount, so toggle the mode
            boolean newState = !getFireState(stack);
            setFireState(stack, newState);
            if (displayChangeMessage) {
                player.sendSystemMessage(MekanismLang.LOG_FORMAT.translateColored(EnumColor.DARK_BLUE, MekanismLang.MEKANISM,
                      MekanismLang.FIRE_MODE.translateColored(EnumColor.GRAY, OnOff.of(newState, true))), Util.NIL_UUID);
            }
        }
    }

    @Nonnull
    @Override
    public Text getScrollTextComponent(@Nonnull ItemStack stack) {
        return MekanismLang.FIRE_MODE.translateColored(EnumColor.PINK, OnOff.of(getFireState(stack), true));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        //Ignore NBT for energized items causing re-equip animations
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        //Ignore NBT for energized items causing block break reset
        return oldStack.getItem() != newStack.getItem();
    }
}