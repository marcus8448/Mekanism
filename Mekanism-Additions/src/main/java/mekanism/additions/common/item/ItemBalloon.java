package mekanism.additions.common.item;

import java.util.List;
import javax.annotation.Nonnull;
import mekanism.additions.common.entity.EntityBalloon;
import mekanism.api.text.EnumColor;
import mekanism.api.text.TextComponentUtil;
import mekanism.common.lib.math.Pos3D;
import mekanism.common.registration.impl.ItemDeferredRegister;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ItemBalloon extends Item {

    private final EnumColor color;

    public ItemBalloon(EnumColor color) {
        super(ItemDeferredRegister.getMekBaseProperties());
        this.color = color;
        DispenserBlock.registerBehavior(this, new DispenserBehavior());
    }

    public EnumColor getColor() {
        return color;
    }

    @Nonnull
    @Override
    public TypedActionResult<ItemStack> use(World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
        if (!world.isClient) {
            Pos3D pos = new Pos3D(hand == Hand.MAIN_HAND ? -0.4 : 0.4, 0, 0.3).rotateY(player.bodyYaw).translate(new Pos3D(player));
            world.spawnEntity(new EntityBalloon(world, pos.x - 0.5, pos.y - 1.25, pos.z - 0.5, color));
        }
        ItemStack stack = player.getStackInHand(hand);
        if (!player.isCreative()) {
            stack.decrement(1);
        }
        return new TypedActionResult<>(ActionResult.SUCCESS, stack);
    }

    @Nonnull
    @Override
    public Text getName(@Nonnull ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof ItemBalloon) {
            return TextComponentUtil.build(((ItemBalloon) item).getColor(), super.getName(stack));
        }
        return super.getName(stack);
    }

    @Nonnull
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        if (player == null) {
            return ActionResult.PASS;
        }
        ItemStack stack = player.getStackInHand(context.getHand());
        if (player.isSneaking()) {
            BlockPos pos = context.getBlockPos();
            Box bound = new Box(pos, pos.add(1, 3, 1));
            List<EntityBalloon> balloonsNear = player.world.getNonSpectatingEntities(EntityBalloon.class, bound);
            if (!balloonsNear.isEmpty()) {
                return ActionResult.FAIL;
            }
            World world = context.getWorld();
            if (MekanismUtils.isValidReplaceableBlock(world, pos)) {
                pos = pos.down();
            }
            if (!Block.sideCoversSmallSquare(world, pos, Direction.UP)) {
                return ActionResult.FAIL;
            }
            if (MekanismUtils.isValidReplaceableBlock(world, pos.up()) && MekanismUtils.isValidReplaceableBlock(world, pos.up(2))) {
                world.removeBlock(pos.up(), false);
                world.removeBlock(pos.up(2), false);
                if (!world.isClient) {
                    world.spawnEntity(new EntityBalloon(world, pos, color));
                    stack.decrement(1);
                }
                return ActionResult.SUCCESS;
            }
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }

    @Nonnull
    @Override
    public ActionResult useOnEntity(@Nonnull ItemStack stack, PlayerEntity player, @Nonnull LivingEntity entity, @Nonnull Hand hand) {
        if (player.isSneaking()) {
            if (!player.world.isClient) {
                Box bound = new Box(entity.getX() - 0.2, entity.getY() - 0.5, entity.getZ() - 0.2,
                      entity.getX() + 0.2, entity.getY() + entity.getDimensions(entity.getPose()).height + 4, entity.getZ() + 0.2);
                List<EntityBalloon> balloonsNear = player.world.getNonSpectatingEntities(EntityBalloon.class, bound);
                for (EntityBalloon balloon : balloonsNear) {
                    if (balloon.latchedEntity == entity) {
                        return ActionResult.SUCCESS;
                    }
                }
                player.world.spawnEntity(new EntityBalloon(entity, color));
                stack.decrement(1);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public class DispenserBehavior extends ItemDispenserBehavior {

        @Nonnull
        @Override
        public ItemStack dispenseSilently(BlockPointer source, @Nonnull ItemStack stack) {
            Direction side = source.getBlockState().get(DispenserBlock.FACING);
            BlockPos sourcePos = source.getBlockPos();
            BlockPos offsetPos = sourcePos.offset(side);
            List<LivingEntity> entities = source.getWorld().getNonSpectatingEntities(LivingEntity.class, new Box(offsetPos, offsetPos.add(1, 1, 1)));
            boolean latched = false;

            for (LivingEntity entity : entities) {
                Box bound = new Box(entity.getX() - 0.2, entity.getY() - 0.5, entity.getZ() - 0.2,
                      entity.getX() + 0.2, entity.getY() + entity.getDimensions(entity.getPose()).height + 4, entity.getZ() + 0.2);
                List<EntityBalloon> balloonsNear = source.getWorld().getNonSpectatingEntities(EntityBalloon.class, bound);
                boolean hasBalloon = false;
                for (EntityBalloon balloon : balloonsNear) {
                    if (balloon.latchedEntity == entity) {
                        hasBalloon = true;
                        break;
                    }
                }
                if (!hasBalloon) {
                    source.getWorld().spawnEntity(new EntityBalloon(entity, color));
                    latched = true;
                }
            }
            if (!latched) {
                Pos3D pos = Pos3D.create(sourcePos).translate(0, -0.5, 0);
                switch (side) {
                    case DOWN:
                        pos = pos.translate(0, -2.5, 0);
                        break;
                    case UP:
                        pos = pos.translate(0, 0, 0);
                        break;
                    case NORTH:
                        pos = pos.translate(0, -1, -0.5);
                        break;
                    case SOUTH:
                        pos = pos.translate(0, -1, 0.5);
                        break;
                    case WEST:
                        pos = pos.translate(-0.5, -1, 0);
                        break;
                    case EAST:
                        pos = pos.translate(0.5, -1, 0);
                        break;
                    default:
                        break;
                }
                if (!source.getWorld().isClient) {
                    source.getWorld().spawnEntity(new EntityBalloon(source.getWorld(), pos.x, pos.y, pos.z, color));
                }
            }
            stack.decrement(1);
            return stack;
        }
    }
}