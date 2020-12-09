package com.funguscow.crossbreed.init;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.entity.GenericEggEntity;
import com.funguscow.crossbreed.entity.QuailEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, BreedMod.MODID);

    public static final RegistryObject<EntityType<QuailEntity>> QUAIL = ENTITY_TYPES.register("quail",
            () -> EntityType.Builder.create(QuailEntity::new, EntityClassification.CREATURE).size(0.375f, 0.625f)
                    .build(new ResourceLocation(BreedMod.MODID, "quail").toString()));
    public static final RegistryObject<EntityType<GenericEggEntity>> GENERIC_EGG = ENTITY_TYPES.register("generic_egg",
            () -> EntityType.Builder.<GenericEggEntity>create(GenericEggEntity::new, EntityClassification.MISC).size(0.25f, 0.25f)
                    .setTrackingRange(4)
                    .setCustomClientFactory(GenericEggEntity::new)
                .build(new ResourceLocation(BreedMod.MODID, "generic_egg").toString()));

    public static void registerPlacements() {
        EntitySpawnPlacementRegistry.register(QUAIL.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::canAnimalSpawn);
    }

}
