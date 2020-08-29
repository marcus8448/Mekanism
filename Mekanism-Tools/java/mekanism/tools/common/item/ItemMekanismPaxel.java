package mekanism.tools.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.Collections;
import java.util.List;
import java.util.function.IntSupplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.functions.FloatSupplier;
import mekanism.common.lib.attribute.AttributeCache;
import mekanism.common.lib.attribute.IAttributeRefresher;
import mekanism.common.registration.impl.ItemDeferredRegister;
import mekanism.tools.common.IHasRepairType;
import mekanism.tools.common.ToolsLang;
import mekanism.tools.common.material.MaterialCreator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.Material;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.Constants.BlockFlags;
import net.minecraftforge.common.util.Constants.WorldEvents;

public class ItemMekanismPaxel extends MiningToolItem implements IHasRepairType, IAttributeRefresher {

    private static final ToolType PAXEL_TOOL_TYPE = ToolType.get("paxel");

    private static Item.Settings getItemProperties(ToolMaterials material) {
        Item.Settings properties = ItemDeferredRegister.getMekBaseProperties();
        if (material == ToolMaterials.NETHERITE) {
            properties = properties.fireproof();
        }
        return addHarvestLevel(properties, material.getMiningLevel());
    }

    private static Item.Settings addHarvestLevel(Item.Settings properties, int harvestLevel) {
        return properties.addToolType(ToolType.AXE, harvestLevel).addToolType(ToolType.PICKAXE, harvestLevel)
              .addToolType(ToolType.SHOVEL, harvestLevel).addToolType(PAXEL_TOOL_TYPE, harvestLevel);
    }

    private final FloatSupplier paxelDamage;
    private final FloatSupplier paxelAtkSpeed;
    private final FloatSupplier paxelEfficiency;
    private final IntSupplier paxelEnchantability;
    private final IntSupplier paxelMaxDurability;
    private final IntSupplier paxelHarvestLevel;
    private final AttributeCache attributeCache;

    public ItemMekanismPaxel(MaterialCreator material, Item.Settings properties) {
        super(material.getPaxelDamage(), material.getPaxelAtkSpeed(), material, Collections.emptySet(), addHarvestLevel(properties, material.getPaxelHarvestLevel()));
        paxelDamage = material::getPaxelDamage;
        paxelAtkSpeed = material::getPaxelAtkSpeed;
        paxelEfficiency = material::getPaxelEfficiency;
        paxelEnchantability = material::getPaxelEnchantability;
        paxelMaxDurability = material::getPaxelMaxUses;
        paxelHarvestLevel = material::getPaxelHarvestLevel;
        this.attributeCache = new AttributeCache(this, material.attackDamage, material.paxelDamage, material.paxelAtkSpeed);
    }

    public ItemMekanismPaxel(ToolMaterials material) {
        super(4, -2.4F, material, Collections.emptySet(), getItemProperties(material));
        paxelDamage = () -> 4;
        paxelAtkSpeed = () -> -2.4F;
        paxelEfficiency = material::getMiningSpeedMultiplier;
        paxelEnchantability = material::getEnchantability;
        paxelMaxDurability = material::getDurability;
        paxelHarvestLevel = material::getMiningLevel;
        //Don't add any listeners as all the values are "static"
        attributeCache = new AttributeCache(this);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<Text> tooltip, @Nonnull TooltipContext flag) {
        tooltip.add(ToolsLang.HP.translate(stack.getMaxDamage() - stack.getDamage()));
    }

    private float getAttackDamage() {
        return paxelDamage.getAsFloat() + getMaterial().getAttackDamage();
    }

    private int getHarvestLevel() {
        return paxelHarvestLevel.getAsInt();
    }

    @Override
    public boolean isEffectiveOn(BlockState state) {
        ToolType harvestTool = state.getHarvestTool();
        if (harvestTool == ToolType.AXE || harvestTool == ToolType.PICKAXE || harvestTool == ToolType.SHOVEL) {
            if (getHarvestLevel() >= state.getHarvestLevel()) {
                //If the required tool type is one of the tools we "support" then return that we can harvest it if
                // we have an equal or higher harvest level
                return true;
            }
        }
        if (state.isOf(Blocks.SNOW) || state.isOf(Blocks.SNOW_BLOCK)) {
            //Extra hardcoded shovel checks
            return true;
        }
        //Extra hardcoded pickaxe checks
        Material material = state.getMaterial();
        return material == Material.STONE || material == Material.METAL || material == Material.REPAIR_STATION;
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Include hardcoded checks from other items and wrap {@link net.minecraft.item.ToolItem#getDestroySpeed(ItemStack, BlockState)} to return our efficiency
     * level
     */
    @Override
    public float getMiningSpeedMultiplier(@Nonnull ItemStack stack, BlockState state) {
        Material material = state.getMaterial();
        //If pickaxe hardcoded shortcut, or axe hardcoded shortcut or ToolItem#getDestroySpeed checks
        //Note: We do it this way so that we don't need to check if the AxeItem material set contains the material if one of the pickaxe checks match
        if (material == Material.METAL || material == Material.REPAIR_STATION || material == Material.STONE || AxeItem.field_23139.contains(material) ||
            getToolTypes(stack).stream().anyMatch(state::isToolEffective) || effectiveBlocks.contains(state.getBlock())) {
            return paxelEfficiency.getAsFloat();
        }
        return 1;
    }

    /**
     * {@inheritDoc}
     *
     * Merged version of {@link AxeItem#onItemUse(ItemUseContext)} and {@link ShovelItem#onItemUse(ItemUseContext)}
     */
    @Nonnull
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockpos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();
        BlockState blockstate = world.getBlockState(blockpos);
        BlockState resultToSet = blockstate.getToolModifiedState(world, blockpos, player, stack, ToolType.AXE);
        if (resultToSet != null) {
            //We can strip the item as an axe
            world.playSound(player, blockpos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
        } else {
            //We cannot strip the item that was right clicked, so attempt to use the paxel as a shovel
            if (context.getSide() == Direction.DOWN) {
                return ActionResult.PASS;
            }
            BlockState foundResult = blockstate.getToolModifiedState(world, blockpos, player, stack, ToolType.SHOVEL);
            if (foundResult != null && world.isAir(blockpos.up())) {
                //We can flatten the item as a shovel
                world.playSound(player, blockpos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                resultToSet = foundResult;
            } else if (blockstate.getBlock() instanceof CampfireBlock && blockstate.get(CampfireBlock.LIT)) {
                //We can use the paxel as a shovel to extinguish a campfire
                if (!world.isClient) {
                    world.syncWorldEvent(null, WorldEvents.FIRE_EXTINGUISH_SOUND, blockpos, 0);
                }
                CampfireBlock.extinguish(world, blockpos, blockstate);
                resultToSet = blockstate.with(CampfireBlock.LIT, false);
            }
        }
        if (resultToSet == null) {
            return ActionResult.PASS;
        }
        if (!world.isClient) {
            world.setBlockState(blockpos, resultToSet, BlockFlags.DEFAULT_AND_RERENDER);
            if (player != null) {
                stack.damage(1, player, onBroken -> onBroken.sendToolBreakStatus(context.getHand()));
            }
        }
        return ActionResult.success(world.isClient);
    }

    @Override
    public int getEnchantability() {
        return paxelEnchantability.getAsInt();
    }

    @Nonnull
    @Override
    public Ingredient getRepairMaterial() {
        return getMaterial().getRepairIngredient();
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return paxelMaxDurability.getAsInt();
    }

    @Override
    public boolean isDamageable() {
        return paxelMaxDurability.getAsInt() > 0;
    }

    @Override
    public int getHarvestLevel(@Nonnull ItemStack stack, @Nonnull ToolType tool, @Nullable PlayerEntity player, @Nullable BlockState blockState) {
        if (tool == ToolType.AXE || tool == ToolType.PICKAXE || tool == ToolType.SHOVEL || tool == PAXEL_TOOL_TYPE) {
            return getHarvestLevel();
        }
        return super.getHarvestLevel(stack, tool, player, blockState);
    }

    @Nonnull
    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(@Nonnull EquipmentSlot slot, @Nonnull ItemStack stack) {
        return slot == EquipmentSlot.MAINHAND ? attributeCache.getAttributes() : ImmutableMultimap.of();
    }

    @Override
    public void addToBuilder(ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder) {
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", getAttackDamage(), Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Tool modifier", paxelAtkSpeed.getAsFloat(), Operation.ADDITION));
    }
}