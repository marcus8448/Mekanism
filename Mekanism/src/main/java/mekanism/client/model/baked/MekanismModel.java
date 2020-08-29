package mekanism.client.model.baked;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nonnull;
import mekanism.client.render.lib.Quad;
import mekanism.client.render.lib.QuadTransformation;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.minecraftforge.client.model.geometry.IMultipartModelGeometry;

public class MekanismModel implements IMultipartModelGeometry<MekanismModel> {

    private final Multimap<String, BlockPartWrapper> elements;

    public MekanismModel(Multimap<String, BlockPartWrapper> list) {
        this.elements = list;
    }

    public static class Loader implements IModelLoader<MekanismModel> {

        public static final Loader INSTANCE = new Loader();

        private Loader() {
        }

        @Override
        public void apply(@Nonnull ResourceManager resourceManager) {
        }

        @Nonnull
        @Override
        public MekanismModel read(@Nonnull JsonDeserializationContext ctx, JsonObject modelContents) {
            Multimap<String, BlockPartWrapper> multimap = HashMultimap.create();
            if (modelContents.has("elements")) {
                for (JsonElement element : JsonHelper.getArray(modelContents, "elements")) {
                    JsonObject obj = element.getAsJsonObject();
                    ModelElement part = ctx.deserialize(element, ModelElement.class);
                    String name = obj.has("name") ? obj.get("name").getAsString() : "undefined";
                    BlockPartWrapper wrapper = new BlockPartWrapper(name, part);
                    multimap.put(name, wrapper);

                    if (obj.has("faces")) {
                        JsonObject faces = obj.get("faces").getAsJsonObject();
                        faces.entrySet().forEach(e -> {
                            Direction side = Direction.byName(e.getKey());
                            if (side == null) {
                                return;
                            }
                            JsonObject face = e.getValue().getAsJsonObject();
                            if (face.has("lightLevel")) {
                                int light = face.get("lightLevel").getAsInt();
                                wrapper.light(side, light);
                            }
                        });
                    }
                }
            }
            return new MekanismModel(multimap);
        }
    }

    public static class BlockPartWrapper implements IModelGeometryPart {

        private final String name;
        private final ModelElement blockPart;

        private final Object2IntMap<ModelElementFace> litFaceMap = new Object2IntOpenHashMap<>();

        public BlockPartWrapper(String name, ModelElement blockPart) {
            this.name = name;
            this.blockPart = blockPart;
        }

        public ModelElement getPart() {
            return blockPart;
        }

        @Override
        public String name() {
            return name;
        }

        public void light(Direction side, int light) {
            litFaceMap.put(blockPart.faces.get(side), light);
        }

        @Override
        public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelLoader bakery, Function<SpriteIdentifier, Sprite> spriteGetter, ModelBakeSettings modelTransform,
              Identifier modelLocation) {
            for (Direction direction : blockPart.faces.keySet()) {
                ModelElementFace face = blockPart.faces.get(direction);
                Sprite sprite = spriteGetter.apply(owner.resolveTexture(face.textureId));
                BakedQuad quad = JsonUnbakedModel.makeBakedQuad(blockPart, face, sprite, direction, modelTransform, modelLocation);
                if (litFaceMap.containsKey(face)) {
                    quad = new Quad(quad).transform(QuadTransformation.light(litFaceMap.getInt(face) / 15F)).bake();
                }
                if (face.cullFace == null) {
                    modelBuilder.addGeneralQuad(quad);
                } else {
                    modelBuilder.addFaceQuad(modelTransform.getRotation().rotateTransform(face.cullFace), quad);
                }
            }
        }

        @Override
        public Collection<SpriteIdentifier> getTextures(IModelConfiguration owner, Function<Identifier, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
            Set<SpriteIdentifier> textures = Sets.newHashSet();
            for (ModelElementFace face : blockPart.faces.values()) {
                SpriteIdentifier texture = owner.resolveTexture(face.textureId);
                if (Objects.equals(texture.getTextureId(), MissingSprite.getMissingSpriteId())) {
                    missingTextureErrors.add(Pair.of(face.textureId, owner.getModelName()));
                }
                textures.add(texture);
            }
            return textures;
        }
    }

    @Override
    public Collection<BlockPartWrapper> getParts() {
        return elements.values();
    }

    @Override
    public Optional<BlockPartWrapper> getPart(String name) {
        return elements.get(name).stream().findFirst();
    }
}
