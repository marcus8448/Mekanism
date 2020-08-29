package mekanism.generators.client.gui;

import java.util.Arrays;
import javax.annotation.Nonnull;
import mekanism.client.gui.element.gauge.GaugeType;
import mekanism.client.gui.element.gauge.GuiEnergyGauge;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.client.gui.element.gauge.GuiGasGauge;
import mekanism.client.gui.element.gauge.GuiNumberGauge;
import mekanism.client.gui.element.gauge.GuiNumberGauge.INumberInfoHandler;
import mekanism.client.gui.element.progress.GuiProgress;
import mekanism.client.gui.element.progress.ProgressType;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.FluidType;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.EmptyTileContainer;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.UnitDisplayUtils.TemperatureUnit;
import mekanism.common.util.text.EnergyDisplay;
import mekanism.generators.client.gui.element.GuiFusionReactorTab;
import mekanism.generators.client.gui.element.GuiFusionReactorTab.FusionReactorTab;
import mekanism.generators.common.GeneratorsLang;
import mekanism.generators.common.tile.fusion.TileEntityFusionReactorController;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.text.Text;

public class GuiFusionReactorHeat extends GuiFusionReactorInfo {

    private static final double MAX_LEVEL = 500_000_000;

    public GuiFusionReactorHeat(EmptyTileContainer<TileEntityFusionReactorController> container, PlayerInventory inv, Text title) {
        super(container, inv, title);
    }

    @Override
    public void init() {
        super.init();
        addButton(new GuiEnergyTab(() -> Arrays.asList(MekanismLang.STORING
                    .translate(EnergyDisplay.of(tile.getMultiblock().energyContainer.getEnergy(), tile.getMultiblock().energyContainer.getMaxEnergy())),
              GeneratorsLang.PRODUCING_AMOUNT.translate(EnergyDisplay.of(tile.getMultiblock().getPassiveGeneration(false, true)))),
              this));
        addButton(new GuiNumberGauge(new INumberInfoHandler() {
            @Override
            public Sprite getIcon() {
                return MekanismRenderer.getBaseFluidTexture(Fluids.LAVA, FluidType.STILL);
            }

            @Override
            public double getLevel() {
                return tile.getMultiblock().getLastPlasmaTemp();
            }

            @Override
            public double getScaledLevel() {
                return Math.min(1, getLevel() / MAX_LEVEL);
            }

            @Override
            public Text getText() {
                return GeneratorsLang.REACTOR_PLASMA.translate(MekanismUtils.getTemperatureDisplay(getLevel(), TemperatureUnit.KELVIN, true));
            }
        }, GaugeType.STANDARD, this, 7, 50));
        addButton(new GuiProgress(() -> tile.getMultiblock().getLastPlasmaTemp() > tile.getMultiblock().getLastCaseTemp() ? 1 : 0, ProgressType.SMALL_RIGHT, this, 29, 76));
        addButton(new GuiNumberGauge(new INumberInfoHandler() {
            @Override
            public Sprite getIcon() {
                return MekanismRenderer.getBaseFluidTexture(Fluids.LAVA, FluidType.STILL);
            }

            @Override
            public double getLevel() {
                return tile.getMultiblock().getLastCaseTemp();
            }

            @Override
            public double getScaledLevel() {
                return Math.min(1, getLevel() / MAX_LEVEL);
            }

            @Override
            public Text getText() {
                return GeneratorsLang.REACTOR_CASE.translate(MekanismUtils.getTemperatureDisplay(getLevel(), TemperatureUnit.KELVIN, true));
            }
        }, GaugeType.STANDARD, this, 61, 50));
        addButton(new GuiProgress(() -> tile.getMultiblock().getCaseTemp() > 0 ? 1 : 0, ProgressType.SMALL_RIGHT, this, 83, 61));
        addButton(new GuiProgress(() -> (tile.getMultiblock().getCaseTemp() > 0 && !tile.getMultiblock().waterTank.isEmpty() &&
                                         tile.getMultiblock().steamTank.getStored() < tile.getMultiblock().steamTank.getCapacity()) ? 1 : 0,
              ProgressType.SMALL_RIGHT, this, 83, 91));
        addButton(new GuiFluidGauge(() -> tile.getMultiblock().waterTank, () -> tile.getFluidTanks(null), GaugeType.SMALL, this, 115, 84));
        addButton(new GuiGasGauge(() -> tile.getMultiblock().steamTank, () -> tile.getGasTanks(null), GaugeType.SMALL, this, 151, 84));
        addButton(new GuiEnergyGauge(tile.getMultiblock().energyContainer, GaugeType.SMALL, this, 115, 46));
        addButton(new GuiFusionReactorTab(this, tile, FusionReactorTab.FUEL));
        addButton(new GuiFusionReactorTab(this, tile, FusionReactorTab.STAT));
    }

    @Override
    protected void drawForegroundText(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        drawTitleText(matrix, GeneratorsLang.FUSION_REACTOR.translate(), 5);
        super.drawForegroundText(matrix, mouseX, mouseY);
    }
}