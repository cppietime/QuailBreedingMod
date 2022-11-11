package com.funguscow.crossbreed.entity;

import com.funguscow.crossbreed.init.ModEntities;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;


public class GenericEggEntity extends ThrowableItemProjectile {

    private static final EntityDataAccessor<String> ITEM_ID = SynchedEntityData.defineId(GenericEggEntity.class, EntityDataSerializers.STRING);

    private int spawnChance;
    private int manySpawnChance;
    private String animal;
    private String itemID;

    public GenericEggEntity(EntityType<? extends GenericEggEntity> type,
                            Level worldIn) {
        super(type, worldIn);
    }

    public GenericEggEntity(Level worldIn, LivingEntity entity) {
        super(ModEntities.GENERIC_EGG.get(), entity, worldIn);
    }

    public void defineSynchedData() {
        super.defineSynchedData();
        itemID = "minecraft:egg";
        entityData.define(ITEM_ID, "minecraft:egg");
    }

    public GenericEggEntity setEgg(
            String itemID,
            int spawnChance,
            int manySpawnChance,
            String animalType) {
        this.itemID = itemID;
        this.spawnChance = spawnChance;
        this.manySpawnChance = manySpawnChance;
        this.animal = animalType;
        this.entityData.set(ITEM_ID, itemID);
        return this;
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(entityData.get(ITEM_ID)));
    }

    /**
     * Display particles on destroy
     */
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            for (int i = 0; i < 8; ++i) {
                this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()), this.getX(), this.getY(), this.getZ(), ((double) this.random.nextFloat() - 0.5D) * 0.08D, ((double) this.random.nextFloat() - 0.5D) * 0.08D, ((double) this.random.nextFloat() - 0.5D) * 0.08D);
            }
        }
    }

    /**
     * Damage hit entity
     */
    @Override
    protected void onHitEntity(@NotNull EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        hitResult.getEntity().hurt(DamageSource.thrown(this, this.getOwner()), 0.0F);
    }

    /**
     * Called when this egg hits a block or entity.
     */
    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);
        if (!this.level.isClientSide()) {
            if (this.random.nextInt(Math.max(spawnChance, 1)) == 0) {
                int i = 1;
                if (this.random.nextInt(Math.max(manySpawnChance, 1)) == 0) {
                    i = 4;
                }

                for (int j = 0; j < i; ++j) {
                    Animal animalEntity = ((EntityType<? extends Animal>) ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(animal))).create(this.level);
                    if (animalEntity == null)
                        continue;
                    animalEntity.setAge(-24000);
                    animalEntity.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                    this.level.addFreshEntity(animalEntity);
                }
            }

            this.level.broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }

    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("Spawn"))
            spawnChance = nbt.getInt("Spawn");
        if (nbt.contains("Extra"))
            manySpawnChance = nbt.getInt("Extra");
        if (nbt.contains("Animal"))
            animal = nbt.getString("Animal");
        if (nbt.contains("Item"))
            itemID = (nbt.getString("Item"));
        entityData.set(ITEM_ID, itemID);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        itemID = entityData.get(ITEM_ID);
        nbt.putInt("Chance", spawnChance);
        nbt.putInt("Extra", manySpawnChance);
        nbt.putString("Animal", animal);
        nbt.putString("Item", itemID);
    }
}
