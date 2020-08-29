package mekanism.tools.client.render.item;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import javax.annotation.Nonnull;
import mekanism.api.NBTConstants;
import mekanism.common.Mekanism;
import mekanism.tools.client.ShieldTextures;
import mekanism.tools.common.registries.ToolsItems;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.DyeColor;

public class RenderMekanismShieldItem extends BuiltinModelItemRenderer {

    @Override
    public void render(@Nonnull ItemStack stack, @Nonnull Mode transformType, @Nonnull MatrixStack matrix, @Nonnull VertexConsumerProvider renderer, int light, int overlayLight) {
        Item item = stack.getItem();
        ShieldTextures textures;
        if (item == ToolsItems.BRONZE_SHIELD.getItem()) {
            textures = ShieldTextures.BRONZE;
        } else if (item == ToolsItems.LAPIS_LAZULI_SHIELD.getItem()) {
            textures = ShieldTextures.LAPIS_LAZULI;
        } else if (item == ToolsItems.OSMIUM_SHIELD.getItem()) {
            textures = ShieldTextures.OSMIUM;
        } else if (item == ToolsItems.REFINED_GLOWSTONE_SHIELD.getItem()) {
            textures = ShieldTextures.REFINED_GLOWSTONE;
        } else if (item == ToolsItems.REFINED_OBSIDIAN_SHIELD.getItem()) {
            textures = ShieldTextures.REFINED_OBSIDIAN;
        } else if (item == ToolsItems.STEEL_SHIELD.getItem()) {
            textures = ShieldTextures.STEEL;
        } else {
            Mekanism.logger.warn("Unknown item for mekanism shield renderer: {}", item.getRegistryName());
            return;
        }
        SpriteIdentifier material = textures.getBase();
        matrix.push();
        matrix.scale(1, -1, -1);
        VertexConsumer buffer = material.getSprite().getTextureSpecificVertexConsumer(ItemRenderer.method_29711(renderer, modelShield.getLayer(material.getAtlasId()), false, stack.hasGlint()));
        if (stack.getSubTag(NBTConstants.BLOCK_ENTITY_TAG) != null) {
            modelShield.method_23775().render(matrix, buffer, light, overlayLight, 1, 1, 1, 1);
            List<Pair<BannerPattern, DyeColor>> list = BannerBlockEntity.method_24280(ShieldItem.getColor(stack), BannerBlockEntity.getPatternListTag(stack));
            BannerBlockEntityRenderer.method_29999(matrix, renderer, light, overlayLight, modelShield.method_23774(), material, false, list);
        } else {
            modelShield.render(matrix, buffer, light, overlayLight, 1, 1, 1, 1);
        }
        matrix.pop();
    }
}