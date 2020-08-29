package mekanism.client.render.obj;

import javax.annotation.Nonnull;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.util.math.AffineTransformation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class TransmitterModelTransform implements ModelBakeSettings {

    private final boolean isUvLock;
    private final AffineTransformation matrix;

    public TransmitterModelTransform(ModelBakeSettings internal, Direction dir, float angle) {
        AffineTransformation matrix = new AffineTransformation(null, new Quaternion(vecForDirection(dir), angle, true), null, null);
        this.matrix = internal.getRotation().compose(matrix);
        this.isUvLock = internal.isShaded();
    }

    private static Vector3f vecForDirection(Direction dir) {
        Vector3f vec = new Vector3f(Vec3d.of(dir.getVector()));
        vec.scale(-1);
        return vec;
    }

    @Nonnull
    @Override
    public AffineTransformation getRotation() {
        return matrix;
    }

    @Override
    public boolean isShaded() {
        return isUvLock;
    }
}