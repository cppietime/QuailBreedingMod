package com.funguscow.crossbreed.init;

import com.funguscow.crossbreed.BreedMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BreedMod.MODID);

    public static final RegistryObject<SoundEvent> QUAIL_AMBIENT = SOUNDS.register("quail_ambient",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BreedMod.MODID, "quail_ambient"))),
            QUAIL_HURT = SOUNDS.register("quail_hurt",
                    () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BreedMod.MODID, "quail_hurt"))),
            QUAIL_DIE = SOUNDS.register("quail_die",
                    () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BreedMod.MODID, "quail_die"))),
            QUAIL_STEP = SOUNDS.register("quail_step",
                    () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BreedMod.MODID, "quail_step"))),
            QUAIL_PLOP = SOUNDS.register("quail_plop",
                    () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BreedMod.MODID, "quail_plop"))),
            QUAIL_MILK = SOUNDS.register("quail_milk",
                    () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BreedMod.MODID, "quail_milk")));

    public static void register(IEventBus bus) {
        SOUNDS.register(bus);
    }

}
