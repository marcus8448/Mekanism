package mekanism.client.render.item;

import java.util.concurrent.Callable;
import mekanism.client.render.item.block.RenderChemicalDissolutionChamberItem;
import mekanism.client.render.item.block.RenderEnergyCubeItem;
import mekanism.client.render.item.block.RenderFluidTankItem;
import mekanism.client.render.item.block.RenderIndustrialAlarmItem;
import mekanism.client.render.item.block.RenderQuantumEntangloporterItem;
import mekanism.client.render.item.block.RenderSeismicVibratorItem;
import mekanism.client.render.item.block.RenderSolarNeutronActivatorItem;
import mekanism.client.render.item.gear.RenderArmoredJetpack;
import mekanism.client.render.item.gear.RenderAtomicDisassembler;
import mekanism.client.render.item.gear.RenderFlameThrower;
import mekanism.client.render.item.gear.RenderFreeRunners;
import mekanism.client.render.item.gear.RenderJetpack;
import mekanism.client.render.item.gear.RenderScubaMask;
import mekanism.client.render.item.gear.RenderScubaTank;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;

//This class is used to prevent class loading issues on the server without having to use OnlyIn hacks
public class ISTERProvider {

    public static Callable<BuiltinModelItemRenderer> energyCube() {
        return RenderEnergyCubeItem::new;
    }

    public static Callable<BuiltinModelItemRenderer> dissolution() {
        return RenderChemicalDissolutionChamberItem::new;
    }

    public static Callable<BuiltinModelItemRenderer> fluidTank() {
        return RenderFluidTankItem::new;
    }

    public static Callable<BuiltinModelItemRenderer> industrialAlarm() {
        return RenderIndustrialAlarmItem::new;
    }

    public static Callable<BuiltinModelItemRenderer> entangloporter() {
        return RenderQuantumEntangloporterItem::new;
    }

    public static Callable<BuiltinModelItemRenderer> seismicVibrator() {
        return RenderSeismicVibratorItem::new;
    }

    public static Callable<BuiltinModelItemRenderer> activator() {
        return RenderSolarNeutronActivatorItem::new;
    }

    public static Callable<BuiltinModelItemRenderer> armoredJetpack() {
        return RenderArmoredJetpack::new;
    }

    public static Callable<BuiltinModelItemRenderer> disassembler() {
        return RenderAtomicDisassembler::new;
    }

    public static Callable<BuiltinModelItemRenderer> flamethrower() {
        return RenderFlameThrower::new;
    }

    public static Callable<BuiltinModelItemRenderer> freeRunners() {
        return RenderFreeRunners::new;
    }

    public static Callable<BuiltinModelItemRenderer> scubaMask() {
        return RenderScubaMask::new;
    }

    public static Callable<BuiltinModelItemRenderer> jetpack() {
        return RenderJetpack::new;
    }

    public static Callable<BuiltinModelItemRenderer> scubaTank() {
        return RenderScubaTank::new;
    }
}