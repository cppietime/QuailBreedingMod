package com.funguscow.crossbreed.entity;

import com.funguscow.crossbreed.init.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

public class GenericEggEntity extends ProjectileItemEntity {

    private static final DataParameter<String> ITEM_ID = EntityDataManager.createKey(GenericEggEntity.class, DataSerializers.STRING);

    private int spawnChance;
    private int manySpawnChance;
    private String animal;

    public GenericEggEntity(EntityType<? extends GenericEggEntity> type,
                            World worldIn) {
        super(type, worldIn);
    }

    public GenericEggEntity(FMLPlayMessages.SpawnEntity packet, World worldIn){
        super(ModEntities.GENERIC_EGG.get(), worldIn);
    }

    public GenericEggEntity setEgg(
            String itemID,
            int spawnChance,
            int manySpawnChance,
            String animalType){
        dataManager.set(ITEM_ID, itemID);
        this.spawnChance = spawnChance;
        this.manySpawnChance = manySpawnChance;
        this.animal = animalType;
        return this;
    }

    protected void registerData(){
        super.registerData();
        dataManager.register(ITEM_ID, "minecraft:egg");
    }

    @Override
    protected Item getDefaultItem() {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(dataManager.get(ITEM_ID)));
    }

    /**
     * Display particles on destroy
     */
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 3) {
            for(int i = 0; i < 8; ++i) {
                this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, this.getItem()), this.getPosX(), this.getPosY(), this.getPosZ(), ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D);
            }
        }
    }

    /**
     * Damage hit entity
     */
    @Override
    protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
        super.onEntityHit(p_213868_1_);
        p_213868_1_.getEntity().attackEntityFrom(DamageSource.causeThrownDamage(this, this.func_234616_v_()), 0.0F);
    }

    /**
     * Called when this egg hits a block or entity.
     */
    protected void onImpact(RayTraceResult result) {
        super.onImpact(result);
        if (!this.world.isRemote) {
            if (this.rand.nextInt(Math.max(spawnChance, 1)) == 0) {
                int i = 1;
                if (this.rand.nextInt(Math.max(manySpawnChance, 1)) == 0) {
                    i = 4;
                }

                for(int j = 0; j < i; ++j) {
                    AnimalEntity animalEntity = ((EntityType<? extends AnimalEntity>) ForgeRegistries.ENTITIES.getValue(new ResourceLocation(animal))).create(this.world);
                    if(animalEntity == null)
                        continue;
                    animalEntity.setGrowingAge(-24000);
                    animalEntity.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw, 0.0F);
                    this.world.addEntity(animalEntity);
                }
            }

            this.world.setEntityState(this, (byte)3);
            this.remove();
        }

    }

//    public void readSpawnData(PacketBuffer packet){
//        readAdditional(packet.readCompoundTag());
//    }

    public void readAdditional(CompoundNBT nbt){
        super.readAdditional(nbt);
        if(nbt.contains("Spawn"))
            spawnChance = nbt.getInt("Spawn");
        if(nbt.contains("Extra"))
            manySpawnChance = nbt.getInt("Extra");
        if(nbt.contains("Animal"))
            animal = nbt.getString("Animal");
        if(nbt.contains("Item"))
            dataManager.set(ITEM_ID, nbt.getString("Item"));
    }

//    public void writeSpawnData(PacketBuffer packet){
//        CompoundNBT nbt = new CompoundNBT();
//        writeAdditional(nbt);
//        packet.writeCompoundTag(nbt);
//    }

    public void writeAdditional(CompoundNBT nbt){
        super.writeAdditional(nbt);
        nbt.putInt("Chance", spawnChance);
        nbt.putInt("Extra", manySpawnChance);
        nbt.putString("Animal", animal);
        nbt.putString("Item", dataManager.get(ITEM_ID));
    }

//    public ItemStack getItem(){
//        return new ItemStack(getDefaultItem());
//    }

    public IPacket<?> createSpawnPacket(){
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
