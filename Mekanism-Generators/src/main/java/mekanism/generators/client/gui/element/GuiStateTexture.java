package mekanism.generators.client.gui.element;

import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiTexturedElement;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.generators.common.MekanismGenerators;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class GuiStateTexture extends GuiTexturedElement {

    private static final Identifier stateHolder = MekanismGenerators.rl(ResourceType.GUI.getPrefix() + "state_holder.png");

    private final BooleanSupplier onSupplier;
    private final Identifier onTexture;
    private final Identifier offTexture;

    public GuiStateTexture(IGuiWrapper gui, int x, int y, BooleanSupplier onSupplier, Identifier onTexture, Identifier offTexture) {
        super(stateHolder, gui, x, y, 16, 16);
        this.onSupplier = onSupplier;
        this.onTexture = onTexture;
        this.offTexture = offTexture;
    }

    @Override
    public void drawBackground(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        minecraft.textureManager.bindTexture(getResource());
        drawTexture(matrix, x, y, 0, 0, width, height, width, height);
        minecraft.textureManager.bindTexture(onSupplier.getAsBoolean() ? onTexture : offTexture);
        drawTexture(matrix, x + 2, y + 2, 0, 0, width - 4, height - 4, width - 4, height - 4);
    }
}