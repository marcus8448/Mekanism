package mekanism.tools.client;

import mekanism.api.providers.IItemProvider;
import mekanism.client.ClientRegistrationUtil;
import mekanism.tools.common.MekanismTools;
import mekanism.tools.common.registries.ToolsItems;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = MekanismTools.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ToolsClientRegistration {

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        addShieldPropertyOverrides(MekanismTools.rl("blocking"),
              (stack, world, entity) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F,
              ToolsItems.BRONZE_SHIELD, ToolsItems.LAPIS_LAZULI_SHIELD, ToolsItems.OSMIUM_SHIELD, ToolsItems.REFINED_GLOWSTONE_SHIELD,
              ToolsItems.REFINED_OBSIDIAN_SHIELD, ToolsItems.STEEL_SHIELD);
    }

    private static void addShieldPropertyOverrides(Identifier override, ModelPredicateProvider propertyGetter, IItemProvider... shields) {
        for (IItemProvider shield : shields) {
            ClientRegistrationUtil.setPropertyOverride(shield, override, propertyGetter);
        }
    }

    @SubscribeEvent
    public static void onStitch(TextureStitchEvent.Pre event) {
        if (event.getMap().getId().equals(SpriteAtlasTexture.BLOCK_ATLAS_TEX)) {
            for (ShieldTextures textures : ShieldTextures.values()) {
                event.addSprite(textures.getBase().getTextureId());
            }
        }
    }
}