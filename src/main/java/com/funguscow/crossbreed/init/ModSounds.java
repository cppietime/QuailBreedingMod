package com.funguscow.crossbreed.init;

import com.funguscow.crossbreed.BreedMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BreedMod.MODID);

    public static final RegistryObject<SoundEvent> QUAIL_AMBIENT = SOUNDS.register("quail_ambient",
            () -> new SoundEvent(new ResourceLocation("breesources", "quail_ambient"))),
        QUAIL_HURT = SOUNDS.register("quail_hurt",
                () -> new SoundEvent(new ResourceLocation("breesources", "quail_hurt"))),
            QUAIL_DIE = SOUNDS.register("quail_die",
                    () -> new SoundEvent(new ResourceLocation("breesources", "quail_die"))),
            QUAIL_STEP = SOUNDS.register("quail_step",
                    () -> new SoundEvent(new ResourceLocation("breesources", "quail_step"))),
            QUAIL_PLOP = SOUNDS.register("quail_plop",
                    () -> new SoundEvent(new ResourceLocation("breesources", "quail_plop"))),
            QUAIL_MILK = SOUNDS.register("quail_milk",
                    () -> new SoundEvent(new ResourceLocation("breesources", "quail_milk")));

}
