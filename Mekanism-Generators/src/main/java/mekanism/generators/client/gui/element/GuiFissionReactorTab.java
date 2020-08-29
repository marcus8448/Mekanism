package mekanism.generators.client.gui.element;

import mekanism.api.text.ILangEntry;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.tab.GuiTabElementType;
import mekanism.client.gui.element.tab.TabType;
import mekanism.common.MekanismLang;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.generators.client.gui.element.GuiFissionReactorTab.FissionReactorTab;
import mekanism.generators.common.GeneratorsLang;
import mekanism.generators.common.MekanismGenerators;
import mekanism.generators.common.network.PacketGeneratorsGuiButtonPress;
import mekanism.generators.common.network.PacketGeneratorsGuiButtonPress.ClickedGeneratorsTileButton;
import mekanism.generators.common.tile.fission.TileEntityFissionReactorCasing;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GuiFissionReactorTab extends GuiTabElementType<TileEntityFissionReactorCasing, FissionReactorTab> {

    public GuiFissionReactorTab(IGuiWrapper gui, TileEntityFissionReactorCasing tile, FissionReactorTab type) {
        super(gui, tile, type);
    }

    public enum FissionReactorTab implements TabType<TileEntityFissionReactorCasing> {
        MAIN("radioactive.png", MekanismLang.MAIN_TAB, ClickedGeneratorsTileButton.TAB_MAIN),
        STAT("stats.png", GeneratorsLang.STATS_TAB, ClickedGeneratorsTileButton.TAB_STATS);

        private final ClickedGeneratorsTileButton button;
        private final ILangEntry description;
        private final String path;

        FissionReactorTab(String path, ILangEntry description, ClickedGeneratorsTileButton button) {
            this.path = path;
            this.description = description;
            this.button = button;
        }

        @Override
        public Identifier getResource() {
            return MekanismUtils.getResource(ResourceType.GUI, path);
        }

        @Override
        public void onClick(TileEntityFissionReactorCasing tile) {
            MekanismGenerators.packetHandler.sendToServer(new PacketGeneratorsGuiButtonPress(button, tile.getPos()));
        }

        @Override
        public Text getDescription() {
            return description.translate();
        }

        @Override
        public int getYPos() {
            return 6;
        }
    }
}