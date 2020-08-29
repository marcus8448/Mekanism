package mekanism.common.lib.effect;

import java.util.Random;
import mekanism.common.lib.Color;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class CustomEffect {

    private final int GRID_SIZE;
    private final Identifier texture;

    protected final Random rand = new Random();

    protected Vec3d pos = new Vec3d(0, 0, 0);
    protected Color color = Color.rgbai(255, 255, 255, 255);
    protected float scale = 1F;

    protected int ticker;

    public CustomEffect(Identifier texture) {
        this(texture, 4);
    }

    public CustomEffect(Identifier texture, int gridSize) {
        this.texture = texture;
        this.GRID_SIZE = gridSize;
    }

    protected Vec3d randVec() {
        return new Vec3d(rand.nextDouble() - 0.5, rand.nextDouble() - 0.5, rand.nextDouble() - 0.5).normalize();
    }

    public boolean tick() {
        ticker++;
        return false;
    }

    public void setPos(Vec3d pos) {
        this.pos = pos;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public Vec3d getPos(float partialTick) {
        return pos;
    }

    public float getScale() {
        return scale;
    }

    public Identifier getTexture() {
        return texture;
    }

    public int getTextureGridSize() {
        return GRID_SIZE;
    }
}
