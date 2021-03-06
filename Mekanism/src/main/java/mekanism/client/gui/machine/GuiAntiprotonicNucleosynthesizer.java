package mekanism.client.gui.machine;

import java.util.Arrays;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import mekanism.client.gui.GuiConfigurableTile;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.bar.GuiBar.IBarInfoHandler;
import mekanism.client.gui.element.bar.GuiDynamicHorizontalRateBar;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiEnergyGauge;
import mekanism.client.gui.element.gauge.GuiGasGauge;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.client.gui.element.tab.GuiRedstoneControlTab;
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.client.gui.element.tab.GuiUpgradeTab;
import mekanism.client.render.MekanismRenderType;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.lib.effect.BoltRenderer;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.lib.Color;
import mekanism.common.lib.Color.ColorFunction;
import mekanism.common.lib.effect.BoltEffect;
import mekanism.common.lib.effect.BoltEffect.BoltRenderInfo;
import mekanism.common.lib.effect.BoltEffect.FadeFunction;
import mekanism.common.lib.effect.BoltEffect.SpawnFunction;
import mekanism.common.tile.machine.TileEntityAntiprotonicNucleosynthesizer;
import mekanism.common.util.text.EnergyDisplay;
import mekanism.common.util.text.TextUtils;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class GuiAntiprotonicNucleosynthesizer extends GuiConfigurableTile<TileEntityAntiprotonicNucleosynthesizer, MekanismTileContainer<TileEntityAntiprotonicNucleosynthesizer>> {

    private static final Vec3d from = new Vec3d(47, 50, 0), to = new Vec3d(147, 50, 0);
    private static final BoltRenderInfo boltRenderInfo = new BoltRenderInfo().color(Color.rgbad(0.45F, 0.45F, 0.5F, 1));

    private final BoltRenderer bolt = new BoltRenderer();
    private final Supplier<BoltEffect> boltSupplier = () -> new BoltEffect(boltRenderInfo, from, to, 15)
          .count((int) Math.min(Math.ceil(tile.getProcessRate() / 8F), 20))
          .size(1)
          .lifespan(1)
          .spawn(SpawnFunction.CONSECUTIVE)
          .fade(FadeFunction.NONE);

    public GuiAntiprotonicNucleosynthesizer(MekanismTileContainer<TileEntityAntiprotonicNucleosynthesizer> container, PlayerInventory inv, Text title) {
        super(container, inv, title);
        dynamicSlots = true;
        backgroundHeight += 27;
        backgroundWidth += 20;
    }

    @Override
    public void init() {
        super.init();

        addButton(new GuiInnerScreen(this, 45, 18, 104, 68).jeiCategory(tile));
        addButton(new GuiSecurityTab<>(this, tile));
        addButton(new GuiRedstoneControlTab(this, tile));
        addButton(new GuiUpgradeTab(this, tile));
        addButton(new GuiEnergyTab(() -> Arrays.asList(MekanismLang.USING.translate(EnergyDisplay.of(tile.clientEnergyUsed)),
              MekanismLang.NEEDED.translate(EnergyDisplay.of(tile.getEnergyContainer().getNeeded()))), this));
        addButton(new GuiGasGauge(() -> tile.gasTank, () -> tile.getGasTanks(null), GaugeType.SMALL_MED, this, 5, 18));
        addButton(new GuiEnergyGauge(tile.getEnergyContainer(), GaugeType.SMALL_MED, this, 172, 18));
        addButton(new GuiDynamicHorizontalRateBar(this, new IBarInfoHandler() {
            @Override
            public Text getTooltip() {
                return MekanismLang.PROGRESS.translate(TextUtils.getPercent(tile.getScaledProgress()));
            }

            @Override
            public double getLevel() {
                return tile.getScaledProgress();
            }
        }, 5, 88, backgroundWidth - 12, ColorFunction.scale(Color.rgbi(60, 45, 74), Color.rgbi(100, 30, 170))));
    }

    @Override
    protected void drawForegroundText(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        drawString(matrix, tile.getName(), (getXSize() / 2) - (getStringWidth(tile.getName()) / 2), 6, titleTextColor());
        drawString(matrix, MekanismLang.INVENTORY.translate(), 8, (getYSize() - 96) + 3, titleTextColor());
        drawTextScaledBound(matrix, MekanismLang.PROCESS_RATE.translate(TextUtils.getPercent(tile.getProcessRate())), 48, 76, screenTextColor(), 100);
        super.drawForegroundText(matrix, mouseX, mouseY);
        matrix.push();
        matrix.translate(0, 0, 100);
        VertexConsumerProvider.Immediate renderer = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        bolt.update(this, boltSupplier.get(), MekanismRenderer.getPartialTick());
        bolt.render(MekanismRenderer.getPartialTick(), matrix, renderer);
        renderer.draw(MekanismRenderType.MEK_LIGHTNING);
        matrix.pop();
    }
}