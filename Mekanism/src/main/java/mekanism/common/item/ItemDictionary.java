package mekanism.common.item;

import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.api.text.EnumColor;
import mekanism.client.MekKeyHandler;
import mekanism.client.MekanismKeyHandler;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.ContainerProvider;
import mekanism.common.inventory.container.item.DictionaryContainer;
import mekanism.common.util.MekanismUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.world.RayTraceContext.FluidHandling;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class ItemDictionary extends Item {

    public ItemDictionary(Settings properties) {
        super(properties.maxCount(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<Text> tooltip, @Nonnull TooltipContext flag) {
        if (MekKeyHandler.getIsKeyPressed(MekanismKeyHandler.descriptionKey)) {
            tooltip.add(MekanismLang.DESCRIPTION_DICTIONARY.translate());
        } else {
            tooltip.add(MekanismLang.HOLD_FOR_DESCRIPTION.translateColored(EnumColor.GRAY, EnumColor.AQUA, MekanismKeyHandler.descriptionKey.getBoundKeyLocalizedText()));
        }
    }

    @Nonnull
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        if (player != null && !player.isSneaking()) {
            World world = context.getWorld();
            if (!world.isClient) {
                sendTagsToPlayer(player, world.getBlockState(context.getBlockPos()).getBlock().getTags());
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Nonnull
    @Override
    public ActionResult useOnEntity(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, @Nonnull LivingEntity entity, @Nonnull Hand hand) {
        if (!player.isSneaking()) {
            if (!player.world.isClient) {
                sendTagsToPlayer(player, entity.getType().getTags());
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Nonnull
    @Override
    public TypedActionResult<ItemStack> use(@Nonnull World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (player.isSneaking()) {
            if (!world.isClient()) {
                NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerProvider(stack.getName(), (i, inv, p) -> new DictionaryContainer(i, inv, hand, stack)),
                      buf -> {
                          buf.writeEnumConstant(hand);
                          buf.writeItemStack(stack);
                      });
            }
            return new TypedActionResult<>(ActionResult.SUCCESS, stack);
        } else {
            BlockHitResult result = MekanismUtils.rayTrace(player, FluidHandling.ANY);
            if (result.getType() != Type.MISS) {
                Block block = world.getBlockState(result.getBlockPos()).getBlock();
                if (block instanceof FluidBlock) {
                    if (!world.isClient()) {
                        sendTagsToPlayer(player, ((FluidBlock) block).getFluid().getTags());
                    }
                    return new TypedActionResult<>(ActionResult.SUCCESS, stack);
                }
            }
        }
        return new TypedActionResult<>(ActionResult.PASS, stack);
    }

    private void sendTagsToPlayer(PlayerEntity player, Set<Identifier> tags) {
        if (tags.isEmpty()) {
            player.sendSystemMessage(MekanismLang.LOG_FORMAT.translateColored(EnumColor.DARK_BLUE, MekanismLang.MEKANISM, EnumColor.GRAY, MekanismLang.DICTIONARY_NO_KEY), Util.NIL_UUID);
        } else {
            player.sendSystemMessage(MekanismLang.LOG_FORMAT.translateColored(EnumColor.DARK_BLUE, MekanismLang.MEKANISM, EnumColor.GRAY, MekanismLang.DICTIONARY_KEYS_FOUND), Util.NIL_UUID);
            for (Identifier tag : tags) {
                player.sendSystemMessage(MekanismLang.DICTIONARY_KEY.translateColored(EnumColor.DARK_GREEN, tag), Util.NIL_UUID);
            }
        }
    }
}