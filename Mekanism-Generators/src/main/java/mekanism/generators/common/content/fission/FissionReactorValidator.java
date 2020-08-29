package mekanism.generators.common.content.fission;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import mekanism.api.NBTConstants;
import mekanism.common.content.blocktype.BlockType;
import mekanism.common.content.blocktype.BlockTypeTile;
import mekanism.common.lib.multiblock.CuboidStructureValidator;
import mekanism.common.lib.multiblock.FormationProtocol.CasingType;
import mekanism.common.lib.multiblock.FormationProtocol.FormationResult;
import mekanism.common.util.EnumUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.generators.common.GeneratorsLang;
import mekanism.generators.common.registries.GeneratorsBlockTypes;
import mekanism.generators.common.tile.fission.TileEntityControlRodAssembly;
import mekanism.generators.common.tile.fission.TileEntityFissionFuelAssembly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

public class FissionReactorValidator extends CuboidStructureValidator<FissionReactorMultiblockData> {

    @Override
    protected CasingType getCasingType(BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        if (BlockTypeTile.is(block, GeneratorsBlockTypes.FISSION_REACTOR_CASING)) {
            return CasingType.FRAME;
        } else if (BlockTypeTile.is(block, GeneratorsBlockTypes.FISSION_REACTOR_PORT)) {
            return CasingType.VALVE;
        } else if (BlockTypeTile.is(block, GeneratorsBlockTypes.FISSION_REACTOR_LOGIC_ADAPTER)) {
            return CasingType.OTHER;
        }
        return CasingType.INVALID;
    }

    @Override
    protected boolean validateInner(BlockPos pos) {
        if (super.validateInner(pos)) {
            return true;
        }
        return BlockType.is(world.getBlockState(pos).getBlock(), GeneratorsBlockTypes.FISSION_FUEL_ASSEMBLY, GeneratorsBlockTypes.CONTROL_ROD_ASSEMBLY);
    }

    @Override
    public FormationResult postcheck(FissionReactorMultiblockData structure, Set<BlockPos> innerNodes) {
        Map<AssemblyPos, FuelAssembly> map = new HashMap<>();
        Set<BlockPos> fuelAssemblyCoords = new HashSet<>();
        int assemblyCount = 0, surfaceArea = 0;

        for (BlockPos coord : innerNodes) {
            BlockEntity tile = MekanismUtils.getTileEntity(world, coord);
            AssemblyPos pos = new AssemblyPos(coord.getX(), coord.getZ());
            FuelAssembly assembly = map.get(pos);

            if (tile instanceof TileEntityFissionFuelAssembly) {
                if (assembly == null) {
                    map.put(pos, new FuelAssembly(coord, false));
                } else {
                    assembly.fuelAssemblies.add(coord);
                }
                assemblyCount++;
                // compute surface area
                surfaceArea += 6;
                for (Direction side : EnumUtils.DIRECTIONS) {
                    if (fuelAssemblyCoords.contains(coord.offset(side))) {
                        surfaceArea -= 2;
                    }
                }
                fuelAssemblyCoords.add(coord);
                structure.internalLocations.add(coord);
            } else if (tile instanceof TileEntityControlRodAssembly) {
                if (assembly == null) {
                    map.put(pos, new FuelAssembly(coord, true));
                } else if (assembly.controlRodAssembly != null) {
                    // only one control rod per assembly
                    return FormationResult.fail(GeneratorsLang.FISSION_INVALID_EXTRA_CONTROL_ROD, coord);
                } else {
                    assembly.controlRodAssembly = coord;
                }
            }
        }

        // require at least one fuel assembly
        if (map.isEmpty()) {
            return FormationResult.fail(GeneratorsLang.FISSION_INVALID_MISSING_FUEL_ASSEMBLY);
        }

        for (FuelAssembly assembly : map.values()) {
            FormationResult result = assembly.validate();
            if (!result.isFormed()) {
                return result;
            }
            structure.assemblies.add(assembly.build());
        }

        structure.fuelAssemblies = assemblyCount;
        structure.surfaceArea = surfaceArea;

        return FormationResult.SUCCESS;
    }

    public static class FuelAssembly {

        public final TreeSet<BlockPos> fuelAssemblies = new TreeSet<>(Comparator.comparingInt(Vec3i::getY));
        public BlockPos controlRodAssembly;

        public FuelAssembly(BlockPos start, boolean isControlRod) {
            if (isControlRod) {
                controlRodAssembly = start;
            } else {
                fuelAssemblies.add(start);
            }
        }

        public FormationResult validate() {
            if (fuelAssemblies.isEmpty() || controlRodAssembly == null) {
                return FormationResult.fail(GeneratorsLang.FISSION_INVALID_BAD_FUEL_ASSEMBLY);
            }
            int prevY = -1;
            for (BlockPos coord : fuelAssemblies) {
                if (prevY != -1 && coord.getY() != prevY + 1) {
                    return FormationResult.fail(GeneratorsLang.FISSION_INVALID_MALFORMED_FUEL_ASSEMBLY, coord);
                }
                prevY = coord.getY();
            }

            if (controlRodAssembly.getY() != prevY + 1) {
                return FormationResult.fail(GeneratorsLang.FISSION_INVALID_BAD_CONTROL_ROD, controlRodAssembly);
            }
            return FormationResult.SUCCESS;
        }

        public FormedAssembly build() {
            BlockPos base = fuelAssemblies.first();
            return new FormedAssembly(base, fuelAssemblies.size());
        }
    }

    public static class FormedAssembly {

        private final BlockPos pos;
        private final int height;

        public FormedAssembly(BlockPos pos, int height) {
            this.pos = pos;
            this.height = height;
        }

        public CompoundTag write() {
            CompoundTag ret = new CompoundTag();
            ret.putInt(NBTConstants.X, pos.getX());
            ret.putInt(NBTConstants.Y, pos.getY());
            ret.putInt(NBTConstants.Z, pos.getZ());
            ret.putInt(NBTConstants.HEIGHT, height);
            return ret;
        }

        public BlockPos getPos() {
            return pos;
        }

        public int getHeight() {
            return height;
        }

        public static FormedAssembly read(CompoundTag nbt) {
            return new FormedAssembly(new BlockPos(nbt.getInt(NBTConstants.X), nbt.getInt(NBTConstants.Y), nbt.getInt(NBTConstants.Z)),
                  nbt.getInt(NBTConstants.HEIGHT));
        }
    }

    public static class AssemblyPos {

        private final int x, z;

        public AssemblyPos(int x, int z) {
            this.x = x;
            this.z = z;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + x;
            result = prime * result + z;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof AssemblyPos && ((AssemblyPos) obj).x == x && ((AssemblyPos) obj).z == z;
        }
    }
}
