package mekanism.client.render;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nonnull;
import mekanism.api.RelativeSide;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.math.FloatingLong;
import mekanism.client.MekanismClient;
import mekanism.client.gui.GuiUtils;
import mekanism.client.gui.element.bar.GuiBar;
import mekanism.client.render.MekanismRenderer.Model3D;
import mekanism.client.render.lib.Quad;
import mekanism.client.render.lib.QuadUtils;
import mekanism.client.render.lib.Vertex;
import mekanism.client.render.lib.effect.BoltRenderer;
import mekanism.client.render.tileentity.IWireFrameRenderer;
import mekanism.common.Mekanism;
import mekanism.common.base.ProfilerConstants;
import mekanism.common.block.BlockBounding;
import mekanism.common.block.attribute.Attribute;
import mekanism.common.block.attribute.AttributeCustomSelectionBox;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.config.MekanismConfig;
import mekanism.common.item.ItemConfigurator;
import mekanism.common.item.ItemConfigurator.ConfiguratorMode;
import mekanism.common.item.gear.ItemFlamethrower;
import mekanism.common.item.gear.ItemMekaSuitArmor;
import mekanism.common.item.interfaces.IItemHUDProvider;
import mekanism.common.item.interfaces.IModeItem;
import mekanism.common.lib.Color;
import mekanism.common.lib.effect.BoltEffect;
import mekanism.common.lib.math.Pos3D;
import mekanism.common.lib.radiation.RadiationManager;
import mekanism.common.lib.radiation.RadiationManager.RadiationScale;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.registries.MekanismParticleTypes;
import mekanism.common.tile.TileEntityBoundingBlock;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.interfaces.ISideConfiguration;
import mekanism.common.util.ChemicalUtil;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.common.util.StorageUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderTickHandler {

    public final MinecraftClient minecraft = MinecraftClient.getInstance();

    private static final Identifier POWER_BAR = MekanismUtils.getResource(ResourceType.GUI_BAR, "horizontal_power_long.png");
    private static final Map<BlockState, List<Vertex[]>> cachedWireFrames = new HashMap<>();
    private static final Map<Direction, Map<TransmissionType, Model3D>> cachedOverlays = new EnumMap<>(Direction.class);
    private static final EquipmentSlot[] EQUIPMENT_ORDER = new EquipmentSlot[]{EquipmentSlot.OFFHAND, EquipmentSlot.MAINHAND,
                                                                                       EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS,
                                                                                       EquipmentSlot.FEET};

    private static final float HUD_SCALE = 0.6F;

    private static final HUDRenderer hudRenderer = new HUDRenderer();

    public static int modeSwitchTimer = 0;
    public static double prevRadiation = 0;

    private static final BoltRenderer boltRenderer = new BoltRenderer();

    public static void resetCached() {
        cachedOverlays.clear();
        cachedWireFrames.clear();
    }

    public static void renderBolt(Object renderer, BoltEffect bolt) {
        boltRenderer.update(renderer, bolt, MekanismRenderer.getPartialTick());
    }

    @SubscribeEvent
    public void renderWorld(RenderWorldLastEvent event) {
        MatrixStack matrix = event.getMatrixStack();
        matrix.push();
        // here we translate based on the inverse position of the client viewing camera to get back to 0, 0, 0
        Vec3d camVec = minecraft.gameRenderer.getCamera().getPos();
        matrix.translate(-camVec.x, -camVec.y, -camVec.z);
        VertexConsumerProvider.Immediate renderer = minecraft.getBufferBuilders().getEntityVertexConsumers();
        boltRenderer.render(minecraft.getTickDelta(), matrix, renderer);
        renderer.draw(MekanismRenderType.MEK_LIGHTNING);
        matrix.pop();
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == ElementType.ARMOR) {
            FloatingLong capacity = FloatingLong.ZERO, stored = FloatingLong.ZERO;
            for (ItemStack stack : minecraft.player.inventory.armor) {
                IEnergyContainer container = StorageUtils.getEnergyContainer(stack, 0);
                if (stack.getItem() instanceof ItemMekaSuitArmor && container != null) {
                    capacity = capacity.plusEqual(container.getMaxEnergy());
                    stored = stored.plusEqual(container.getEnergy());
                }
            }
            if (!capacity.isZero()) {
                int x = event.getWindow().getScaledWidth() / 2 - 91;
                int y = event.getWindow().getScaledHeight() - ForgeIngameGui.left_height + 2;
                int length = (int) Math.round(stored.divide(capacity).doubleValue() * 79);
                MatrixStack matrix = event.getMatrixStack();
                GuiUtils.renderExtendedTexture(matrix, GuiBar.BAR, 2, 2, x, y, 81, 6);
                minecraft.getTextureManager().bindTexture(POWER_BAR);
                DrawableHelper.drawTexture(matrix, x + 1, y + 1, length, 4, 0, 0, length, 4, 79, 4);
                minecraft.getTextureManager().bindTexture(ForgeIngameGui.GUI_ICONS_TEXTURE);
                ForgeIngameGui.left_height += 8;
            }
        }
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == ElementType.HOTBAR) {
            if (!minecraft.player.isSpectator() && MekanismConfig.client.enableHUD.get() && MekanismClient.renderHUD) {
                int count = 0;
                Map<EquipmentSlot, List<Text>> renderStrings = new LinkedHashMap<>();
                for (EquipmentSlot slotType : EQUIPMENT_ORDER) {
                    ItemStack stack = minecraft.player.getEquippedStack(slotType);
                    if (stack.getItem() instanceof IItemHUDProvider) {
                        List<Text> list = new ArrayList<>();
                        ((IItemHUDProvider) stack.getItem()).addHUDStrings(list, stack, slotType);
                        int size = list.size();
                        if (size > 0) {
                            renderStrings.put(slotType, list);
                            count += size;
                        }
                    }
                }
                int start = (renderStrings.size() * 2) + (count * 9);
                boolean alignLeft = MekanismConfig.client.alignHUDLeft.get();
                Window window = event.getWindow();
                int y = window.getScaledHeight();
                MatrixStack matrix = event.getMatrixStack();
                matrix.push();
                matrix.scale(HUD_SCALE, HUD_SCALE, HUD_SCALE);
                for (Map.Entry<EquipmentSlot, List<Text>> entry : renderStrings.entrySet()) {
                    for (Text text : entry.getValue()) {
                        drawString(window, matrix, text, alignLeft, (int) (y * (1 / HUD_SCALE)) - start, 0xC8C8C8);
                        start -= 9;
                    }
                    start -= 2;
                }
                matrix.pop();

                if (minecraft.player.getEquippedStack(EquipmentSlot.HEAD).getItem() instanceof ItemMekaSuitArmor) {
                    hudRenderer.renderHUD(matrix, event.getPartialTicks());
                }
            }
        }
    }

    @SubscribeEvent
    public void tickEnd(RenderTickEvent event) {
        if (event.phase == Phase.END) {
            if (minecraft.player != null && minecraft.player.world != null && !minecraft.isPaused()) {
                PlayerEntity player = minecraft.player;
                World world = minecraft.player.world;
                //TODO: Check if we have another matrix stack we should use
                MatrixStack matrix = new MatrixStack();
                renderStatusBar(matrix, player);
                //Traverse active jetpacks and do animations
                for (UUID uuid : Mekanism.playerState.getActiveJetpacks()) {
                    PlayerEntity p = world.getPlayerByUuid(uuid);
                    if (p != null) {
                        Pos3D playerPos = new Pos3D(p).translate(0, p.getStandingEyeHeight(), 0);
                        Vec3d playerMotion = p.getVelocity();
                        float random = (world.random.nextFloat() - 0.5F) * 0.1F;
                        Pos3D vLeft = new Pos3D(-0.43, -0.55, -0.54).rotateX(p.isInSneakingPose() ? 20 : 0).rotateY(p.bodyYaw);
                        renderJetpackSmoke(world, playerPos.translate(vLeft, playerMotion), vLeft.multiply(0.2).translate(playerMotion, vLeft.multiply(random)));
                        Pos3D vRight = new Pos3D(0.43, -0.55, -0.54).rotateX(p.isInSneakingPose() ? 20 : 0).rotateY(p.bodyYaw);
                        renderJetpackSmoke(world, playerPos.translate(vRight, playerMotion), vRight.multiply(0.2).translate(playerMotion, vRight.multiply(random)));
                        Pos3D vCenter = new Pos3D((world.random.nextFloat() - 0.5) * 0.4, -0.86, -0.30).rotateX(p.isInSneakingPose() ? 25 : 0).rotateY(p.bodyYaw);
                        renderJetpackSmoke(world, playerPos.translate(vCenter, playerMotion), vCenter.multiply(0.2).translate(playerMotion));
                    }
                }

                if (world.getTime() % 4 == 0) {
                    //Traverse active scuba masks and do animations
                    for (UUID uuid : Mekanism.playerState.getActiveScubaMasks()) {
                        PlayerEntity p = world.getPlayerByUuid(uuid);
                        if (p != null && p.isTouchingWater()) {
                            Pos3D vec = new Pos3D(0.4, 0.4, 0.4).multiply(p.getRotationVec(1)).translate(0, -0.2, 0);
                            Pos3D motion = vec.multiply(0.2).translate(p.getVelocity());
                            Pos3D v = new Pos3D(p).translate(0, p.getStandingEyeHeight(), 0).translate(vec);
                            world.addParticle((DefaultParticleType) MekanismParticleTypes.SCUBA_BUBBLE.getParticleType(), v.x, v.y, v.z, motion.x, motion.y + 0.2, motion.z);
                        }
                    }
                    //Traverse players and do animations for idle flame throwers
                    for (PlayerEntity p : world.getPlayers()) {
                        if (!p.handSwinging && !Mekanism.playerState.isFlamethrowerOn(p)) {
                            ItemStack currentItem = p.getMainHandStack();
                            if (!currentItem.isEmpty() && currentItem.getItem() instanceof ItemFlamethrower && ChemicalUtil.hasGas(currentItem)) {
                                Pos3D flameVec;
                                if (player == p && minecraft.options.perspective == 0) {
                                    flameVec = new Pos3D(1, 1, 1).multiply(p.getRotationVec(1)).rotateY(5).translate(0, p.getStandingEyeHeight() - 0.1, 0);
                                } else {
                                    double flameXCoord = -0.2;
                                    double flameYCoord = 1;
                                    double flameZCoord = 1.2;
                                    if (p.isInSneakingPose()) {
                                        flameYCoord -= 0.65;
                                        flameZCoord -= 0.15;
                                    }
                                    flameVec = new Pos3D(flameXCoord, flameYCoord, flameZCoord).rotateY(p.bodyYaw);
                                }
                                Vec3d motion = p.getVelocity();
                                Pos3D flameMotion = new Pos3D(motion.getX(), p.isOnGround() ? 0 : motion.getY(), motion.getZ());
                                Pos3D playerPos = new Pos3D(p);
                                Pos3D mergedVec = playerPos.translate(flameVec);
                                world.addParticle((DefaultParticleType) MekanismParticleTypes.JETPACK_FLAME.getParticleType(),
                                      mergedVec.x, mergedVec.y, mergedVec.z, flameMotion.x, flameMotion.y, flameMotion.z);
                            }
                        }
                    }
                }

                if (MekanismUtils.isPlayingMode(player)) {
                    player.getCapability(Capabilities.RADIATION_ENTITY_CAPABILITY).ifPresent(c -> {
                        double radiation = c.getRadiation();
                        double severity = RadiationScale.getScaledDoseSeverity(radiation) * 0.8;
                        if (prevRadiation < severity) {
                            prevRadiation = Math.min(severity, prevRadiation + 0.01);
                        }
                        if (prevRadiation > severity) {
                            prevRadiation = Math.max(severity, prevRadiation - 0.01);
                        }
                        if (severity > RadiationManager.BASELINE) {
                            int effect = (int) (prevRadiation * 255);
                            int color = (0x701E1E << 8) + effect;
                            MekanismRenderer.renderColorOverlay(matrix, 0, 0, minecraft.getWindow().getScaledWidth(), minecraft.getWindow().getScaledHeight(), color);
                        }
                    });
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockHover(DrawHighlightEvent.HighlightBlock event) {
        PlayerEntity player = minecraft.player;
        if (player == null) {
            return;
        }
        BlockHitResult rayTraceResult = event.getTarget();
        if (!rayTraceResult.getType().equals(Type.MISS)) {
            World world = player.getEntityWorld();
            BlockPos pos = rayTraceResult.getBlockPos();
            VertexConsumerProvider renderer = event.getBuffers();
            Camera info = event.getInfo();
            MatrixStack matrix = event.getMatrix();
            Profiler profiler = world.getProfiler();
            BlockState blockState = world.getBlockState(pos);
            boolean shouldCancel = false;
            profiler.push(ProfilerConstants.MEKANISM_OUTLINE);
            if (!blockState.isAir(world, pos) && world.getWorldBorder().contains(pos)) {
                BlockPos actualPos = pos;
                BlockState actualState = blockState;
                if (blockState.getBlock() instanceof BlockBounding) {
                    TileEntityBoundingBlock tile = MekanismUtils.getTileEntity(TileEntityBoundingBlock.class, world, pos);
                    if (tile != null) {
                        actualPos = tile.getMainPos();
                        actualState = world.getBlockState(actualPos);
                    }
                }
                if (Attribute.has(actualState.getBlock(), AttributeCustomSelectionBox.class)) {
                    WireFrameRenderer renderWireFrame = null;
                    if (Attribute.get(actualState.getBlock(), AttributeCustomSelectionBox.class).isJavaModel()) {
                        //If we use a TER to render the wire frame, grab the tile
                        BlockEntity tile = MekanismUtils.getTileEntity(world, actualPos);
                        if (tile != null) {
                            BlockEntityRenderer<BlockEntity> tileRenderer = BlockEntityRenderDispatcher.INSTANCE.get(tile);
                            if (tileRenderer instanceof IWireFrameRenderer) {
                                renderWireFrame = (buffer, matrixStack, red, green, blue, alpha) ->
                                      ((IWireFrameRenderer) tileRenderer).renderWireFrame(tile, event.getPartialTicks(), matrixStack, buffer, red, green, blue, alpha);
                            }
                        }
                    } else {
                        //Otherwise skip getting the tile and just grab the model
                        BlockState finalActualState = actualState;
                        renderWireFrame = (buffer, matrixStack, red, green, blue, alpha) ->
                              renderQuadsWireFrame(finalActualState, buffer, matrixStack.peek().getModel(), world.random, red, green, blue, alpha);
                    }
                    if (renderWireFrame != null) {
                        matrix.push();
                        Vec3d viewPosition = info.getPos();
                        matrix.translate(actualPos.getX() - viewPosition.x, actualPos.getY() - viewPosition.y, actualPos.getZ() - viewPosition.z);
                        renderWireFrame.render(renderer.getBuffer(RenderLayer.getLines()), matrix, 0, 0, 0, 0.4F);
                        matrix.pop();
                        shouldCancel = true;
                    }
                }
            }
            profiler.pop();

            ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemConfigurator)) {
                //If we are not holding a configurator, look if we are in the offhand
                stack = player.getStackInHand(Hand.OFF_HAND);
                if (stack.isEmpty() || !(stack.getItem() instanceof ItemConfigurator)) {
                    if (shouldCancel) {
                        event.setCanceled(true);
                    }
                    return;
                }
            }
            profiler.push(ProfilerConstants.CONFIGURABLE_MACHINE);
            ConfiguratorMode state = ((ItemConfigurator) stack.getItem()).getMode(stack);
            if (state.isConfigurating()) {
                TransmissionType type = Objects.requireNonNull(state.getTransmission(), "Configurating state requires transmission type");
                BlockEntity tile = MekanismUtils.getTileEntity(world, pos);
                if (tile instanceof ISideConfiguration) {
                    ISideConfiguration configurable = (ISideConfiguration) tile;
                    TileComponentConfig config = configurable.getConfig();
                    if (config.supports(type)) {
                        Direction face = rayTraceResult.getSide();
                        DataType dataType = config.getDataType(type, RelativeSide.fromDirections(configurable.getOrientation(), face));
                        if (dataType != null) {
                            Vec3d viewPosition = info.getPos();
                            matrix.push();
                            matrix.translate(pos.getX() - viewPosition.x, pos.getY() - viewPosition.y, pos.getZ() - viewPosition.z);
                            MekanismRenderer.renderObject(getOverlayModel(face, type), matrix, renderer.getBuffer(TexturedRenderLayers.getEntityTranslucentCull()),
                                  MekanismRenderer.getColorARGB(dataType.getColor(), 0.6F), MekanismRenderer.FULL_LIGHT, OverlayTexture.DEFAULT_UV);
                            matrix.pop();
                        }
                    }
                }
            }
            profiler.pop();
            if (shouldCancel) {
                event.setCanceled(true);
            }
        }
    }

    private void renderQuadsWireFrame(BlockState state, VertexConsumer buffer, Matrix4f matrix, Random rand, float red, float green, float blue,
          float alpha) {
        List<Vertex[]> allVertices = cachedWireFrames.computeIfAbsent(state, s -> {
            BakedModel bakedModel = MinecraftClient.getInstance().getBlockRenderManager().getModel(s);
            //TODO: Eventually we may want to add support for Model data
            IModelData modelData = EmptyModelData.INSTANCE;
            List<Vertex[]> vertices = new ArrayList<>();
            for (Direction direction : EnumUtils.DIRECTIONS) {
                QuadUtils.unpack(bakedModel.getQuads(s, direction, rand, modelData)).stream().map(Quad::getVertices).forEach(vertices::add);
            }
            QuadUtils.unpack(bakedModel.getQuads(s, null, rand, modelData)).stream().map(Quad::getVertices).forEach(vertices::add);
            return vertices;
        });
        for (Vertex[] vertices : allVertices) {
            Vector4f vertex = getVertex(matrix, vertices[0]);
            Vec3d normal = vertices[0].getNormal();
            Vector4f vertex2 = getVertex(matrix, vertices[1]);
            Vec3d normal2 = vertices[1].getNormal();
            Vector4f vertex3 = getVertex(matrix, vertices[2]);
            Vec3d normal3 = vertices[2].getNormal();
            Vector4f vertex4 = getVertex(matrix, vertices[3]);
            Vec3d normal4 = vertices[3].getNormal();
            buffer.vertex(vertex.getX(), vertex.getY(), vertex.getZ()).normal((float) normal.getX(), (float) normal.getY(), (float) normal.getZ()).color(red, green, blue, alpha).next();
            buffer.vertex(vertex2.getX(), vertex2.getY(), vertex2.getZ()).normal((float) normal2.getX(), (float) normal2.getY(), (float) normal2.getZ()).color(red, green, blue, alpha).next();

            buffer.vertex(vertex3.getX(), vertex3.getY(), vertex3.getZ()).normal((float) normal3.getX(), (float) normal3.getY(), (float) normal3.getZ()).color(red, green, blue, alpha).next();
            buffer.vertex(vertex4.getX(), vertex4.getY(), vertex4.getZ()).normal((float) normal4.getX(), (float) normal4.getY(), (float) normal4.getZ()).color(red, green, blue, alpha).next();

            buffer.vertex(vertex2.getX(), vertex2.getY(), vertex2.getZ()).normal((float) normal2.getX(), (float) normal2.getY(), (float) normal2.getZ()).color(red, green, blue, alpha).next();
            buffer.vertex(vertex3.getX(), vertex3.getY(), vertex3.getZ()).normal((float) normal3.getX(), (float) normal3.getY(), (float) normal3.getZ()).color(red, green, blue, alpha).next();

            buffer.vertex(vertex.getX(), vertex.getY(), vertex.getZ()).normal((float) normal.getX(), (float) normal.getY(), (float) normal.getZ()).color(red, green, blue, alpha).next();
            buffer.vertex(vertex4.getX(), vertex4.getY(), vertex4.getZ()).normal((float) normal4.getX(), (float) normal4.getY(), (float) normal4.getZ()).color(red, green, blue, alpha).next();
        }
    }

    private static Vector4f getVertex(Matrix4f matrix4f, Vertex vertex) {
        Vector4f vector4f = new Vector4f((float) vertex.getPos().getX(), (float) vertex.getPos().getY(), (float) vertex.getPos().getZ(), 1);
        vector4f.transform(matrix4f);
        return vector4f;
    }

    private void renderStatusBar(MatrixStack matrix, @Nonnull PlayerEntity player) {
        //TODO: use vanilla status bar text? Note, the vanilla status bar text stays a lot longer than we have our message
        // display for, so we would need to somehow modify it. This can be done via ATs but does cause it to always appear
        // to be more faded in color, and blinks to full color just before disappearing
        if (modeSwitchTimer > 1) {
            if (minecraft.currentScreen == null && minecraft.textRenderer != null) {
                ItemStack stack = player.getMainHandStack();
                if (IModeItem.isModeItem(stack, EquipmentSlot.MAINHAND)) {
                    Text scrollTextComponent = ((IModeItem) stack.getItem()).getScrollTextComponent(stack);
                    if (scrollTextComponent != null) {
                        int x = minecraft.getWindow().getScaledWidth();
                        int y = minecraft.getWindow().getScaledHeight();
                        int color = Color.rgbad(1, 1, 1, modeSwitchTimer / 100F).argb();
                        minecraft.textRenderer.draw(matrix, scrollTextComponent, x / 2 - minecraft.textRenderer.getWidth(scrollTextComponent) / 2, y - 60, color);
                    }
                }
            }
            modeSwitchTimer--;
        }
    }

    private void renderJetpackSmoke(World world, Pos3D pos, Pos3D motion) {
        world.addParticle((DefaultParticleType) MekanismParticleTypes.JETPACK_FLAME.getParticleType(), pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
        world.addParticle((DefaultParticleType) MekanismParticleTypes.JETPACK_SMOKE.getParticleType(), pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
    }

    private void drawString(Window window, MatrixStack matrix, Text text, boolean leftSide, int y, int color) {
        TextRenderer font = minecraft.textRenderer;
        // Note that we always offset by 2 pixels when left or right aligned
        if (leftSide) {
            font.drawWithShadow(matrix, text, 2, y, color);
        } else {
            int width = font.getWidth(text) + 2;
            font.drawWithShadow(matrix, text, window.getScaledWidth() - width, y, color);
        }
    }

    private Model3D getOverlayModel(Direction side, TransmissionType type) {
        if (cachedOverlays.containsKey(side) && cachedOverlays.get(side).containsKey(type)) {
            return cachedOverlays.get(side).get(type);
        }

        Model3D toReturn = new Model3D();
        toReturn.setTexture(MekanismRenderer.overlays.get(type));
        cachedOverlays.computeIfAbsent(side, s -> new EnumMap<>(TransmissionType.class)).putIfAbsent(type, toReturn);

        switch (side) {
            case DOWN:
                toReturn.minY = -.01;
                toReturn.maxY = -.001;

                toReturn.minX = 0;
                toReturn.minZ = 0;
                toReturn.maxX = 1;
                toReturn.maxZ = 1;
                break;
            case UP:
                toReturn.minY = 1.001;
                toReturn.maxY = 1.01;

                toReturn.minX = 0;
                toReturn.minZ = 0;
                toReturn.maxX = 1;
                toReturn.maxZ = 1;
                break;
            case NORTH:
                toReturn.minZ = -.01;
                toReturn.maxZ = -.001;

                toReturn.minX = 0;
                toReturn.minY = 0;
                toReturn.maxX = 1;
                toReturn.maxY = 1;
                break;
            case SOUTH:
                toReturn.minZ = 1.001;
                toReturn.maxZ = 1.01;

                toReturn.minX = 0;
                toReturn.minY = 0;
                toReturn.maxX = 1;
                toReturn.maxY = 1;
                break;
            case WEST:
                toReturn.minX = -.01;
                toReturn.maxX = -.001;

                toReturn.minY = 0;
                toReturn.minZ = 0;
                toReturn.maxY = 1;
                toReturn.maxZ = 1;
                break;
            case EAST:
                toReturn.minX = 1.001;
                toReturn.maxX = 1.01;

                toReturn.minY = 0;
                toReturn.minZ = 0;
                toReturn.maxY = 1;
                toReturn.maxZ = 1;
                break;
        }
        return toReturn;
    }

    @FunctionalInterface
    private interface WireFrameRenderer {

        void render(VertexConsumer buffer, MatrixStack matrix, float red, float green, float blue, float alpha);
    }
}