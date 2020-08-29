package mekanism.client.render.tileentity;

import javax.annotation.ParametersAreNonnullByDefault;
import mekanism.client.model.ModelIndustrialAlarm;
import mekanism.common.base.ProfilerConstants;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.tile.TileEntityIndustrialAlarm;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.profiler.Profiler;

@ParametersAreNonnullByDefault
public class RenderIndustrialAlarm extends MekanismTileEntityRenderer<TileEntityIndustrialAlarm> {

    private static final float ROTATE_SPEED = 10F;
    private final ModelIndustrialAlarm model = new ModelIndustrialAlarm();

    public RenderIndustrialAlarm(BlockEntityRenderDispatcher renderer) {
        super(renderer);
    }

    @Override
    protected void render(TileEntityIndustrialAlarm tile, float partialTick, MatrixStack matrix, VertexConsumerProvider renderer, int light, int overlayLight, Profiler profiler) {
        performTranslations(tile, matrix);
        float rotation = (tile.getWorld().getTime() + partialTick) * ROTATE_SPEED % 360;
        model.render(matrix, renderer, light, overlayLight, Attribute.isActive(tile.getCachedState()), rotation, false, false);
        matrix.pop();
    }

    @Override
    protected String getProfilerSection() {
        return ProfilerConstants.INDUSTRIAL_ALARM;
    }

    @Override
    public boolean isGlobalRenderer(TileEntityIndustrialAlarm tile) {
        return true;
    }

    /**
     * Make sure to call matrix.pop afterwards
     */
    private void performTranslations(TileEntityIndustrialAlarm tile, MatrixStack matrix) {
        matrix.push();
        matrix.translate(0.5, 0, 0.5);
        switch (tile.getDirection()) {
            case DOWN: {
                matrix.translate(0, 1, 0);
                matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180));
                break;
            }
            case NORTH: {
                matrix.translate(0, 0.5, 0.5);
                matrix.multiply(Vector3f.NEGATIVE_X.getDegreesQuaternion(90));
                break;
            }
            case SOUTH: {
                matrix.translate(0, 0.5, -0.5);
                matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90));
                break;
            }
            case EAST: {
                matrix.translate(-0.5, 0.5, 0);
                matrix.multiply(Vector3f.NEGATIVE_Z.getDegreesQuaternion(90));
                break;
            }
            case WEST: {
                matrix.translate(0.5, 0.5, 0);
                matrix.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90));
                break;
            }
            default:
                break;
        }
    }
}
