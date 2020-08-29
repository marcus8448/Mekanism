package mekanism.common.registries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.api.text.IHasTranslationKey;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Vec3d;

//Note: This isn't an actual registry but should make things a bit cleaner
@MethodsReturnNonnullByDefault
public class MekanismDamageSource extends DamageSource implements IHasTranslationKey {

    public static final MekanismDamageSource LASER = new MekanismDamageSource("laser");
    public static final MekanismDamageSource RADIATION = new MekanismDamageSource("radiation").setBypassesArmor();

    private final String translationKey;

    private final Vec3d damageLocation;


    public MekanismDamageSource(String damageType) {
        this(damageType, null);
    }

    private MekanismDamageSource(@Nonnull String damageType, @Nullable Vec3d damageLocation) {
        super(damageType);
        this.translationKey = "death.attack." + getName();
        this.damageLocation = damageLocation;
    }

    /**
     * Gets a new instance of this damage source, that is positioned at the given location.
     */
    public MekanismDamageSource fromPosition(@Nonnull Vec3d damageLocation) {
        return new MekanismDamageSource(getName(), damageLocation);
    }

    @Override
    public String getTranslationKey() {
        return translationKey;
    }

    @Nullable
    @Override
    public Vec3d getPosition() {
        return damageLocation;
    }

    @Override
    public MekanismDamageSource setProjectile() {
        super.setProjectile();
        return this;
    }

    @Override
    public MekanismDamageSource setExplosive() {
        super.setExplosive();
        return this;
    }

    @Override
    public MekanismDamageSource setBypassesArmor() {
        super.setBypassesArmor();
        return this;
    }

    @Override
    public MekanismDamageSource setOutOfWorld() {
        super.setOutOfWorld();
        return this;
    }

    @Override
    public MekanismDamageSource setUnblockable() {
        super.setUnblockable();
        return this;
    }

    @Override
    public MekanismDamageSource setFire() {
        super.setFire();
        return this;
    }

    @Override
    public MekanismDamageSource setScaledWithDifficulty() {
        super.setScaledWithDifficulty();
        return this;
    }

    @Override
    public MekanismDamageSource setUsesMagic() {
        super.setUsesMagic();
        return this;
    }
}