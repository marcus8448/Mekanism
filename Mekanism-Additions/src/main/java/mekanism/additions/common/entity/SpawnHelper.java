package mekanism.additions.common.entity;

import java.util.List;
import mekanism.additions.common.config.AdditionsCommonConfig;
import mekanism.additions.common.config.MekanismAdditionsConfig;
import mekanism.additions.common.entity.baby.EntityBabyStray;
import mekanism.additions.common.registries.AdditionsEntityTypes;
import mekanism.api.providers.IEntityTypeProvider;
import mekanism.common.Mekanism;
import mekanism.common.registration.impl.EntityTypeRegistryObject;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnEntry;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraftforge.registries.ForgeRegistries;

public class SpawnHelper {

    public static void addSpawns() {
        for (Biome biome : ForgeRegistries.BIOMES) {
            List<SpawnEntry> monsterSpawns = biome.getEntitySpawnList(SpawnGroup.MONSTER);
            //Fail quick if no monsters can spawn in this biome anyways
            if (!monsterSpawns.isEmpty()) {
                Identifier biomeName = biome.getRegistryName();
                addSpawn(AdditionsEntityTypes.BABY_CREEPER, EntityType.CREEPER, MekanismAdditionsConfig.common.babyCreeper, monsterSpawns, biomeName);
                addSpawn(AdditionsEntityTypes.BABY_ENDERMAN, EntityType.ENDERMAN, MekanismAdditionsConfig.common.babyEnderman, monsterSpawns, biomeName);
                addSpawn(AdditionsEntityTypes.BABY_SKELETON, EntityType.SKELETON, MekanismAdditionsConfig.common.babySkeleton, monsterSpawns, biomeName);
                addSpawn(AdditionsEntityTypes.BABY_STRAY, EntityType.STRAY, MekanismAdditionsConfig.common.babyStray, monsterSpawns, biomeName);
                addSpawn(AdditionsEntityTypes.BABY_WITHER_SKELETON, EntityType.WITHER_SKELETON, MekanismAdditionsConfig.common.babyWitherSkeleton, monsterSpawns, biomeName);
            }
        }
        //Add special spawns to the fortress for baby wither skeletons and skeletons
        List<SpawnEntry> fortressSpawns = StructureFeature.FORTRESS.getMonsterSpawns();
        addSpawn(AdditionsEntityTypes.BABY_WITHER_SKELETON, EntityType.WITHER_SKELETON, MekanismAdditionsConfig.common.babyWitherSkeleton, fortressSpawns);
        addSpawn(AdditionsEntityTypes.BABY_SKELETON, EntityType.SKELETON, MekanismAdditionsConfig.common.babySkeleton, fortressSpawns);

        //Register spawn controls for the baby entities based on the vanilla spawn controls
        registerSpawnControls(AdditionsEntityTypes.BABY_CREEPER, AdditionsEntityTypes.BABY_ENDERMAN, AdditionsEntityTypes.BABY_SKELETON,
              AdditionsEntityTypes.BABY_WITHER_SKELETON);
        //Slightly different restrictions for the baby stray, as strays have a slightly different spawn restriction
        SpawnRestriction.register(AdditionsEntityTypes.BABY_STRAY.get(), SpawnRestriction.Location.ON_GROUND,
              Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityBabyStray::spawnRestrictions);
        //Add the global entity type attributes for the entities
        DefaultAttributeRegistry.put(AdditionsEntityTypes.BABY_CREEPER.getEntityType(), CreeperEntity.createCreeperAttributes().build());
        DefaultAttributeRegistry.put(AdditionsEntityTypes.BABY_ENDERMAN.getEntityType(), EndermanEntity.createEndermanAttributes().build());
        DefaultAttributeRegistry.put(AdditionsEntityTypes.BABY_SKELETON.getEntityType(), AbstractSkeletonEntity.createAbstractSkeletonAttributes().build());
        DefaultAttributeRegistry.put(AdditionsEntityTypes.BABY_STRAY.getEntityType(), AbstractSkeletonEntity.createAbstractSkeletonAttributes().build());
        DefaultAttributeRegistry.put(AdditionsEntityTypes.BABY_WITHER_SKELETON.getEntityType(), AbstractSkeletonEntity.createAbstractSkeletonAttributes().build());
    }

    private static void addSpawn(IEntityTypeProvider entityTypeProvider, EntityType<?> parent, AdditionsCommonConfig.SpawnConfig spawnConfig,
          List<SpawnEntry> monsterSpawns) {
        if (spawnConfig.shouldSpawn.get()) {
            addSpawn(entityTypeProvider, parent, spawnConfig, monsterSpawns, "to nether fortresses");
        }
    }

    private static void addSpawn(IEntityTypeProvider entityTypeProvider, EntityType<?> parent, AdditionsCommonConfig.SpawnConfig spawnConfig,
          List<SpawnEntry> monsterSpawns, Identifier biomeName) {
        if (spawnConfig.shouldSpawn.get() && !spawnConfig.biomeBlackList.get().contains(biomeName)) {
            addSpawn(entityTypeProvider, parent, spawnConfig, monsterSpawns, "in biome " + biomeName);
        }
    }

    private static void addSpawn(IEntityTypeProvider entityTypeProvider, EntityType<?> parent, AdditionsCommonConfig.SpawnConfig spawnConfig,
          List<SpawnEntry> monsterSpawns, String location) {
        monsterSpawns.stream().filter(monsterSpawn -> monsterSpawn.type == parent).findFirst().ifPresent(parentEntry -> {
            //If the adult mob can spawn in this biome let the baby mob spawn in it
            //Note: We adjust the mob's spawning based on the adult's spawn rates
            EntityType<?> entityType = entityTypeProvider.getEntityType();
            int weight = (int) Math.ceil(parentEntry.weight * spawnConfig.weightPercentage.get());
            int minSize = (int) Math.ceil(parentEntry.minGroupSize * spawnConfig.minSizePercentage.get());
            int maxSize = (int) Math.ceil(parentEntry.maxGroupSize * spawnConfig.maxSizePercentage.get());
            monsterSpawns.add(new SpawnEntry(entityType, weight, minSize, Math.max(minSize, maxSize)));
            Mekanism.logger.debug("Adding spawn rate for {} {}, with weight: {}, minSize: {}, maxSize: {}", entityType.getRegistryName(),
                  location, weight, minSize, maxSize);
        });
    }

    @SafeVarargs
    private static void registerSpawnControls(EntityTypeRegistryObject<? extends HostileEntity>... entityTypeROs) {
        for (EntityTypeRegistryObject<? extends HostileEntity> entityTypeRO : entityTypeROs) {
            SpawnRestriction.register(entityTypeRO.get(), SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                  HostileEntity::canSpawnInDark);
        }
    }
}