package mekanism.client.render.transmitter;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.lib.Quad;
import mekanism.client.render.lib.QuadUtils;
import mekanism.client.render.lib.Vertex;
import mekanism.client.render.obj.ContentsModelConfiguration;
import mekanism.client.render.obj.VisibleModelConfiguration;
import mekanism.client.render.tileentity.MekanismTileEntityRenderer;
import mekanism.common.config.MekanismConfig;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.EmptyModelData;

@ParametersAreNonnullByDefault
public abstract class RenderTransmitterBase<TRANSMITTER extends TileEntityTransmitter> extends MekanismTileEntityRenderer<TRANSMITTER> {

    public static final Identifier MODEL_LOCATION = MekanismUtils.getResource(ResourceType.MODEL, "transmitter_contents.obj");
    private static final IModelConfiguration contentsConfiguration = new ContentsModelConfiguration();
    private static final Map<ContentsModelData, List<BakedQuad>> contentModelCache = new Object2ObjectOpenHashMap<>();

    public static void onStitch() {
        contentModelCache.clear();
    }

    private static List<BakedQuad> getBakedQuads(List<String> visible, Sprite icon, World world) {
        return contentModelCache.computeIfAbsent(new ContentsModelData(visible, icon), modelData -> {
            List<BakedQuad> bakedQuads = MekanismRenderer.contentsModel.bake(new VisibleModelConfiguration(contentsConfiguration, modelData.visible),
                  ModelLoader.instance(), material -> modelData.icon, ModelRotation.X0_Y0, ModelOverrideList.EMPTY, MODEL_LOCATION
            ).getQuads(null, null, world.getRandom(), EmptyModelData.INSTANCE);
            //TODO: Try to improve this/do it better. It is close enough for now given it fixes render order issues, but could be improved further
            List<Quad> unpackedQuads = QuadUtils.unpack(bakedQuads);
            for (Quad unpackedQuad : unpackedQuads) {
                for (Vertex vertex : unpackedQuad.getVertices()) {
                    //Adjust the normals so it is closer to as if there were no normals set the same way we do it in Render Resizable Cuboid
                    vertex.normal(vertex.getNormal().add(2.5, 2.5, 2.5).normalize());
                }
            }
            return QuadUtils.bake(unpackedQuads);
        });
    }

    protected RenderTransmitterBase(BlockEntityRenderDispatcher renderer) {
        super(renderer);
    }

    protected void renderModel(TRANSMITTER transmitter, MatrixStack matrix, VertexConsumer builder, int rgb, float alpha, int light, int overlayLight,
          Sprite icon) {
        renderModel(transmitter, matrix, builder, MekanismRenderer.getRed(rgb), MekanismRenderer.getGreen(rgb), MekanismRenderer.getBlue(rgb), alpha, light,
              overlayLight, icon, Arrays.stream(EnumUtils.DIRECTIONS)
                    .map(side -> side.asString() + transmitter.getTransmitter().getConnectionType(side).asString().toUpperCase(Locale.ROOT))
                    .collect(Collectors.toList()));
    }

    protected void renderModel(TRANSMITTER transmitter, MatrixStack matrix, VertexConsumer builder, float red, float green, float blue, float alpha, int light,
          int overlayLight, Sprite icon, List<String> visible) {
        if (!visible.isEmpty()) {
            Entry entry = matrix.peek();
            //Get all the sides
            for (BakedQuad quad : getBakedQuads(visible, icon, transmitter.getWorld())) {
                builder.addVertexData(entry, quad, red, green, blue, alpha, light, overlayLight);
            }
        }
    }

    @Override
    public void render(TRANSMITTER transmitter, float partialTick, MatrixStack matrix, VertexConsumerProvider renderer, int light, int overlayLight) {
        if (!MekanismConfig.client.opaqueTransmitters.get()) {
            super.render(transmitter, partialTick, matrix, renderer, light, overlayLight);
        }
    }

    private static class ContentsModelData {

        private final List<String> visible;
        private final Sprite icon;

        private ContentsModelData(List<String> visible, Sprite icon) {
            this.visible = visible;
            this.icon = icon;
        }

        @Override
        public int hashCode() {
            return Objects.hash(visible, icon);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof ContentsModelData) {
                ContentsModelData other = (ContentsModelData) o;
                return visible.equals(other.visible) && icon.equals(other.icon);
            }
            return false;
        }
    }
}