package mekanism.common.item.gear;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.Action;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.AutomationType;
import mekanism.api.math.FloatingLong;
import mekanism.api.text.EnumColor;
import mekanism.api.text.ILangEntry;
import mekanism.client.MekKeyHandler;
import mekanism.client.MekanismKeyHandler;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.block.BlockBounding;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.gear.Module;
import mekanism.common.content.gear.Modules;
import mekanism.common.content.gear.mekatool.ModuleExcavationEscalationUnit;
import mekanism.common.content.gear.mekatool.ModuleMekaTool;
import mekanism.common.content.gear.mekatool.ModuleVeinMiningUnit;
import mekanism.common.content.gear.shared.ModuleEnergyUnit;
import mekanism.common.item.ItemEnergized;
import mekanism.common.item.interfaces.IItemHUDProvider;
import mekanism.common.item.interfaces.IModeItem;
import mekanism.common.network.PacketPortalFX;
import mekanism.common.tags.MekanismTags;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.StorageUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.IFluidBlock;

public class ItemMekaTool extends ItemEnergized implements IModuleContainerItem, IModeItem, IItemHUDProvider {

    private final Multimap<EntityAttribute, EntityAttributeModifier> attributes;

    public ItemMekaTool(Settings properties) {
        super(MekanismConfig.gear.mekaToolBaseChargeRate, MekanismConfig.gear.mekaToolBaseEnergyCapacity, properties.rarity(Rarity.EPIC).setNoRepair());
        Modules.setSupported(this, Modules.ENERGY_UNIT, Modules.ATTACK_AMPLIFICATION_UNIT, Modules.SILK_TOUCH_UNIT, Modules.VEIN_MINING_UNIT,
              Modules.FARMING_UNIT, Modules.TELEPORTATION_UNIT, Modules.EXCAVATION_ESCALATION_UNIT);
        Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", -2.4D, Operation.ADDITION));
        this.attributes = builder.build();
    }

    @Override
    public boolean isEffectiveOn(@Nonnull BlockState state) {
        //Allow harvesting everything, things that are unbreakable are caught elsewhere
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(@Nonnull ItemStack stack, World world, @Nonnull List<Text> tooltip, @Nonnull TooltipContext flag) {
        if (MekKeyHandler.getIsKeyPressed(MekanismKeyHandler.detailsKey)) {
            for (Module module : Modules.loadAll(stack)) {
                ILangEntry langEntry = module.getData().getLangEntry();
                if (module.getInstalledCount() > 1) {
                    Text amount = MekanismLang.GENERIC_FRACTION.translate(module.getInstalledCount(), module.getData().getMaxStackSize());
                    tooltip.add(MekanismLang.GENERIC_WITH_PARENTHESIS.translateColored(EnumColor.GRAY, langEntry, amount));
                } else {
                    tooltip.add(langEntry.translateColored(EnumColor.GRAY));
                }
            }
        } else {
            StorageUtils.addStoredEnergy(stack, tooltip, true);
            tooltip.add(MekanismLang.HOLD_FOR_MODULES.translateColored(EnumColor.GRAY, EnumColor.INDIGO, MekanismKeyHandler.detailsKey.getBoundKeyLocalizedText()));
        }
    }

    @Nonnull
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        for (Module module : Modules.loadAll(context.getStack())) {
            if (module.isEnabled() && module instanceof ModuleMekaTool) {
                ActionResult result = ((ModuleMekaTool) module).onItemUse(context);
                if (result != ActionResult.PASS) {
                    return result;
                }
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public float getMiningSpeedMultiplier(@Nonnull ItemStack stack, @Nonnull BlockState state) {
        IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
        ModuleExcavationEscalationUnit module = Modules.load(stack, Modules.EXCAVATION_ESCALATION_UNIT);
        double efficiency = module == null || !module.isEnabled() ? MekanismConfig.gear.mekaToolBaseEfficiency.get() : module.getEfficiency();
        return energyContainer == null || energyContainer.isEmpty() ? 1 : (float) efficiency;
    }

    @Override
    public boolean postMine(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull BlockState state, @Nonnull BlockPos pos, @Nonnull LivingEntity entityliving) {
        IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
        if (energyContainer != null) {
            energyContainer.extract(getDestroyEnergy(stack, state.getHardness(world, pos), false), Action.EXECUTE, AutomationType.MANUAL);
        }
        return true;
    }

    @Override
    public boolean postHit(@Nonnull ItemStack stack, @Nonnull LivingEntity target, @Nonnull LivingEntity attacker) {
        IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
        FloatingLong energy = energyContainer == null ? FloatingLong.ZERO : energyContainer.getEnergy();
        FloatingLong energyCost = FloatingLong.ZERO;
        int minDamage = MekanismConfig.gear.mekaToolBaseDamage.get(), maxDamage = minDamage;
        if (isModuleEnabled(stack, Modules.ATTACK_AMPLIFICATION_UNIT)) {
            maxDamage = Modules.load(stack, Modules.ATTACK_AMPLIFICATION_UNIT).getDamage();
            if (maxDamage > minDamage) {
                energyCost = MekanismConfig.gear.mekaToolEnergyUsageWeapon.get().multiply((maxDamage - minDamage) / 4F);
            }
            minDamage = Math.min(minDamage, maxDamage);
        }
        int damageDifference = maxDamage - minDamage;
        //If we don't have enough power use it at a reduced power level
        double percent = 1;
        if (energy.smallerThan(energyCost)) {
            percent = energy.divideToLevel(energyCost);
        }
        float damage = (float) (minDamage + damageDifference * percent);
        if (attacker instanceof PlayerEntity) {
            target.damage(DamageSource.player((PlayerEntity) attacker), damage);
        } else {
            target.damage(DamageSource.mob(attacker), damage);
        }
        if (energyContainer != null && !energy.isZero()) {
            energyContainer.extract(energyCost, Action.EXECUTE, AutomationType.MANUAL);
        }
        return false;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {
        World world = player.world;
        if (!world.isClient && !player.isCreative()) {
            BlockState state = world.getBlockState(pos);
            IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
            if (energyContainer == null) {
                //If something went wrong and we don't have an energy container, just go to super
                return super.onBlockStartBreak(stack, pos, player);
            }
            boolean silk = isModuleEnabled(stack, Modules.SILK_TOUCH_UNIT);
            if (silk) {
                // if we can't break the initial block, terminate
                if (!breakBlock(stack, world, pos, (ServerPlayerEntity) player, energyContainer, true)) {
                    return super.onBlockStartBreak(stack, pos, player);
                }
            }
            if (isModuleEnabled(stack, Modules.VEIN_MINING_UNIT)) {
                ModuleVeinMiningUnit module = Modules.load(stack, Modules.VEIN_MINING_UNIT);
                boolean extended = module.isExtended();
                if (state.getBlock() instanceof BlockBounding) {
                    //Even though we now handle breaking bounding blocks properly, don't allow vein mining
                    // them as an added safety measure
                    return silk;
                }
                //If it is extended or should be treated as an ore
                if (extended || state.isIn(MekanismTags.Blocks.ATOMIC_DISASSEMBLER_ORE)) {
                    ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
                    Set<BlockPos> found = ModuleVeinMiningUnit.findPositions(state, pos, world, extended ? module.getExcavationRange() : -1);
                    for (BlockPos foundPos : found) {
                        if (pos.equals(foundPos)) {
                            continue;
                        }
                        breakBlock(stack, world, foundPos, serverPlayerEntity, energyContainer, silk);
                    }
                }
            }
        }
        return super.onBlockStartBreak(stack, pos, player);
    }

    private boolean breakBlock(ItemStack stack, World world, BlockPos pos, ServerPlayerEntity player, IEnergyContainer energyContainer, boolean silk) {
        BlockState state = world.getBlockState(pos);
        FloatingLong destroyEnergy = getDestroyEnergy(stack, state.getHardness(world, pos), silk);
        if (energyContainer.extract(destroyEnergy, Action.SIMULATE, AutomationType.MANUAL).smallerThan(destroyEnergy)) {
            return false;
        }
        int exp = ForgeHooks.onBlockBreakEvent(world, player.interactionManager.getGameMode(), player, pos);
        if (exp == -1) {
            //If we can't actually break the block continue (this allows mods to stop us from vein mining into protected land)
            return false;
        }
        //Otherwise break the block
        Block block = state.getBlock();
        //Get the tile now so that we have it for when we try to harvest the block
        BlockEntity tileEntity = MekanismUtils.getTileEntity(world, pos);
        //Remove the block
        boolean removed = state.removedByPlayer(world, pos, player, true, world.getFluidState(pos));
        if (removed) {
            block.onBroken(world, pos, state);
            //Harvest the block allowing it to handle block drops, incrementing block mined count, and adding exhaustion
            ItemStack harvestTool = stack.copy();
            if (silk) {
                harvestTool.addEnchantment(Enchantments.SILK_TOUCH, 1);
                //Calculate the proper amount of xp that the state would drop if broken with a silk touch tool
                //Note: This fixes ores and the like dropping xp when broken with silk touch but makes it so that
                // BlockEvent.BreakEvent is unable to be used to adjust the amount of xp dropped.
                //TODO: look into if there is a better way for us to handle this as BlockEvent.BreakEvent goes based
                // of the item in the main hand so there isn't an easy way to change this without actually enchanting the meka-tool
                exp = state.getExpDrop(world, pos, 0, 1);
            }
            block.afterBreak(world, player, pos, state, tileEntity, harvestTool);
            player.incrementStat(Stats.USED.getOrCreateStat(this));
            if (exp > 0) {
                //If we have xp drop it
                block.dropExperience(world, pos, exp);
            }
            //Use energy
            energyContainer.extract(destroyEnergy, Action.EXECUTE, AutomationType.MANUAL);
        }

        return true;
    }

    private FloatingLong getDestroyEnergy(ItemStack itemStack, float hardness, boolean silk) {
        FloatingLong destroyEnergy = silk ? MekanismConfig.gear.mekaToolEnergyUsageSilk.get() : MekanismConfig.gear.mekaToolEnergyUsage.get();
        ModuleExcavationEscalationUnit module = Modules.load(itemStack, Modules.EXCAVATION_ESCALATION_UNIT);
        double efficiency = module == null || !module.isEnabled() ? MekanismConfig.gear.mekaToolBaseEfficiency.get() : module.getEfficiency();
        destroyEnergy = destroyEnergy.multiply(efficiency);
        return hardness == 0 ? destroyEnergy.divide(2) : destroyEnergy;
    }

    @Nonnull
    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlot slot, @Nonnull ItemStack stack) {
        return slot == EquipmentSlot.MAINHAND ? attributes : super.getAttributeModifiers(slot, stack);
    }

    @Nonnull
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!world.isClient() && isModuleEnabled(stack, Modules.TELEPORTATION_UNIT)) {
            BlockHitResult result = MekanismUtils.rayTrace(player, MekanismConfig.gear.mekaToolMaxTeleportReach.get());
            if (result.getType() != HitResult.Type.MISS) {
                BlockPos pos = result.getBlockPos();
                // make sure we fit
                if (isValidDestinationBlock(world, pos.up()) && isValidDestinationBlock(world, pos.up(2))) {
                    double distance = player.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ());
                    if (distance < 5) {
                        return new TypedActionResult<>(ActionResult.PASS, stack);
                    }
                    IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(stack, 0);
                    FloatingLong energyNeeded = MekanismConfig.gear.mekaToolEnergyUsageTeleport.get().multiply(distance / 10D);
                    if (energyContainer == null || energyContainer.getEnergy().smallerThan(energyNeeded)) {
                        return new TypedActionResult<>(ActionResult.FAIL, stack);
                    }
                    energyContainer.extract(energyNeeded, Action.EXECUTE, AutomationType.MANUAL);
                    player.requestTeleport(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);
                    player.fallDistance = 0.0F;
                    Mekanism.packetHandler.sendToAllTracking(new PacketPortalFX(pos.up()), world, pos);
                    world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    return new TypedActionResult<>(ActionResult.SUCCESS, stack);
                }
            }
        }
        return new TypedActionResult<>(ActionResult.PASS, stack);
    }

    private boolean isValidDestinationBlock(World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        //Allow teleporting into air or fluids
        return blockState.isAir(world, pos) || blockState.getBlock() instanceof FluidBlock || blockState.getBlock() instanceof IFluidBlock;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        if (stack.getTag() == null) {
            stack.setTag(new CompoundTag());
        }
        stack.getTag().putInt("HideFlags", 2);
        return super.initCapabilities(stack, nbt);
    }

    @Override
    public void addHUDStrings(List<Text> list, ItemStack stack, EquipmentSlot slotType) {
        for (Module module : Modules.loadAll(stack)) {
            if (module.renderHUD()) {
                module.addHUDStrings(list);
            }
        }
    }

    @Override
    public void changeMode(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, int shift, boolean displayChangeMessage) {
        for (Module module : Modules.loadAll(stack)) {
            if (module.handlesModeChange()) {
                module.changeMode(player, stack, shift, displayChangeMessage);
                return;
            }
        }
    }

    @Override
    protected FloatingLong getMaxEnergy(ItemStack stack) {
        ModuleEnergyUnit module = Modules.load(stack, Modules.ENERGY_UNIT);
        return module != null ? module.getEnergyCapacity() : MekanismConfig.gear.mekaToolBaseEnergyCapacity.get();
    }

    @Override
    protected FloatingLong getChargeRate(ItemStack stack) {
        ModuleEnergyUnit module = Modules.load(stack, Modules.ENERGY_UNIT);
        return module != null ? module.getChargeRate() : MekanismConfig.gear.mekaToolBaseChargeRate.get();
    }
}
