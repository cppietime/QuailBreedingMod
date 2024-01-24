package com.funguscow.crossbreed.entity;

import com.funguscow.crossbreed.config.QuailConfig;
import com.funguscow.crossbreed.init.ModEntities;
import com.funguscow.crossbreed.init.ModSounds;
import com.funguscow.crossbreed.util.TagUtils;
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
import org.jetbrains.annotations.NotNull;

/**
 * A quail entity to be bred and produce various resources
 */
public class QuailEntity extends ModAnimalEntity {

    public static final String ID = "quail";

    public static final String BREED_KEY = "Breed",
            ALLELE_A_KEY = "AlleleA",
            ALLELE_B_KEY = "AlleleB",
            TIMER_KEY = "EggLayTime";

    private static final Lazy<Integer> breedingTimeout = Lazy.of(QuailConfig.COMMON.quailBreedingTime);

    public static class Gene {
        public static final float LAY_AMOUNT_RANGE = 1.5f;
        public static final float LAY_TIME_RANGE = 1.5f;
        public static final float FECUNDITY_RANGE = 4f;
        public static final float LAY_AMOUNT_SIGMA = 0.08f;
        public static final float LAY_RANDOM_AMOUNT_SIGMA = 0.08f;
        public static final float LAY_TIME_SIGMA = 0.08f;
        public static final float LAY_RANDOM_TIME_SIGMA = 0.08f;
        public static final float FECUNDITY_SIGMA = 0.5f;
        public static final float DOMINANCE_SIGMA = 0.05f;

        public float layAmount;
        public float layRandomAmount;
        public float layTime;
        public float layRandomTime;
        public float fecundity;
        public float dominance;

        public Gene(RandomSource rand) {
            layAmount = rand.nextFloat() * LAY_AMOUNT_RANGE;
            layRandomAmount = rand.nextFloat();
            layTime = rand.nextFloat() * LAY_TIME_RANGE;
            layRandomTime = rand.nextFloat();
            fecundity = rand.nextFloat() * FECUNDITY_RANGE;
            dominance = rand.nextFloat();
        }

        public Gene() {
        }

        public Gene crossover(Gene other, RandomSource rand) {
            Gene child = new Gene();
            child.layAmount = Math.max(0, rand.nextBoolean() ? layAmount : other.layAmount + (float) rand.nextGaussian() * LAY_AMOUNT_SIGMA);
            child.layRandomAmount = Math.max(0, rand.nextBoolean() ? layRandomAmount : other.layRandomAmount + (float) rand.nextGaussian() * LAY_RANDOM_AMOUNT_SIGMA);
            child.layTime = Math.max(0, rand.nextBoolean() ? layTime : other.layTime + (float) rand.nextGaussian() * LAY_TIME_SIGMA);
            child.layRandomTime = Math.max(0, rand.nextBoolean() ? layRandomTime : other.layRandomTime + (float) rand.nextGaussian() * LAY_RANDOM_TIME_SIGMA);
            child.fecundity = Math.max(0, rand.nextBoolean() ? fecundity : other.fecundity + (float) rand.nextGaussian() * FECUNDITY_SIGMA);
            child.dominance = rand.nextBoolean() ? dominance : other.dominance + (float) rand.nextGaussian() * DOMINANCE_SIGMA;
            return child;
        }

        public Gene readFromTag(CompoundTag nbt) {
            layAmount = TagUtils.getOrDefault(nbt, "LayAmount", LAY_AMOUNT_RANGE / 2);
            layRandomAmount = TagUtils.getOrDefault(nbt, "LayRandomAmount", 1f / 2);
            layTime = TagUtils.getOrDefault(nbt, "LayTime", LAY_TIME_RANGE / 2);
            layRandomTime = TagUtils.getOrDefault(nbt, "LayRandomTime", 1f / 2);
            fecundity = TagUtils.getOrDefault(nbt, "Fecundity", 0.5f);
            dominance = TagUtils.getOrDefault(nbt, "Dominance", 0.5f);
            return this;
        }

        public CompoundTag writeToTag() {
            CompoundTag nbt = new CompoundTag();
            nbt.putFloat("LayAmount", layAmount);
            nbt.putFloat("LayRandomAmount", layRandomAmount);
            nbt.putFloat("LayTime", layTime);
            nbt.putFloat("LayRandomTime", layRandomTime);
            nbt.putFloat("Fecundity", fecundity);
            nbt.putFloat("Dominance", dominance);
            return nbt;
        }
    }

    private static final EntityDataAccessor<String> BREED_NAME = SynchedEntityData.defineId(QuailEntity.class, EntityDataSerializers.STRING);

    public static final Ingredient BREED_MATERIAL = Ingredient.of(Items.WHEAT_SEEDS, Items.PUMPKIN_SEEDS, Items.MELON_SEEDS, Items.BEETROOT_SEEDS);

    private Gene gene, alleleA, alleleB;
    private QuailType breed;
    private int layTimer;

    public float oFlap, oFlapSpeed, wingRotation, wingRotDelta = 1.0f, destPos;

    public QuailEntity(EntityType<? extends Animal> type, Level worldIn) {
        super(type, worldIn);
        setBreed(QuailType.PAINTED);
        randomBreed();
        setAlleles(new Gene(level().random), new Gene(level().random));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    public Gene getGene() {
        return gene;
    }

    private void layLoot() {
        this.spawnAtLocation(breed.getLoot(random, gene));
        this.gameEvent(GameEvent.ENTITY_PLACE);
    }

    public void setAlleles(Gene a, Gene b) {
        alleleA = a;
        alleleB = b;
        gene = alleleA.dominance >= alleleB.dominance ? alleleA : alleleB;
        resetTimer();
    }

    private void resetTimer() {
        layTimer = breed.layTime + random.nextInt(breed.layTime + 1);
        layTimer *= gene.layTime + random.nextFloat() * gene.layRandomTime;
        layTimer = Math.max(600, layTimer);
    }

    public String getBreedName() {
        return entityData.get(BREED_NAME);
    }

    public int getLayTimer() {
        return layTimer;
    }

    public void randomBreed() {
        switch (random.nextInt(4)) {
            case 0 -> breed = QuailType.PAINTED;
            case 1 -> breed = QuailType.BOBWHITE;
            case 2 -> breed = QuailType.BROWN;
            default -> breed = QuailType.ELEGANT;
        }
        entityData.set(BREED_NAME, breed.name, true);
    }

    private void setBreed(QuailType breed) {
        this.breed = breed;
        entityData.set(BREED_NAME, breed.name);
    }

    protected int getBreedingTimeout() {
        return breedingTimeout.get();
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor worldIn, @NotNull DifficultyInstance difficultyIn, @NotNull MobSpawnType reason, SpawnGroupData spawnDataIn, CompoundTag dataTag) {
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
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(BREED_NAME, QuailType.PAINTED.name);
    }

    @Override
    public QuailEntity getBreedOffspring(@NotNull ServerLevel world, @NotNull AgeableMob other) {
        QuailEntity child = ModEntities.QUAIL.get().create(world);
        if (child != null) {
            QuailEntity otherQuail = (QuailEntity) other;
            Gene childA = alleleA.crossover(alleleB, random);
            Gene childB = otherQuail.alleleA.crossover(otherQuail.alleleB, random);
            child.setAlleles(childA, childB);
            child.setBreed(breed.getOffspring(otherQuail.breed, random));
        }
        return child;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.oFlap = this.wingRotation;
        this.oFlapSpeed = this.destPos;
        this.destPos = (float) ((double) this.destPos + (double) (this.onGround() ? -1 : 2) * 0.3D);
        this.destPos = Mth.clamp(this.destPos, 0.0F, 1.0F);
        if (!this.onGround() && this.wingRotDelta < 1.0F) {
            this.wingRotDelta = 1.0F;
        }

        this.wingRotDelta = (float) ((double) this.wingRotDelta * 0.9D);
        Vec3 vector3d = this.getDeltaMovement();
        if (!this.onGround() && vector3d.y < 0.0D) {
            this.setDeltaMovement(vector3d.multiply(1.0D, 0.6D, 1.0D));
        }

        this.wingRotation += this.wingRotDelta * 2.0F;
        if (!level().isClientSide && isAlive() && !isBaby() && gene != null) {
            layTimer--;
            if (layTimer <= 0) {
                if (breed != null) {
                    resetTimer();
                    this.playSound(ModSounds.QUAIL_PLOP.get(), 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
                    layLoot();
                }
            }
        }
    }

    @Override
    public boolean isFood(@NotNull ItemStack stack) {
        return BREED_MATERIAL.test(stack);
    }

    @Override
    protected float getStandingEyeHeight(@NotNull Pose poseIn, @NotNull EntityDimensions sizeIn) {
        return this.isBaby() ? sizeIn.height * 0.85F : sizeIn.height * 0.92F;
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, @NotNull DamageSource source) {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.QUAIL_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return ModSounds.QUAIL_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.QUAIL_DIE.get();
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState blockIn) {
        this.playSound(ModSounds.QUAIL_STEP.get(), 0.15F, 1.0F);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains(BREED_KEY)) {
            setBreed(QuailType.Types.get(nbt.getString(BREED_KEY)));
        }
        if (nbt.contains(ALLELE_A_KEY))
            alleleA.readFromTag(nbt.getCompound(ALLELE_A_KEY));
        if (nbt.contains(ALLELE_B_KEY))
            alleleB.readFromTag(nbt.getCompound(ALLELE_B_KEY));
        setAlleles(alleleA, alleleB);
        layTimer = TagUtils.getOrDefault(nbt, TIMER_KEY, layTimer);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt(TIMER_KEY, layTimer);
        nbt.putString(BREED_KEY, breed.name);
        nbt.put(ALLELE_A_KEY, alleleA.writeToTag());
        nbt.put(ALLELE_B_KEY, alleleB.writeToTag());
    }

    @Override
    public void die(@NotNull DamageSource source) {
        if (net.minecraftforge.common.ForgeHooks.onLivingDeath(this, source)) return;
        if (this.isRemoved() || this.dead)
            return;
        super.die(source);
        if (breed.deathItem == null || breed.deathItem.equals("") || breed.deathAmount <= 0)
            return;
        int lootingLevel = ForgeHooks.getLootingLevel(this, source.getEntity(), source);
        int amount = breed.deathAmount + random.nextInt(Math.max(1, breed.deathAmount) + lootingLevel);
        Item dieItem = QuailType.getItem(breed.deathItem, random);
        if (dieItem != null)
            spawnAtLocation(new ItemStack(dieItem, amount));
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.getItem() == Items.BUCKET && !this.isBaby() && this.breed == QuailType.LEATHER) {
            player.playSound(ModSounds.QUAIL_MILK.get(), 1.0f, 1.0f);
            ItemStack leftover = ItemUtils.createFilledResult(itemStack, player, Items.MILK_BUCKET.getDefaultInstance());
            player.setItemInHand(hand, leftover);
            return InteractionResult.sidedSuccess(player.level().isClientSide());
        } else
            return super.mobInteract(player, hand);
    }
}
