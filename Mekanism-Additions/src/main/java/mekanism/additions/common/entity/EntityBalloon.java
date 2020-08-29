package mekanism.additions.common.entity;

import java.util.UUID;
import javax.annotation.Nonnull;
import mekanism.additions.common.registries.AdditionsEntityTypes;
import mekanism.additions.common.registries.AdditionsItems;
import mekanism.additions.common.registries.AdditionsSounds;
import mekanism.api.NBTConstants;
import mekanism.api.text.EnumColor;
import mekanism.common.registration.impl.EntityTypeRegistryObject;
import mekanism.common.util.NBTUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityBalloon extends Entity implements IEntityAdditionalSpawnData {

    private static final TrackedData<Byte> IS_LATCHED = DataTracker.registerData(EntityBalloon.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Integer> LATCHED_X = DataTracker.registerData(EntityBalloon.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> LATCHED_Y = DataTracker.registerData(EntityBalloon.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> LATCHED_Z = DataTracker.registerData(EntityBalloon.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> LATCHED_ID = DataTracker.registerData(EntityBalloon.class, TrackedDataHandlerRegistry.INTEGER);
    private static final double OFFSET = -0.275;

    public EnumColor color = EnumColor.DARK_BLUE;
    private BlockPos latched;
    public LivingEntity latchedEntity;
    /* server-only */
    private boolean hasCachedEntity;
    private UUID cachedEntityUUID;

    public EntityBalloon(EntityType<EntityBalloon> type, World world) {
        super(type, world);

        ignoreCameraFrustum = true;
        inanimate = true;
        updatePosition(getX() + 0.5F, getY() + 3F, getZ() + 0.5F);
        setVelocity(getVelocity().getX(), 0.04, getVelocity().getZ());

        dataTracker.startTracking(IS_LATCHED, (byte) 0);
        dataTracker.startTracking(LATCHED_X, 0);
        dataTracker.startTracking(LATCHED_Y, 0);
        dataTracker.startTracking(LATCHED_Z, 0);
        dataTracker.startTracking(LATCHED_ID, -1);
    }

    private EntityBalloon(EntityTypeRegistryObject<EntityBalloon> type, World world) {
        this(type.getEntityType(), world);
    }

    public EntityBalloon(World world, double x, double y, double z, EnumColor c) {
        this(AdditionsEntityTypes.BALLOON, world);
        updatePosition(x + 0.5F, y + 3F, z + 0.5F);

        prevX = getX();
        prevY = getY();
        prevZ = getZ();
        color = c;
    }

    public EntityBalloon(LivingEntity entity, EnumColor c) {
        this(AdditionsEntityTypes.BALLOON, entity.world);
        latchedEntity = entity;
        float height = latchedEntity.getDimensions(latchedEntity.getPose()).height;
        updatePosition(latchedEntity.getX(), latchedEntity.getY() + height + 1.7F, latchedEntity.getZ());

        prevX = getX();
        prevY = getY();
        prevZ = getZ();

        color = c;
        dataTracker.set(IS_LATCHED, (byte) 2);
        dataTracker.set(LATCHED_ID, entity.getEntityId());
    }

    public EntityBalloon(World world, BlockPos pos, EnumColor c) {
        this(AdditionsEntityTypes.BALLOON, world);
        latched = pos;
        updatePosition(latched.getX() + 0.5F, latched.getY() + 1.8F, latched.getZ() + 0.5F);

        prevX = getX();
        prevY = getY();
        prevZ = getZ();

        color = c;
        dataTracker.set(IS_LATCHED, (byte) 1);
        dataTracker.set(LATCHED_X, latched.getX());
        dataTracker.set(LATCHED_Y, latched.getY());
        dataTracker.set(LATCHED_Z, latched.getZ());
    }

    @Override
    public void tick() {
        prevX = getX();
        prevY = getY();
        prevZ = getZ();

        if (getY() >= world.getHeight()) {
            pop();
            return;
        }

        if (world.isClient) {
            if (dataTracker.get(IS_LATCHED) == 1) {
                latched = new BlockPos(dataTracker.get(LATCHED_X), dataTracker.get(LATCHED_Y), dataTracker.get(LATCHED_Z));
            } else {
                latched = null;
            }
            if (dataTracker.get(IS_LATCHED) == 2) {
                latchedEntity = (LivingEntity) world.getEntityById(dataTracker.get(LATCHED_ID));
            } else {
                latchedEntity = null;
            }
        } else {
            if (hasCachedEntity) {
                if (world instanceof ServerWorld) {
                    Entity entity = ((ServerWorld) world).getEntity(cachedEntityUUID);
                    if (entity instanceof LivingEntity) {
                        latchedEntity = (LivingEntity) entity;
                    }
                }
                cachedEntityUUID = null;
                hasCachedEntity = false;
            }
            if (age == 1) {
                byte isLatched;
                if (latched != null) {
                    isLatched = (byte) 1;
                } else if (latchedEntity != null) {
                    isLatched = (byte) 2;
                } else {
                    isLatched = (byte) 0;
                }
                dataTracker.set(IS_LATCHED, isLatched);
                dataTracker.set(LATCHED_X, latched == null ? 0 : latched.getX());
                dataTracker.set(LATCHED_Y, latched == null ? 0 : latched.getY());
                dataTracker.set(LATCHED_Z, latched == null ? 0 : latched.getZ());
                dataTracker.set(LATCHED_ID, latchedEntity == null ? -1 : latchedEntity.getEntityId());
            }
        }

        if (!world.isClient) {
            if (latched != null && world.canSetBlock(latched) && world.isAir(latched)) {
                latched = null;
                dataTracker.set(IS_LATCHED, (byte) 0);
            }
            if (latchedEntity != null && (latchedEntity.getHealth() <= 0 || !latchedEntity.isAlive() || !world.getChunkManager().shouldTickEntity(latchedEntity))) {
                latchedEntity = null;
                dataTracker.set(IS_LATCHED, (byte) 0);
            }
        }

        if (!isLatched()) {
            Vec3d motion = getVelocity();
            setVelocity(motion.getX(), Math.min(motion.getY() * 1.02F, 0.2F), motion.getZ());

            move(MovementType.SELF, getVelocity());

            motion = getVelocity();
            motion = motion.multiply(0.98, 0, 0.98);

            if (onGround) {
                motion = motion.multiply(0.7, 0, 0.7);
            }
            if (motion.getY() == 0) {
                motion = new Vec3d(motion.getX(), 0.04, motion.getZ());
            }
            setVelocity(motion);
        } else if (latched != null) {
            setVelocity(0, 0, 0);
        } else if (latchedEntity != null && latchedEntity.getHealth() > 0) {
            int floor = getFloor(latchedEntity);
            Vec3d motion = latchedEntity.getVelocity();
            if (latchedEntity.getY() - (floor + 1) < -0.1) {
                latchedEntity.setVelocity(motion.getX(), Math.max(0.04, motion.getY() * 1.015), motion.getZ());
            } else if (latchedEntity.getY() - (floor + 1) > 0.1) {
                latchedEntity.setVelocity(motion.getX(), Math.min(-0.04, motion.getY() * 1.015), motion.getZ());
            } else {
                latchedEntity.setVelocity(motion.getX(), 0, motion.getZ());
            }
            updatePosition(latchedEntity.getX(), latchedEntity.getY() + getAddedHeight(), latchedEntity.getZ());
        }
    }

    public double getAddedHeight() {
        return latchedEntity.getDimensions(latchedEntity.getPose()).height + 0.8;
    }

    private int getFloor(LivingEntity entity) {
        BlockPos pos = new BlockPos(entity.getPos());
        for (BlockPos posi = pos; posi.getY() > 0; posi = posi.down()) {
            if (posi.getY() < world.getHeight() && !world.isAir(posi)) {
                return posi.getY() + 1 + (entity instanceof PlayerEntity ? 1 : 0);
            }
        }
        return -1;
    }

    private void pop() {
        playSound(AdditionsSounds.POP.getSoundEvent(), 1, 1);
        if (!world.isClient) {
            DustParticleEffect redstoneParticleData = new DustParticleEffect(color.getColor(0), color.getColor(1), color.getColor(2), 1.0F);
            for (int i = 0; i < 10; i++) {
                ((ServerWorld) world).spawnParticles(redstoneParticleData, getX() + 0.6 * random.nextFloat() - 0.3, getY() + 0.6 * random.nextFloat() - 0.3,
                      getZ() + 0.6 * random.nextFloat() - 0.3, 1, 0, 0, 0, 0);
            }
        }
        remove();
    }

    @Override
    public boolean isPushable() {
        return latched == null;
    }

    @Override
    public boolean collides() {
        return isAlive();
    }

    @Override
    protected boolean canClimb() {
        return false;
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    protected void readCustomDataFromTag(@Nonnull CompoundTag nbtTags) {
        NBTUtils.setEnumIfPresent(nbtTags, NBTConstants.COLOR, EnumColor::byIndexStatic, color -> this.color = color);
        NBTUtils.setBlockPosIfPresent(nbtTags, NBTConstants.LATCHED, pos -> latched = pos);
        NBTUtils.setUUIDIfPresent(nbtTags, NBTConstants.OWNER_UUID, uuid -> {
            hasCachedEntity = true;
            cachedEntityUUID = uuid;
        });
    }

    @Override
    protected void writeCustomDataToTag(@Nonnull CompoundTag nbtTags) {
        nbtTags.putInt(NBTConstants.COLOR, color.ordinal());
        if (latched != null) {
            nbtTags.put(NBTConstants.LATCHED, NbtHelper.fromBlockPos(latched));
        }
        if (latchedEntity != null) {
            nbtTags.putUuid(NBTConstants.OWNER_UUID, latchedEntity.getUuid());
        }
    }

    @Override
    public boolean handleAttack(@Nonnull Entity entity) {
        pop();
        return true;
    }

    @Nonnull
    @Override
    public Packet<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(PacketByteBuf data) {
        data.writeDouble(getX());
        data.writeDouble(getY());
        data.writeDouble(getZ());

        data.writeEnumConstant(color);
        if (latched != null) {
            data.writeByte((byte) 1);
            data.writeBlockPos(latched);
        } else if (latchedEntity != null) {
            data.writeByte((byte) 2);
            data.writeVarInt(latchedEntity.getEntityId());
        } else {
            data.writeByte((byte) 0);
        }
    }

    @Override
    public void readSpawnData(PacketByteBuf data) {
        updatePosition(data.readDouble(), data.readDouble(), data.readDouble());
        color = data.readEnumConstant(EnumColor.class);
        byte type = data.readByte();
        if (type == 1) {
            latched = data.readBlockPos();
        } else if (type == 2) {
            latchedEntity = (LivingEntity) world.getEntityById(data.readVarInt());
        } else {
            latched = null;
        }
    }

    @Override
    public void remove() {
        super.remove();
        if (latchedEntity != null) {
            latchedEntity.velocityDirty = false;
        }
    }

    @Override
    public boolean shouldRender(double dist) {
        return dist <= 64;
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    @Override
    public boolean damage(@Nonnull DamageSource dmgSource, float damage) {
        if (isInvulnerableTo(dmgSource)) {
            return false;
        }
        scheduleVelocityUpdate();
        if (dmgSource != DamageSource.MAGIC && dmgSource != DamageSource.DROWN && dmgSource != DamageSource.FALL) {
            pop();
            return true;
        }
        return false;
    }

    public boolean isLatched() {
        if (world.isClient) {
            return dataTracker.get(IS_LATCHED) > 0;
        }
        return latched != null || latchedEntity != null;
    }

    public boolean isLatchedToEntity() {
        return dataTracker.get(IS_LATCHED) == 2 && latchedEntity != null;
    }

    //Adjust various bounding boxes/eye height so that the balloon gets interacted with properly
    @Override
    protected float getEyeHeight(@Nonnull EntityPose pose, @Nonnull EntityDimensions size) {
        return (float) (size.height - OFFSET);
    }

    @Nonnull
    @Override
    protected Box calculateBoundsForPose(@Nonnull EntityPose pose) {
        return getBoundingBox(getDimensions(pose), getX(), getY(), getZ());
    }

    @Override
    public void updatePosition(double x, double y, double z) {
        setPos(x, y, z);
        if (isAddedToWorld() && !this.world.isClient && world instanceof ServerWorld) {
            ((ServerWorld) this.world).checkChunk(this); // Forge - Process chunk registration after moving.
        }
        setBoundingBox(getBoundingBox(getDimensions(EntityPose.STANDING), x, y, z));
    }

    private Box getBoundingBox(EntityDimensions size, double x, double y, double z) {
        float f = size.width / 2F;
        double posY = y - OFFSET;
        return new Box(new Vec3d(x - f, posY, z - f), new Vec3d(x + f, posY + size.height, z + f));
    }

    @Override
    public void calculateDimensions() {
        //NO-OP don't allow size to change
    }

    @Override
    public void moveToBoundingBoxCenter() {
        Box axisalignedbb = getBoundingBox();
        //Offset the y value upwards to match where it actually should be relative to the bounding box
        setPos((axisalignedbb.minX + axisalignedbb.maxX) / 2D, axisalignedbb.minY + OFFSET, (axisalignedbb.minZ + axisalignedbb.maxZ) / 2D);
        if (isAddedToWorld() && !this.world.isClient && world instanceof ServerWorld) {
            ((ServerWorld) this.world).checkChunk(this); // Forge - Process chunk registration after moving.
        }
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        switch (color) {
            case BLACK:
                return AdditionsItems.BLACK_BALLOON.getItemStack();
            case DARK_BLUE:
                return AdditionsItems.BLUE_BALLOON.getItemStack();
            case DARK_GREEN:
                return AdditionsItems.GREEN_BALLOON.getItemStack();
            case DARK_AQUA:
                return AdditionsItems.CYAN_BALLOON.getItemStack();
            case PURPLE:
                return AdditionsItems.PURPLE_BALLOON.getItemStack();
            case ORANGE:
                return AdditionsItems.ORANGE_BALLOON.getItemStack();
            case GRAY:
                return AdditionsItems.LIGHT_GRAY_BALLOON.getItemStack();
            case DARK_GRAY:
                return AdditionsItems.GRAY_BALLOON.getItemStack();
            case INDIGO:
                return AdditionsItems.LIGHT_BLUE_BALLOON.getItemStack();
            case BRIGHT_GREEN:
                return AdditionsItems.LIME_BALLOON.getItemStack();
            case RED:
                return AdditionsItems.RED_BALLOON.getItemStack();
            case PINK:
                return AdditionsItems.MAGENTA_BALLOON.getItemStack();
            case YELLOW:
                return AdditionsItems.YELLOW_BALLOON.getItemStack();
            case WHITE:
                return AdditionsItems.WHITE_BALLOON.getItemStack();
            case BROWN:
                return AdditionsItems.BROWN_BALLOON.getItemStack();
            case BRIGHT_PINK:
                return AdditionsItems.PINK_BALLOON.getItemStack();
        }
        return super.getPickedResult(target);
    }
}