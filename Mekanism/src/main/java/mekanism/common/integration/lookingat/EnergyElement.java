package mekanism.common.integration.lookingat;

import mekanism.api.math.FloatingLong;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.util.text.EnergyDisplay;
import net.minecraft.client.texture.Sprite;
import net.minecraft.text.Text;

public class EnergyElement extends LookingAtElement {

    protected final FloatingLong energy;
    protected final FloatingLong maxEnergy;

    public EnergyElement(FloatingLong energy, FloatingLong maxEnergy) {
        super(0xFF000000, 0xFFFFFF);
        this.energy = energy;
        this.maxEnergy = maxEnergy;
    }

    @Override
    public int getScaledLevel(int level) {
        if (energy.equals(FloatingLong.MAX_VALUE)) {
            return level;
        }
        return (int) (level * energy.divideToLevel(maxEnergy));
    }

    @Override
    public Sprite getIcon() {
        return MekanismRenderer.energyIcon;
    }

    @Override
    public Text getText() {
        return EnergyDisplay.of(energy, maxEnergy).getTextComponent();
    }
}