package mekanism.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import mekanism.api.text.ILangEntry;
import mekanism.common.MekanismLang;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.HUDElement;
import mekanism.common.content.gear.HUDElement.HUDColor;
import mekanism.common.item.gear.ItemMekaSuitArmor;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class HUDRenderer {

    private static final Identifier HEAD_ICON = MekanismUtils.getResource(ResourceType.GUI_HUD, "hud_mekasuit_helmet.png");
    private static final Identifier CHEST_ICON = MekanismUtils.getResource(ResourceType.GUI_HUD, "hud_mekasuit_chest.png");
    private static final Identifier LEGS_ICON = MekanismUtils.getResource(ResourceType.GUI_HUD, "hud_mekasuit_leggings.png");
    private static final Identifier BOOTS_ICON = MekanismUtils.getResource(ResourceType.GUI_HUD, "hud_mekasuit_boots.png");

    private static final Identifier COMPASS = MekanismUtils.getResource(ResourceType.GUI, "compass.png");

    private long lastTick = -1;

    private float prevRotationYaw;
    private float prevRotationPitch;

    private final MinecraftClient minecraft = MinecraftClient.getInstance();

    public void renderHUD(MatrixStack matrix, float partialTick) {
        update();
        int color = HUDColor.REGULAR.getColor();
        if (MekanismConfig.client.hudOpacity.get() < 0.05F) {
            return;
        }
        matrix.push();
        float yawJitter = -absSqrt(minecraft.player.headYaw - prevRotationYaw);
        float pitchJitter = -absSqrt(minecraft.player.pitch - prevRotationPitch);
        matrix.translate(yawJitter, pitchJitter, 0);
        if (MekanismConfig.client.hudCompassEnabled.get()) {
            renderCompass(matrix, partialTick, color);
        }

        renderMekaSuitEnergyIcons(matrix, partialTick, color);
        renderMekaSuitModuleIcons(matrix, partialTick, color);

        matrix.pop();
    }

    private void update() {
        // if we're just now rendering the HUD after a pause, reset the pitch/yaw trackers
        if (lastTick == -1 || minecraft.world.getTime() - lastTick > 1) {
            prevRotationYaw = minecraft.player.yaw;
            prevRotationPitch = minecraft.player.pitch;
        }
        lastTick = minecraft.world.getTime();
        float yawDiff = (minecraft.player.headYaw - prevRotationYaw);
        float pitchDiff = (minecraft.player.pitch - prevRotationPitch);
        prevRotationYaw += yawDiff / MekanismConfig.client.hudJitter.get();
        prevRotationPitch += pitchDiff / MekanismConfig.client.hudJitter.get();
    }

    private static float absSqrt(float val) {
        float ret = (float) Math.sqrt(Math.abs(val));
        return val < 0 ? -ret : ret;
    }

    private void renderMekaSuitEnergyIcons(MatrixStack matrix, float partialTick, int color) {
        matrix.push();
        matrix.translate(10, 10, 0);
        int posX = 0;
        if (getStack(EquipmentSlot.HEAD).getItem() instanceof ItemMekaSuitArmor) {
            renderHUDElement(matrix, posX, 0, HUDElement.energyPercent(HEAD_ICON, getStack(EquipmentSlot.HEAD)), color, false);
            posX += 48;
        }
        if (getStack(EquipmentSlot.CHEST).getItem() instanceof ItemMekaSuitArmor) {
            renderHUDElement(matrix, posX, 0, HUDElement.energyPercent(CHEST_ICON, getStack(EquipmentSlot.CHEST)), color, false);
            posX += 48;
        }
        if (getStack(EquipmentSlot.LEGS).getItem() instanceof ItemMekaSuitArmor) {
            renderHUDElement(matrix, posX, 0, HUDElement.energyPercent(LEGS_ICON, getStack(EquipmentSlot.LEGS)), color, false);
            posX += 48;
        }
        if (getStack(EquipmentSlot.FEET).getItem() instanceof ItemMekaSuitArmor) {
            renderHUDElement(matrix, posX, 0, HUDElement.energyPercent(BOOTS_ICON, getStack(EquipmentSlot.FEET)), color, false);
        }
        matrix.pop();
    }

    private void renderMekaSuitModuleIcons(MatrixStack matrix, float partialTick, int color) {
        // create list of all elements to render
        List<HUDElement> elements = new ArrayList<>();
        for (EquipmentSlot type : EnumUtils.ARMOR_SLOTS) {
            ItemStack stack = getStack(type);
            if (stack.getItem() instanceof ItemMekaSuitArmor) {
                elements.addAll(((ItemMekaSuitArmor) stack.getItem()).getHUDElements(stack));
            }
        }

        int startX = minecraft.getWindow().getScaledWidth() - 10;
        int curY = minecraft.getWindow().getScaledHeight() - 10;

        matrix.push();
        for (HUDElement element : elements) {
            int elementWidth = 24 + minecraft.textRenderer.getWidth(element.getText());
            curY -= 18;
            renderHUDElement(matrix, startX - elementWidth, curY, element, color, true);
        }
        matrix.pop();
    }

    private void renderHUDElement(MatrixStack matrix, int x, int y, HUDElement element, int color, boolean iconRight) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        MekanismRenderer.color(color);
        minecraft.getTextureManager().bindTexture(element.getIcon());
        if (!iconRight) {
            DrawableHelper.drawTexture(matrix, x, y, 0, 0, 16, 16, 16, 16);
            MekanismRenderer.resetColor();
            minecraft.textRenderer.draw(matrix, element.getText(), x + 18, y + 5, element.getColor());
        } else {
            DrawableHelper.drawTexture(matrix, x + minecraft.textRenderer.getWidth(element.getText()) + 2, y, 0, 0, 16, 16, 16, 16);
            MekanismRenderer.resetColor();
            minecraft.textRenderer.draw(matrix, element.getText(), x, y + 5, element.getColor());
        }
    }

    private void renderCompass(MatrixStack matrix, float partialTick, int color) {
        matrix.push();
        int posX = 25;
        int posY = minecraft.getWindow().getScaledHeight() - 100;
        matrix.translate(posX + 50, posY + 50, 0);
        matrix.push();
        float angle = 180 - MathHelper.lerp(partialTick, minecraft.player.prevHeadYaw, minecraft.player.headYaw);
        matrix.push();
        matrix.scale(0.7F, 0.7F, 0.7F);
        Text coords = MekanismLang.GENERIC_BLOCK_POS.translate((int) minecraft.player.getX(), (int) minecraft.player.getY(), (int) minecraft.player.getZ());
        minecraft.textRenderer.draw(matrix, coords, -minecraft.textRenderer.getWidth(coords) / 2F, -4, color);
        matrix.pop();
        matrix.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-60));
        matrix.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(angle));
        minecraft.getTextureManager().bindTexture(COMPASS);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        MekanismRenderer.color(color);
        DrawableHelper.drawTexture(matrix, -50, -50, 100, 100, 0, 0, 256, 256, 256, 256);
        rotateStr(matrix, MekanismLang.NORTH_SHORT, angle, 0, color);
        rotateStr(matrix, MekanismLang.EAST_SHORT, angle, 90, color);
        rotateStr(matrix, MekanismLang.SOUTH_SHORT, angle, 180, color);
        rotateStr(matrix, MekanismLang.WEST_SHORT, angle, 270, color);
        MekanismRenderer.resetColor();
        matrix.pop();
        matrix.pop();
    }

    private void rotateStr(MatrixStack matrix, ILangEntry langEntry, float rotation, float shift, int color) {
        matrix.push();
        matrix.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(shift));
        matrix.translate(0, -50, 0);
        matrix.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(-rotation - shift));
        minecraft.textRenderer.draw(matrix, langEntry.translate(), -2.5F, -4, color);
        matrix.pop();
    }

    private ItemStack getStack(EquipmentSlot type) {
        return minecraft.player.getEquippedStack(type);
    }
}
