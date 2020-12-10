package com.funguscow.crossbreed.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;

public abstract class ModAnimalEntity extends AnimalEntity {

    protected ModAnimalEntity(EntityType<? extends AnimalEntity> type, World worldIn) {
        super(type, worldIn);
    }

    protected abstract int getBreedingTimeout();

    @Override
    public void livingTick() {
        super.livingTick();
        int timeout = getBreedingTimeout();
        if(getGrowingAge() > timeout)
            setGrowingAge(timeout);
        if(timeout > 6000){
            if(getGrowingAge() == 5999)
                setGrowingAge(timeout);
            else if(getGrowingAge() == 6000)
                setGrowingAge(5999);
        }
    }
}
