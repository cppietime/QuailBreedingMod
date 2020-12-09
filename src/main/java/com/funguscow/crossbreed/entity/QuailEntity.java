package com.funguscow.crossbreed.entity;

import com.funguscow.crossbreed.init.ModEntities;
import com.funguscow.crossbreed.init.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

/**
 * A quail entity to be bred and produce various resources
 */
public class QuailEntity extends AnimalEntity {

    public static class Gene {
        private static final float MUTATION_SIGMA = 0.05f;

        public float layAmount;
        public float layRandomAmount;
        public float layTime;
        public float layRandomTime;
        public float dominance;

        public Gene(Random rand){
            layAmount = rand.nextFloat() * 1.5f;
            layRandomAmount = rand.nextFloat();
            layTime = rand.nextFloat() * 1.5f;
            layRandomTime = rand.nextFloat();
            dominance = rand.nextFloat();
        }

        public Gene(){
        }

        public Gene crossover(Gene other, Random rand){
            Gene child = new Gene();
            child.layAmount = Math.max(0, rand.nextBoolean() ? layAmount : other.layAmount + (float)rand.nextGaussian() * MUTATION_SIGMA);
            child.layRandomAmount = Math.max(0, rand.nextBoolean() ? layRandomAmount : other.layRandomAmount + (float)rand.nextGaussian() * MUTATION_SIGMA);
            child.layTime = Math.max(0, rand.nextBoolean() ? layTime : other.layTime + (float)rand.nextGaussian() * MUTATION_SIGMA);
            child.layRandomTime = Math.max(0, rand.nextBoolean() ? layRandomTime : other.layRandomTime + (float)rand.nextGaussian() * MUTATION_SIGMA);
            child.dominance = rand.nextBoolean() ? dominance : other.dominance + (float)rand.nextGaussian() * MUTATION_SIGMA;
            return child;
        }

        public void readFromTag(CompoundNBT nbt){
            layAmount = nbt.getFloat("LayAmount");
            layRandomAmount = nbt.getFloat("LayRandomAmount");
            layTime = nbt.getFloat("LayTime");
            layRandomTime = nbt.getFloat("LayRandomTime");
            dominance = nbt.getFloat("Dominance");
        }

        public CompoundNBT writeToTag(){
            CompoundNBT nbt = new CompoundNBT();
            nbt.putFloat("LayAmount", layAmount);
            nbt.putFloat("LayRandomAmount", layRandomAmount);
            nbt.putFloat("LayTime", layTime);
            nbt.putFloat("LayRandomTime", layRandomTime);
            nbt.putFloat("Dominance", dominance);
            return nbt;
        }
    }

    private static final DataParameter<String> BREED_NAME = EntityDataManager.createKey(QuailEntity.class, DataSerializers.STRING);

    private static final Ingredient BREED_MATERIAL = Ingredient.fromItems(Items.WHEAT_SEEDS, Items.PUMPKIN_SEEDS, Items.MELON_SEEDS, Items.BEETROOT_SEEDS);

    private static final Logger LOGGER = LogManager.getLogger();

    private Gene gene, alleleA, alleleB;
    private QuailType breed;
    private int layTimer;

    public float oFlap, oFlapSpeed, wingRotation, wingRotDelta = 1.0f, destPos;

    public QuailEntity(EntityType<? extends AnimalEntity> type, World worldIn) {
        super(type, worldIn);
        breed = QuailType.BROWN;
        setAlleles(new Gene(rand), new Gene(rand));
        randomBreed();
    }

    public static AttributeModifierMap.MutableAttribute setAttributes(){
        return MobEntity.func_233666_p_()
                .createMutableAttribute(Attributes.MAX_HEALTH, 4)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25);
    }

    public Gene getGene(){
        return gene;
    }

    private void layLoot(){
        Item layItem;
        switch (breed.layItem) {
            case "FLOWERS":
                switch (rand.nextInt(16)) {
                    default:
                        layItem = Items.DANDELION;
                        break;
                    case 1:
                        layItem = Items.POPPY;
                        break;
                    case 2:
                        layItem = Items.SUNFLOWER;
                        break;
                    case 3:
                        layItem = Items.AZURE_BLUET;
                        break;
                    case 4:
                        layItem = Items.ALLIUM;
                        break;
                    case 5:
                        layItem = Items.CORNFLOWER;
                        break;
                    case 6:
                        layItem = Items.ORANGE_TULIP;
                        break;
                    case 7:
                        layItem = Items.PINK_TULIP;
                        break;
                    case 8:
                        layItem = Items.RED_TULIP;
                        break;
                    case 9:
                        layItem = Items.WHITE_TULIP;
                        break;
                }
                break;
            case "CORALS":
                switch (rand.nextInt(5)) {
                    case 0:
                        layItem = Items.BRAIN_CORAL_BLOCK;
                        break;
                    case 1:
                        layItem = Items.TUBE_CORAL_BLOCK;
                        break;
                    case 2:
                        layItem = Items.FIRE_CORAL_BLOCK;
                        break;
                    case 3:
                        layItem = Items.BUBBLE_CORAL_BLOCK;
                        break;
                    default:
                        layItem = Items.HORN_CORAL_BLOCK;
                        break;
                }
                break;
            case "FISH":
                switch (rand.nextInt(4)) {
                    case 0:
                        layItem = Items.COD;
                        break;
                    case 1:
                        layItem = Items.SALMON;
                        break;
                    case 2:
                        layItem = Items.TROPICAL_FISH;
                        break;
                    default:
                        layItem = Items.PUFFERFISH;
                        break;
                }
                break;
            default:
                layItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(breed.layItem));
                break;
        }
        int amount = breed.layAmount + rand.nextInt(breed.layRandomAmount + 1);
        amount = Math.max(1, (int)(amount * (gene.layAmount + rand.nextFloat() * gene.layAmount)));
        ItemStack itemStack = new ItemStack(layItem, amount);
        if(layItem == Items.BOOK){
            itemStack = EnchantmentHelper.addRandomEnchantment(rand, itemStack, 30, true);
        }
        entityDropItem(itemStack);
    }

    public void setAlleles(Gene a, Gene b){
        alleleA = a;
        alleleB = b;
        gene = alleleA.dominance >= alleleB.dominance ? alleleA : alleleB;
        resetTimer();
    }

    private void resetTimer(){
        layTimer = breed.layTime + rand.nextInt(breed.layTime + 1);
        layTimer *=  gene.layTime + rand.nextFloat() * gene.layRandomTime;
        layTimer = Math.max(600, layTimer);
    }

    public String getBreedName(){
        return dataManager.get(BREED_NAME);
    }

    public int getLayTimer(){
        return layTimer;
    }

    public void randomBreed(){
        switch(rand.nextInt(4)){
            case 0:
                breed = QuailType.PAINTED; break;
            case 1:
                breed = QuailType.BOBWHITE; break;
            case 2:
                breed = QuailType.BROWN; break;
            default:
                breed = QuailType.ELEGANT; break;
        }
        dataManager.set(BREED_NAME, breed.name);
    }

    private void setBreed(QuailType breed){
        this.breed = breed;
        dataManager.set(BREED_NAME, breed.name);
    }

    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, ILivingEntityData spawnDataIn, CompoundNBT dataTag) {
        randomBreed();
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new SwimGoal(this));
        goalSelector.addGoal(1, new PanicGoal(this, 1.25));
        goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        goalSelector.addGoal(3, new TemptGoal(this, 1.1, BREED_MATERIAL, false));
        goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
        goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0));
        goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0f));
        goalSelector.addGoal(7, new LookRandomlyGoal(this));
    }

    @Override
    protected void registerData(){
        super.registerData();
        dataManager.register(BREED_NAME, "painted");
    }

    @Override
    public AgeableEntity func_241840_a(ServerWorld world, AgeableEntity other) {
        QuailEntity child = ModEntities.QUAIL.get().create(world);
        QuailEntity otherQuail = (QuailEntity)other;
        Gene childA = alleleA.crossover(alleleB, rand);
        Gene childB = otherQuail.alleleA.crossover(otherQuail.alleleB, rand);
        child.setAlleles(childA, childB);
        child.setBreed(breed.getOffspring(otherQuail.breed, rand));
        return child;
    }

    @Override
    public void livingTick(){
        super.livingTick();
        this.oFlap = this.wingRotation;
        this.oFlapSpeed = this.destPos;
        this.destPos = (float)((double)this.destPos + (double)(this.onGround ? -1 : 2) * 0.3D);
        this.destPos = MathHelper.clamp(this.destPos, 0.0F, 1.0F);
        if (!this.onGround && this.wingRotDelta < 1.0F) {
            this.wingRotDelta = 1.0F;
        }

        this.wingRotDelta = (float)((double)this.wingRotDelta * 0.9D);
        Vector3d vector3d = this.getMotion();
        if (!this.onGround && vector3d.y < 0.0D) {
            this.setMotion(vector3d.mul(1.0D, 0.6D, 1.0D));
        }

        this.wingRotation += this.wingRotDelta * 2.0F;
        if(!world.isRemote && isAlive() && !isChild() && gene != null){
            layTimer --;
            if(layTimer <= 0){
                if(breed != null) {
                    resetTimer();
                    this.playSound(ModSounds.QUAIL_PLOP.get(), 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                    layLoot();
                }
            }
        }
    }

    @Override
    public boolean isBreedingItem(ItemStack stack){
        return BREED_MATERIAL.test(stack);
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return this.isChild() ? sizeIn.height * 0.85F : sizeIn.height * 0.92F;
    }

    @Override
    public boolean onLivingFall(float distance, float damageMultiplier) {
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
    public void readAdditional(CompoundNBT nbt){
        super.readAdditional(nbt);
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
    public void writeAdditional(CompoundNBT nbt){
        super.writeAdditional(nbt);
        nbt.putInt("EggLayTime", layTimer);
        nbt.putString("Breed", breed.name);
        nbt.put("AlleleA", alleleA.writeToTag());
        nbt.put("AlleleB", alleleB.writeToTag());
    }

    @Override
    public void onDeath(DamageSource source){
        if (net.minecraftforge.common.ForgeHooks.onLivingDeath(this, source)) return;
        if(this.removed || this.dead)
            return;
        super.onDeath(source);
        if(breed.deathItem == null)
            return;
        int lootingLevel = ForgeHooks.getLootingLevel(this, source.getTrueSource(), source);
        int amount = breed.deathAmount + rand.nextInt(breed.deathAmount + lootingLevel);
        entityDropItem(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(breed.deathItem)), amount));
    }

    @Override
    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if(itemStack.getItem() == Items.BUCKET && !this.isChild() && this.breed == QuailType.LEATHER){
            player.playSound(ModSounds.QUAIL_MILK.get(), 1.0f, 1.0f);
            ItemStack leftover = DrinkHelper.fill(itemStack, player, Items.MILK_BUCKET.getDefaultInstance());
            player.setHeldItem(hand, leftover);
            return ActionResultType.func_233537_a_(player.world.isRemote);
        }
        else
            return super.func_230254_b_(player, hand);
    }
}
