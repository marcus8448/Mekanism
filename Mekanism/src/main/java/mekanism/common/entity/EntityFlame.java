package mekanism.common.entity;

import java.util.Optional;
import javax.annotation.Nonnull;
import mekanism.api.NBTConstants;
import mekanism.common.config.MekanismConfig;
import mekanism.common.item.gear.ItemFlamethrower;
import mekanism.common.item.gear.ItemFlamethrower.FlamethrowerMode;
import mekanism.common.lib.math.Pos3D;
import mekanism.common.registries.MekanismEntityTypes;
import mekanism.common.util.NBTUtils;
import mekanism.common.util.StackUtils;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.RayTraceContext.FluidHandling;
import net.minecraft.world.RayTraceContext.ShapeType;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.WorldEvents;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityFlame extends ProjectileEntity implements IEntityAdditionalSpawnData {

    public static final int LIFESPAN = 80;
    private static final int DAMAGE = 10;

    private FlamethrowerMode mode = FlamethrowerMode.COMBAT;

    public EntityFlame(EntityType<EntityFlame> type, World world) {
        super(type, world);
    }

    public EntityFlame(PlayerEntity player) {
        this(MekanismEntityTypes.FLAME.getEntityType(), player.world);
        Pos3D playerPos = new Pos3D(player.getX(), player.getEyeY() - 0.1, player.getZ());
        Pos3D flameVec = new Pos3D(1, 1, 1);

        Vec3d lookVec = player.getRotationVector();
        flameVec = flameVec.multiply(lookVec).rotateY(6);

        Pos3D mergedVec = playerPos.translate(flameVec);
        updatePosition(mergedVec.x, mergedVec.y, mergedVec.z);
        setOwner(player);
        mode = ((ItemFlamethrower) player.inventory.getMainHandStack().getItem()).getMode(player.inventory.getMainHandStack());
        setProperties(player, player.pitch, player.yaw, 0, 0.5F, 1);
    }

    @Override
    public void baseTick() {
        if (!isAlive()) {
            return;
        }
        age++;

        prevX = getX();
        prevY = getY();
        prevZ = getZ();

        prevPitch = pitch;
        prevYaw = yaw;

        Vec3d motion = getVelocity();
        setPos(getX() + motion.getX(), getY() + motion.getY(), getZ() + motion.getZ());

        updatePosition(getX(), getY(), getZ());

        calculateVector();
        if (age > LIFESPAN) {
            remove();
        }
    }

    private void calculateVector() {
        Vec3d localVec = new Vec3d(getX(), getY(), getZ());
        Vec3d motion = getVelocity();
        Vec3d motionVec = new Vec3d(getX() + motion.getX() * 2, getY() + motion.getY() * 2, getZ() + motion.getZ() * 2);
        BlockHitResult blockRayTrace = world.rayTrace(new RayTraceContext(localVec, motionVec, ShapeType.COLLIDER, FluidHandling.ANY, this));
        localVec = new Vec3d(getX(), getY(), getZ());
        motionVec = new Vec3d(getX() + motion.getX(), getY() + motion.getY(), getZ() + motion.getZ());
        if (blockRayTrace.getType() != Type.MISS) {
            motionVec = blockRayTrace.getPos();
        }
        EntityHitResult entityResult = ProjectileUtil.getEntityCollision(world, this, localVec, motionVec,
              getBoundingBox().stretch(getVelocity()).expand(1.0D, 1.0D, 1.0D), EntityPredicates.EXCEPT_SPECTATOR);
        onCollision(entityResult == null ? blockRayTrace : entityResult);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityResult) {
        Entity entity = entityResult.getEntity();
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity.getEntity();
            Entity owner = getOwner();
            if (player.abilities.invulnerable || owner instanceof PlayerEntity && !((PlayerEntity) owner).shouldDamagePlayer(player)) {
                return;
            }
        }
        if (!entity.getEntity().isFireImmune()) {
            if (entity.getEntity() instanceof ItemEntity && mode == FlamethrowerMode.HEAT) {
                if (entity.getEntity().age > 100 && !smeltItem((ItemEntity) entity.getEntity())) {
                    burn(entity.getEntity());
                }
            } else {
                burn(entity.getEntity());
            }
        }
        remove();
    }

    @Override
    protected void onBlockHit(@Nonnull BlockHitResult blockRayTrace) {
        super.onBlockHit(blockRayTrace);
        BlockPos hitPos = blockRayTrace.getBlockPos();
        Direction hitSide = blockRayTrace.getSide();
        boolean hitFluid = !world.getFluidState(hitPos).isEmpty();
        if (!world.isClient && MekanismConfig.general.aestheticWorldDamage.get() && !hitFluid) {
            if (mode == FlamethrowerMode.HEAT) {
                smeltBlock(hitPos);
            } else if (mode == FlamethrowerMode.INFERNO) {
                Entity owner = getOwner();
                PlayerEntity shooter = owner instanceof PlayerEntity ? (PlayerEntity) owner : null;
                BlockPos sidePos = hitPos.offset(hitSide);
                BlockState hitState = world.getBlockState(hitPos);
                if (AbstractFireBlock.method_30032(world, sidePos)) {
                    world.setBlockState(sidePos, AbstractFireBlock.getState(world, sidePos));
                } else if (CampfireBlock.method_30035(hitState)) {
                    world.setBlockState(hitPos, hitState.with(Properties.LIT, true));
                } else if (hitState.isFlammable(world, hitPos, hitSide)) {
                    hitState.catchFire(world, hitPos, hitSide, shooter);
                    if (hitState.getBlock() instanceof TntBlock) {
                        world.removeBlock(hitPos, false);
                    }
                }
            }
        }
        if (hitFluid) {
            spawnParticlesAt(getBlockPos());
            playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0F, 1.0F);
        }
        remove();
    }

    private boolean smeltItem(ItemEntity item) {
        Optional<SmeltingRecipe> recipe = world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, new SimpleInventory(item.getStack()), world);
        if (recipe.isPresent()) {
            ItemStack result = recipe.get().getOutput();
            item.setStack(StackUtils.size(result, item.getStack().getCount()));
            item.age = 0;
            spawnParticlesAt(item.getBlockPos());
            playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1.0F, 1.0F);
            return true;
        }
        return false;
    }

    private boolean smeltBlock(BlockPos blockPos) {
        if (world.isAir(blockPos)) {
            return false;
        }
        ItemStack stack = new ItemStack(world.getBlockState(blockPos).getBlock());
        if (stack.isEmpty()) {
            return false;
        }
        Optional<SmeltingRecipe> recipe;
        try {
            recipe = world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, new SimpleInventory(stack), world);
        } catch (Exception e) {
            return false;
        }
        if (recipe.isPresent()) {
            if (!world.isClient) {
                BlockState state = world.getBlockState(blockPos);
                ItemStack result = recipe.get().getOutput();
                if (result.getItem() instanceof BlockItem) {
                    world.setBlockState(blockPos, Block.getBlockFromItem(result.getItem().getItem()).getDefaultState());
                } else {
                    world.removeBlock(blockPos, false);
                    ItemEntity item = new ItemEntity(world, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, result.copy());
                    item.setVelocity(0, 0, 0);
                    world.spawnEntity(item);
                }
                world.syncWorldEvent(WorldEvents.BREAK_BLOCK_EFFECTS, blockPos, Block.getRawIdFromState(state));
            }
            spawnParticlesAt(blockPos.add(0.5, 0.5, 0.5));
            return true;
        }
        return false;
    }

    private void burn(Entity entity) {
        entity.setOnFireFor(20);
        entity.damage(DamageSource.thrownProjectile(this, getOwner()), DAMAGE);
    }

    private void spawnParticlesAt(BlockPos pos) {
        for (int i = 0; i < 10; i++) {
            world.addParticle(ParticleTypes.SMOKE, pos.getX() + (random.nextFloat() - 0.5), pos.getY() + (random.nextFloat() - 0.5),
                  pos.getZ() + (random.nextFloat() - 0.5), 0, 0, 0);
        }
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    public void readCustomDataFromTag(@Nonnull CompoundTag nbtTags) {
        super.readCustomDataFromTag(nbtTags);
        NBTUtils.setEnumIfPresent(nbtTags, NBTConstants.MODE, FlamethrowerMode::byIndexStatic, mode -> this.mode = mode);
    }

    @Override
    public void writeCustomDataToTag(@Nonnull CompoundTag nbtTags) {
        super.writeCustomDataToTag(nbtTags);
        nbtTags.putInt(NBTConstants.MODE, mode.ordinal());
    }

    @Nonnull
    @Override
    public Packet<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(PacketByteBuf dataStream) {
        dataStream.writeEnumConstant(mode);
    }

    @Override
    public void readSpawnData(PacketByteBuf dataStream) {
        mode = dataStream.readEnumConstant(FlamethrowerMode.class);
    }
}