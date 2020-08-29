package mekanism.client.render.entity;

import javax.annotation.Nonnull;
import mekanism.client.model.ModelRobit;
import mekanism.common.entity.EntityRobit;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class RenderRobit extends MobEntityRenderer<EntityRobit, ModelRobit> {

    private static final Identifier ROBIT = MekanismUtils.getResource(ResourceType.RENDER, "robit.png");
    private static final Identifier ROBIT_ALT = MekanismUtils.getResource(ResourceType.RENDER, "robit2.png");

    public RenderRobit(EntityRenderDispatcher renderManager) {
        super(renderManager, new ModelRobit(), 0.5F);
    }

    @Nonnull
    @Override
    public Identifier getEntityTexture(@Nonnull EntityRobit robit) {
        if ((Math.abs(robit.getX() - robit.prevX) + Math.abs(robit.getX() - robit.prevX)) > 0.001) {
            if (robit.age % 3 == 0) {
                robit.texTick = !robit.texTick;
            }
        }
        return robit.texTick ? ROBIT_ALT : ROBIT;
    }
}