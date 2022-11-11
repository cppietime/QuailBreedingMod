package com.funguscow.crossbreed.init;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.entity.GenericEggEntity;
import com.funguscow.crossbreed.entity.QuailEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BreedMod.MODID);

    public static final RegistryObject<EntityType<QuailEntity>> QUAIL = ENTITY_TYPES.register(QuailEntity.ID,
            () -> EntityType.Builder.of(QuailEntity::new, MobCategory.CREATURE).sized(0.375f, 0.625f)
                    .build(new ResourceLocation(BreedMod.MODID, QuailEntity.ID).toString()));
    public static final RegistryObject<EntityType<GenericEggEntity>> GENERIC_EGG = ENTITY_TYPES.register("generic_egg",
            () -> EntityType.Builder.<GenericEggEntity>of(GenericEggEntity::new, MobCategory.MISC).sized(0.25f, 0.25f)
                    .setTrackingRange(4)
                .build(new ResourceLocation(BreedMod.MODID, "generic_egg").toString()));

    public static void registerPlacements() {
        SpawnPlacements.register(QUAIL.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
    }

}
