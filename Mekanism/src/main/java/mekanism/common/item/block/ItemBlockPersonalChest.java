package mekanism.common.item.block;

import java.util.List;
import javax.annotation.Nonnull;
import mekanism.api.text.EnumColor;
import mekanism.common.MekanismLang;
import mekanism.common.block.prefab.BlockTile.BlockTileModel;
import mekanism.common.inventory.container.ContainerProvider;
import mekanism.common.inventory.container.item.PersonalChestItemContainer;
import mekanism.common.item.interfaces.IItemSustainedInventory;
import mekanism.common.lib.security.ISecurityItem;
import mekanism.common.registration.impl.ItemDeferredRegister;
import mekanism.common.util.SecurityUtils;
import mekanism.common.util.text.BooleanStateDisplay.YesNo;
import mekanism.common.util.text.OwnerDisplay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkHooks;

public class ItemBlockPersonalChest extends ItemBlockTooltip<BlockTileModel<?, ?>> implements IItemSustainedInventory, ISecurityItem {

    public ItemBlockPersonalChest(BlockTileModel<?, ?> block) {
        super(block, true, ItemDeferredRegister.getMekBaseProperties().maxCount(1));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void addDetails(@Nonnull ItemStack stack, World world, @Nonnull List<Text> tooltip, boolean advanced) {
        tooltip.add(OwnerDisplay.of(MinecraftClient.getInstance().player, getOwnerUUID(stack)).getTextComponent());
        tooltip.add(MekanismLang.SECURITY.translateColored(EnumColor.GRAY, SecurityUtils.getSecurity(stack, Dist.CLIENT)));
        if (SecurityUtils.isOverridden(stack, Dist.CLIENT)) {
            tooltip.add(MekanismLang.SECURITY_OVERRIDDEN.translateColored(EnumColor.RED));
        }
        tooltip.add(MekanismLang.HAS_INVENTORY.translateColored(EnumColor.AQUA, EnumColor.GRAY, YesNo.of(hasInventory(stack))));
    }

    @Nonnull
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!world.isClient) {
            if (getOwnerUUID(stack) == null) {
                setOwnerUUID(stack, player.getUuid());
            }
            if (SecurityUtils.canAccess(player, stack)) {
                NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerProvider(stack.getName(),
                      (i, inv, p) -> new PersonalChestItemContainer(i, inv, hand, stack)), buf -> {
                    buf.writeEnumConstant(hand);
                    buf.writeItemStack(stack);
                });
            } else {
                SecurityUtils.displayNoAccess(player);
            }
        }
        return new TypedActionResult<>(ActionResult.PASS, stack);
    }

    @Override
    protected boolean canPlace(@Nonnull ItemPlacementContext context, @Nonnull BlockState state) {
        PlayerEntity player = context.getPlayer();
        //Only allow placing if there is no player, it is a fake player, or the player is sneaking
        return (player == null || player instanceof FakePlayer || player.isSneaking()) && super.canPlace(context, state);
    }
}