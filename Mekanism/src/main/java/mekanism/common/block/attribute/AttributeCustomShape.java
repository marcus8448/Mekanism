package mekanism.common.block.attribute;

import net.minecraft.util.shape.VoxelShape;

public class AttributeCustomShape implements Attribute {

    private final VoxelShape[] bounds;

    public AttributeCustomShape(VoxelShape[] bounds) {
        this.bounds = bounds;
    }

    public VoxelShape[] getBounds() {
        return bounds;
    }
}
