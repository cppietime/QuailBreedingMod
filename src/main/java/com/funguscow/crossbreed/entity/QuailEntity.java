package com.funguscow.crossbreed.entity;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.config.QuailConfig;
import com.funguscow.crossbreed.init.ModEntities;
import com.funguscow.crossbreed.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Lazy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A quail entity to be bred and produce various resources
 */
public class QuailEntity extends ModAnimalEntity {

    public static final String ID = "quail";

    private static final Lazy<Integer> breedingTimeout = Lazy.of(QuailConfig.COMMON.quailBreedingTime::get);

    public static class Gene {
        private static final float MUTATION_SIGMA = 0.05f;

        public float layAmount;
        public float layRandomAmount;
        public float layTime;
        public float layRandomTime;
        public float dominance;

        public Gene(RandomSource rand){
            layAmount = rand.nextFloat() * 1.5f;
            layRandomAmount = rand.nextFloat();
            layTime = rand.nextFloat() * 1.5f;
            layRandomTime = rand.nextFloat();
            dominance = rand.nextFloat();
        }

        public Gene(){
        }

        public Gene crossover(Gene other, RandomSource rand){
            Gene child = new Gene();
            child.layAmount = Math.max(0, rand.nextBoolean() ? layAmount : other.layAmount + (float)rand.nextGaussian() * MUTATION_SIGMA);
            child.layRandomAmount = Math.max(0, rand.nextBoolean() ? layRandomAmount : other.layRandomAmount + (float)rand.nextGaussian() * MUTATION_SIGMA);
            child.layTime = Math.max(0, rand.nextBoolean() ? layTime : other.layTime + (float)rand.nextGaussian() * MUTATION_SIGMA);
            child.layRandomTime = Math.max(0, rand.nextBoolean() ? layRandomTime : other.layRandomTime + (float)rand.nextGaussian() * MUTATION_SIGMA);
            child.dominance = rand.nextBoolean() ? dominance : other.dominance + (float)rand.nextGaussian() * MUTATION_SIGMA;
            return child;
        }

        public Gene readFromTag(CompoundTag nbt){
            layAmount = nbt.getFloat("LayAmount");
            layRandomAmount = nbt.getFloat("LayRandomAmount");
            layTime = nbt.getFloat("LayTime");
            layRandomTime = nbt.getFloat("LayRandomTime");
            dominance = nbt.getFloat("Dominance");
            return this;
        }

        public CompoundTag writeToTag(){
            CompoundTag nbt = new CompoundTag();
            nbt.putFloat("LayAmount", layAmount);
            nbt.putFloat("LayRandomAmount", layRandomAmount);
            nbt.putFloat("LayTime", layTime);
            nbt.putFloat("LayRandomTime", layRandomTime);
            nbt.putFloat("Dominance", dominance);
            return nbt;
        }
    }

    private static final EntityDataAccessor<String> BREED_NAME = SynchedEntityData.defineId(QuailEntity.class, EntityDataSerializers.STRING);

    public static final Ingredient BREED_MATERIAL = Ingredient.of(Items.WHEAT_SEEDS, Items.PUMPKIN_SEEDS, Items.MELON_SEEDS, Items.BEETROOT_SEEDS);

    private static final Logger LOGGER = LogManager.getLogger();

    private Gene gene, alleleA, alleleB;
    private QuailType breed;
    private int layTimer;

    public float oFlap, oFlapSpeed, wingRotation, wingRotDelta = 1.0f, destPos;

    public QuailEntity(EntityType<? extends Animal> type, Level worldIn) {
        super(type, worldIn);
        breed = QuailType.BROWN;
        randomBreed();
        setAlleles(new Gene(level.random), new Gene(level.random));
    }

    public static AttributeSupplier.Builder createAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    public Gene getGene(){
        return gene;
    }

    private void layLoot(){
        this.spawnAtLocation(breed.getLoot(random, gene));
        this.gameEvent(GameEvent.ENTITY_PLACE);
    }

    public void setAlleles(Gene a, Gene b){
        alleleA = a;
        alleleB = b;
        gene = alleleA.dominance >= alleleB.dominance ? alleleA : alleleB;
        resetTimer();
    }

    private void resetTimer(){
        layTimer = breed.layTime + random.nextInt(breed.layTime + 1);
        layTimer *=  gene.layTime + random.nextFloat() * gene.layRandomTime;
        layTimer = Math.max(600, layTimer);
    }

    public String getBreedName(){
        return entityData.get(BREED_NAME);
    }

    public int getLayTimer(){
        return layTimer;
    }

    public void randomBreed(){
        switch(random.nextInt(4)){
            case 0:
                breed = QuailType.PAINTED; break;
            case 1:
                breed = QuailType.BOBWHITE; break;
            case 2:
                breed = QuailType.BROWN; break;
            default:
                breed = QuailType.ELEGANT; break;
        }
        BreedMod.LOGGER.warn("Randomly set quail breed to " + breed.name);
        entityData.set(BREED_NAME, breed.name);
    }

    private void setBreed(QuailType breed){
        BreedMod.LOGGER.warn("Assign quail breed to " + breed.name);
        this.breed = breed;
        entityData.set(BREED_NAME, breed.name);
    }

    protected int getBreedingTimeout(){
        return breedingTimeout.get();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, SpawnGroupData spawnDataIn, CompoundTag dataTag) {
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new PanicGoal(this, 1.25));
        goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        goalSelector.addGoal(3, new TemptGoal(this, 1.1, BREED_MATERIAL, false));
        goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0f));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData(){
        super.defineSynchedData();
        entityData.define(BREED_NAME, "painted");
    }

    @Override
    public QuailEntity getBreedOffspring(ServerLevel world, AgeableMob other) {
        QuailEntity child = ModEntities.QUAIL.get().create(world);
        if(child != null) {
            QuailEntity otherQuail = (QuailEntity) other;
            Gene childA = alleleA.crossover(alleleB, random);
            Gene childB = otherQuail.alleleA.crossover(otherQuail.alleleB, random);
            child.setAlleles(childA, childB);
            child.setBreed(breed.getOffspring(otherQuail.breed, random));
        }
        return child;
    }

    @Override
    public void aiStep(){
        super.aiStep();
        this.oFlap = this.wingRotation;
        this.oFlapSpeed = this.destPos;
        this.destPos = (float)((double)this.destPos + (double)(this.onGround ? -1 : 2) * 0.3D);
        this.destPos = Mth.clamp(this.destPos, 0.0F, 1.0F);
        if (!this.onGround && this.wingRotDelta < 1.0F) {
            this.wingRotDelta = 1.0F;
        }

        this.wingRotDelta = (float)((double)this.wingRotDelta * 0.9D);
        Vec3 vector3d = this.getDeltaMovement();
        if (!this.onGround && vector3d.y < 0.0D) {
            this.setDeltaMovement(vector3d.multiply(1.0D, 0.6D, 1.0D));
        }

        this.wingRotation += this.wingRotDelta * 2.0F;
        if(!level.isClientSide && isAlive() && !isBaby() && gene != null){
            layTimer --;
            if(layTimer <= 0){
                if(breed != null) {
                    resetTimer();
                    this.playSound(ModSounds.QUAIL_PLOP.get(), 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
                    layLoot();
                }
            }
        }
    }

    @Override
    public boolean isFood(ItemStack stack){
        return BREED_MATERIAL.test(stack);
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return this.isBaby() ? sizeIn.height * 0.85F : sizeIn.height * 0.92F;
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.QUAIL_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return ModSounds.QUAIL_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.QUAIL_DIE.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(ModSounds.QUAIL_STEP.get(), 0.15F, 1.0F);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt){
        super.readAdditionalSaveData(nbt);
        if(nbt.contains("Breed")) {
            setBreed(QuailType.Types.get(nbt.getString("Breed")));
        }
        if(nbt.contains("AlleleA"))
            alleleA.readFromTag(nbt.getCompound("AlleleA"));
        if(nbt.contains("AlleleB"))
            alleleB.readFromTag(nbt.getCompound("AlleleB"));
        setAlleles(alleleA, alleleB);
        if(nbt.contains("EggLayTime"))
            layTimer = nbt.getInt("EggLayTime");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt){
        super.addAdditionalSaveData(nbt);
        nbt.putInt("EggLayTime", layTimer);
        nbt.putString("Breed", breed.name);
        nbt.put("AlleleA", alleleA.writeToTag());
        nbt.put("AlleleB", alleleB.writeToTag());
    }

    @Override
    public void die(DamageSource source){
        if (net.minecraftforge.common.ForgeHooks.onLivingDeath(this, source)) return;
        if(this.isRemoved() || this.dead)
            return;
        super.die(source);
        if(breed.deathItem == null || breed.deathItem.equals("") || breed.deathAmount <= 0)
            return;
        int lootingLevel = ForgeHooks.getLootingLevel(this, source.getEntity(), source);
        int amount = breed.deathAmount + random.nextInt(Math.max(1, breed.deathAmount) + lootingLevel);
        Item dieItem = QuailType.getItem(breed.deathItem, random);
        if(dieItem != null)
            spawnAtLocation(new ItemStack(dieItem, amount));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if(itemStack.getItem() == Items.BUCKET && !this.isBaby() && this.breed == QuailType.LEATHER){
            player.playSound(ModSounds.QUAIL_MILK.get(), 1.0f, 1.0f);
            ItemStack leftover = ItemUtils.createFilledResult(itemStack, player, Items.MILK_BUCKET.getDefaultInstance());
            player.setItemInHand(hand, leftover);
            return InteractionResult.sidedSuccess(player.level.isClientSide());
        }
        else
            return super.mobInteract(player, hand);
    }
}
