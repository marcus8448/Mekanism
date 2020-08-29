package mekanism.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import mekanism.client.gui.GuiRadialSelector;
import mekanism.client.render.RenderTickHandler;
import mekanism.client.sound.GeigerSound;
import mekanism.client.sound.SoundHandler;
import mekanism.common.CommonPlayerTickHandler;
import mekanism.common.Mekanism;
import mekanism.common.base.HolidayManager;
import mekanism.common.base.KeySync;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.gear.Modules;
import mekanism.common.content.gear.mekasuit.ModuleJetpackUnit;
import mekanism.common.content.gear.mekasuit.ModuleMekaSuit.ModuleVisionEnhancementUnit;
import mekanism.common.content.teleporter.TeleporterFrequency;
import mekanism.common.item.gear.ItemFlamethrower;
import mekanism.common.item.gear.ItemJetpack;
import mekanism.common.item.gear.ItemJetpack.JetpackMode;
import mekanism.common.item.gear.ItemMekaSuitArmor;
import mekanism.common.item.interfaces.IModeItem;
import mekanism.common.item.interfaces.IRadialModeItem;
import mekanism.common.item.interfaces.IRadialSelectorEnum;
import mekanism.common.lib.radiation.RadiationManager.RadiationScale;
import mekanism.common.network.PacketModeChange;
import mekanism.common.network.PacketPortableTeleporterGui;
import mekanism.common.network.PacketPortableTeleporterGui.PortableTeleporterPacketType;
import mekanism.common.network.PacketRadialModeChange;
import mekanism.common.registries.MekanismGases;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Client-side tick handler for Mekanism. Used mainly for the update check upon startup.
 *
 * @author AidanBrady
 */
public class ClientTickHandler {

    public static final MinecraftClient minecraft = MinecraftClient.getInstance();
    public static final Random rand = new Random();
    public static final Map<PlayerEntity, TeleportData> portableTeleports = new Object2ObjectOpenHashMap<>();
    public boolean initHoliday = false;
    public boolean shouldReset = false;
    public static boolean firstTick = true;
    public static boolean visionEnhancement = false;

    private static long lastScrollTime = -1;
    private static double scrollDelta;

    public static boolean isJetpackActive(PlayerEntity player) {
        if (player != minecraft.player) {
            return Mekanism.playerState.isJetpackOn(player);
        }
        if (MekanismUtils.isPlayingMode(player)) {
            ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);
            if (!chest.isEmpty()) {
                JetpackMode mode = getJetpackMode(chest);
                if (mode == JetpackMode.NORMAL) {
                    return minecraft.currentScreen == null && minecraft.options.keyJump.isPressed();
                } else if (mode == JetpackMode.HOVER) {
                    boolean ascending = minecraft.options.keyJump.isPressed();
                    boolean descending = minecraft.options.keySneak.isPressed();
                    if (!ascending || descending || minecraft.currentScreen != null) {
                        return !CommonPlayerTickHandler.isOnGround(player);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /** Will return null if jetpack module is not active */
    private static JetpackMode getJetpackMode(ItemStack stack) {
        if (stack.getItem() instanceof ItemJetpack && ChemicalUtil.hasGas(stack)) {
            return ((ItemJetpack) stack.getItem()).getMode(stack);
        } else if (stack.getItem() instanceof IModuleContainerItem && ChemicalUtil.hasChemical(stack, MekanismGases.HYDROGEN.get())) {
            ModuleJetpackUnit module = Modules.load(stack, Modules.JETPACK_UNIT);
            if (module != null && module.isEnabled()) {
                return module.getMode();
            }
        }
        return null;
    }

    public static boolean isScubaMaskOn(PlayerEntity player) {
        if (player != minecraft.player) {
            return Mekanism.playerState.isScubaMaskOn(player);
        }
        return CommonPlayerTickHandler.isScubaMaskOn(player);
    }

    public static boolean isGravitationalModulationOn(PlayerEntity player) {
        if (player != minecraft.player) {
            return Mekanism.playerState.isGravitationalModulationOn(player);
        }
        return CommonPlayerTickHandler.isGravitationalModulationOn(player);
    }

    public static boolean isVisionEnhancementOn(PlayerEntity player) {
        ModuleVisionEnhancementUnit module = Modules.load(player.getEquippedStack(EquipmentSlot.HEAD), Modules.VISION_ENHANCEMENT_UNIT);
        return module != null && module.isEnabled() && module.getContainerEnergy().greaterThan(MekanismConfig.gear.mekaSuitEnergyUsageVisionEnhancement.get());
    }

    public static boolean isFlamethrowerOn(PlayerEntity player) {
        if (player != minecraft.player) {
            return Mekanism.playerState.isFlamethrowerOn(player);
        }
        return hasFlamethrower(player) && minecraft.options.keyUse.isPressed();
    }

    public static boolean hasFlamethrower(PlayerEntity player) {
        ItemStack currentItem = player.inventory.getMainHandStack();
        return !currentItem.isEmpty() && currentItem.getItem() instanceof ItemFlamethrower && ChemicalUtil.hasGas(currentItem);
    }

    public static void portableTeleport(PlayerEntity player, Hand hand, TeleporterFrequency freq) {
        int delay = MekanismConfig.gear.portableTeleporterDelay.get();
        if (delay == 0) {
            Mekanism.packetHandler.sendToServer(new PacketPortableTeleporterGui(PortableTeleporterPacketType.TELEPORT, hand, freq));
        } else {
            portableTeleports.put(player, new TeleportData(hand, freq, minecraft.world.getTime() + delay));
        }
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        if (event.phase == Phase.START) {
            tickStart();
        }
    }

    public void tickStart() {
        MekanismClient.ticksPassed++;

        if (firstTick && minecraft.world != null) {
            MekanismClient.launchClient();
            firstTick = false;
        }

        if (minecraft.world != null) {
            shouldReset = true;
        } else if (shouldReset) {
            MekanismClient.reset();
            shouldReset = false;
        }

        if (minecraft.world != null && minecraft.player != null && !Mekanism.proxy.isPaused()) {
            if (!initHoliday || MekanismClient.ticksPassed % 1_200 == 0) {
                HolidayManager.notify(MinecraftClient.getInstance().player);
                initHoliday = true;
            }

            if (minecraft.world.getTime() - lastScrollTime > 20) {
                scrollDelta = 0;
            }

            Mekanism.radiationManager.tickClient(minecraft.player);

            UUID playerUUID = minecraft.player.getUuid();
            // Update player's state for various items; this also automatically notifies server if something changed and
            // kicks off sounds as necessary
            Mekanism.playerState.setJetpackState(playerUUID, isJetpackActive(minecraft.player), true);
            Mekanism.playerState.setScubaMaskState(playerUUID, isScubaMaskOn(minecraft.player), true);
            Mekanism.playerState.setGravitationalModulationState(playerUUID, isGravitationalModulationOn(minecraft.player), true);
            Mekanism.playerState.setFlamethrowerState(playerUUID, hasFlamethrower(minecraft.player), isFlamethrowerOn(minecraft.player), true);

            for (Iterator<Entry<PlayerEntity, TeleportData>> iter = portableTeleports.entrySet().iterator(); iter.hasNext(); ) {
                Entry<PlayerEntity, TeleportData> entry = iter.next();
                PlayerEntity player = entry.getKey();
                for (int i = 0; i < 100; i++) {
                    double x = player.getX() + rand.nextDouble() - 0.5D;
                    double y = player.getY() + rand.nextDouble() * 2 - 2D;
                    double z = player.getZ() + rand.nextDouble() - 0.5D;
                    minecraft.world.addParticle(ParticleTypes.PORTAL, x, y, z, 0, 1, 0);
                }

                if (minecraft.world.getTime() == entry.getValue().teleportTime) {
                    Mekanism.packetHandler.sendToServer(new PacketPortableTeleporterGui(PortableTeleporterPacketType.TELEPORT, entry.getValue().hand, entry.getValue().freq));
                    iter.remove();
                }
            }

            ItemStack chestStack = minecraft.player.getEquippedStack(EquipmentSlot.CHEST);
            ModuleJetpackUnit jetpackModule = Modules.load(chestStack, Modules.JETPACK_UNIT);

            if (!chestStack.isEmpty() && (chestStack.getItem() instanceof ItemJetpack || jetpackModule != null)) {
                MekanismClient.updateKey(minecraft.options.keyJump, KeySync.ASCEND);
                MekanismClient.updateKey(minecraft.options.keySneak, KeySync.DESCEND);
            }

            if (isJetpackActive(minecraft.player)) {
                JetpackMode mode = getJetpackMode(chestStack);
                Vec3d motion = minecraft.player.getVelocity();
                if (mode == JetpackMode.NORMAL) {
                    minecraft.player.setVelocity(motion.getX(), Math.min(motion.getY() + 0.15D, 0.5D), motion.getZ());
                    minecraft.player.fallDistance = 0.0F;
                } else if (mode == JetpackMode.HOVER) {
                    boolean ascending = minecraft.options.keyJump.isPressed();
                    boolean descending = minecraft.options.keySneak.isPressed();
                    if ((!ascending && !descending) || (ascending && descending) || minecraft.currentScreen != null) {
                        if (motion.getY() > 0) {
                            minecraft.player.setVelocity(motion.getX(), Math.max(motion.getY() - 0.15D, 0), motion.getZ());
                        } else if (motion.getY() < 0) {
                            if (!CommonPlayerTickHandler.isOnGround(minecraft.player)) {
                                minecraft.player.setVelocity(motion.getX(), Math.min(motion.getY() + 0.15D, 0), motion.getZ());
                            }
                        }
                    } else if (ascending) {
                        minecraft.player.setVelocity(motion.getX(), Math.min(motion.getY() + 0.15D, 0.2D), motion.getZ());
                    } else if (!CommonPlayerTickHandler.isOnGround(minecraft.player)) {
                        minecraft.player.setVelocity(motion.getX(), Math.max(motion.getY() - 0.15D, -0.2D), motion.getZ());
                    }
                    minecraft.player.fallDistance = 0.0F;
                }
            }

            if (isScubaMaskOn(minecraft.player) && minecraft.player.getAir() == 300) {
                for (StatusEffectInstance effect : minecraft.player.getStatusEffects()) {
                    for (int i = 0; i < 9; i++) {
                        effect.update(minecraft.player, () -> MekanismUtils.onChangedPotionEffect(minecraft.player, effect, true));
                    }
                }
            }

            if (isVisionEnhancementOn(minecraft.player)) {
                visionEnhancement = true;
                // adds if it doesn't exist, otherwise tops off duration to 200
                minecraft.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 200, 0, false, true, false));
            } else if (visionEnhancement) {
                visionEnhancement = false;
                minecraft.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
            }

            ItemStack stack = minecraft.player.getEquippedStack(EquipmentSlot.MAINHAND);
            if (MekanismKeyHandler.isKeyDown(MekanismKeyHandler.handModeSwitchKey) && stack.getItem() instanceof IRadialModeItem) {
                if (minecraft.currentScreen == null || minecraft.currentScreen instanceof GuiRadialSelector) {
                    updateSelectorRenderer((IRadialModeItem<?>) stack.getItem());
                }
            } else {
                if (minecraft.currentScreen instanceof GuiRadialSelector) {
                    minecraft.openScreen(null);
                }
            }

            if (MekanismConfig.client.enablePlayerSounds.get() && SoundHandler.radiationSoundMap.isEmpty()) {
                for (RadiationScale scale : EnumUtils.RADIATION_SCALES) {
                    if (scale != RadiationScale.NONE) {
                        GeigerSound sound = GeigerSound.create(minecraft.player, scale);
                        SoundHandler.radiationSoundMap.put(scale, sound);
                        SoundHandler.playSound(sound);
                    }
                }
            }
        }
    }

    private <TYPE extends Enum<TYPE> & IRadialSelectorEnum<TYPE>> void updateSelectorRenderer(IRadialModeItem<TYPE> modeItem) {
        Class<TYPE> modeClass = modeItem.getModeClass();
        if (!(minecraft.currentScreen instanceof GuiRadialSelector) || ((GuiRadialSelector<?>) minecraft.currentScreen).getEnumClass() != modeClass) {
            minecraft.openScreen(new GuiRadialSelector<>(modeClass, () -> {
                if (minecraft.player != null) {
                    ItemStack s = minecraft.player.getEquippedStack(EquipmentSlot.MAINHAND);
                    if (s.getItem() instanceof IRadialModeItem) {
                        return ((IRadialModeItem<TYPE>) s.getItem()).getMode(s);
                    }
                }
                return modeItem.getDefaultMode();
            }, type -> {
                if (minecraft.player != null) {
                    Mekanism.packetHandler.sendToServer(new PacketRadialModeChange(EquipmentSlot.MAINHAND, type.ordinal()));
                }
            }));
        }
    }

    @SubscribeEvent
    public void onMouseEvent(MouseScrollEvent event) {
        if (MekanismConfig.client.allowModeScroll.get() && minecraft.player != null && minecraft.player.isSneaking()) {
            handleModeScroll(event, event.getScrollDelta());
        }
    }

    @SubscribeEvent
    public void onGuiMouseEvent(GuiScreenEvent.MouseScrollEvent.Pre event) {
        if (event.getGui() instanceof GuiRadialSelector) {
            handleModeScroll(event, event.getScrollDelta());
        }
    }

    private void handleModeScroll(Event event, double delta) {
        if (delta != 0 && IModeItem.isModeItem(minecraft.player, EquipmentSlot.MAINHAND)) {
            lastScrollTime = minecraft.world.getTime();
            scrollDelta += delta;
            int shift = (int) scrollDelta;
            scrollDelta %= 1;
            if (shift != 0) {
                RenderTickHandler.modeSwitchTimer = 100;
                Mekanism.packetHandler.sendToServer(new PacketModeChange(EquipmentSlot.MAINHAND, shift));
            }
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onFogLighting(EntityViewRenderEvent.FogColors event) {
        if (visionEnhancement) {
            event.setBlue(0.4F);
            event.setRed(0.4F);
            event.setGreen(0.8F);
        }
    }

    @SubscribeEvent
    public void onFog(EntityViewRenderEvent.RenderFogEvent event) {
        if (visionEnhancement) {
            float fog = 0.1F;
            ModuleVisionEnhancementUnit module = Modules.load(minecraft.player.getEquippedStack(EquipmentSlot.HEAD), Modules.VISION_ENHANCEMENT_UNIT);
            if (module != null) {
                fog -= module.getInstalledCount() * 0.022F;
            }
            RenderSystem.fogDensity(fog);
            RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
        }
    }

    @SubscribeEvent
    public void renderEntityPre(RenderLivingEvent.Pre<?, ?> evt) {
        EntityModel<?> model = evt.getRenderer().getModel();
        if (model instanceof BipedEntityModel) {
            //If the entity has a biped model, then see if it is wearing a meka suit, in which case we want to hide various parts of the model
            setModelVisibility(evt.getEntity(), (BipedEntityModel<?>) model, false);
        }
    }

    @SubscribeEvent
    public void renderEntityPost(RenderLivingEvent.Post<?, ?> evt) {
        EntityModel<?> model = evt.getRenderer().getModel();
        if (model instanceof BipedEntityModel) {
            //Undo model visibility changes we made to ensure that other entities of the same type are properly visible
            setModelVisibility(evt.getEntity(), (BipedEntityModel<?>) model, true);
        }
    }

    private static void setModelVisibility(LivingEntity entity, BipedEntityModel<?> entityModel, boolean showModel) {
        if (entity.getEquippedStack(EquipmentSlot.HEAD).getItem() instanceof ItemMekaSuitArmor) {
            entityModel.head.visible = showModel;
            entityModel.helmet.visible = showModel;
        }
        if (entity.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof ItemMekaSuitArmor) {
            entityModel.torso.visible = showModel;
            if (!(entity instanceof ArmorStandEntity)) {
                //Don't adjust arms for armor stands as the model will end up changing them anyways and then we may incorrectly activate them
                entityModel.leftArm.visible = showModel;
                entityModel.rightArm.visible = showModel;
            }
            if (entityModel instanceof PlayerEntityModel) {
                PlayerEntityModel<?> playerModel = (PlayerEntityModel<?>) entityModel;
                playerModel.jacket.visible = showModel;
                playerModel.leftSleeve.visible = showModel;
                playerModel.rightSleeve.visible = showModel;
            } else if (entityModel instanceof ArmorStandEntityModel) {
                ArmorStandEntityModel armorStandModel = (ArmorStandEntityModel) entityModel;
                armorStandModel.rightTorso.visible = showModel;
                armorStandModel.leftTorso.visible = showModel;
                armorStandModel.hip.visible = showModel;
            }
        }
        if (entity.getEquippedStack(EquipmentSlot.LEGS).getItem() instanceof ItemMekaSuitArmor) {
            entityModel.leftLeg.visible = showModel;
            entityModel.rightLeg.visible = showModel;
            if (entityModel instanceof PlayerEntityModel) {
                PlayerEntityModel<?> playerModel = (PlayerEntityModel<?>) entityModel;
                playerModel.leftPantLeg.visible = showModel;
                playerModel.rightPantLeg.visible = showModel;
            }
        }
    }

    private static class TeleportData {

        private final Hand hand;
        private final TeleporterFrequency freq;
        private final long teleportTime;

        public TeleportData(Hand h, TeleporterFrequency f, long t) {
            hand = h;
            freq = f;
            teleportTime = t;
        }
    }
}