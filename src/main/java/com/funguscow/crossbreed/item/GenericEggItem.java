package com.funguscow.crossbreed.item;

import com.funguscow.crossbreed.entity.GenericEggEntity;
import com.funguscow.crossbreed.init.ModEntities;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GenericEggItem extends Item {

    private static final List<GenericEggItem> EGG_QUEUE = new ArrayList<>();

    private int spawnChance;
    private int multiSpawnChance;
    private final String animal;
    private final String itemID;
    private final Random random = new Random();

    public GenericEggItem(Properties properties, int spawnChance, int multiSpawnChance, String animal, String itemID) {
        super(properties);
        this.spawnChance = spawnChance;
        this.multiSpawnChance = multiSpawnChance;
        this.animal = animal;
        this.itemID = itemID;
        EGG_QUEUE.add(this);
    }

    public void updateOdds(int chance, int multi) {
        spawnChance = chance;
        multiSpawnChance = multi;
    }

    /**
     * Throw the egg
     */
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @NotNull InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.EGG_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
        if (!worldIn.isClientSide()) {
            GenericEggEntity eggEntity = new GenericEggEntity(worldIn, playerIn)
                    .setEgg(itemID, spawnChance, multiSpawnChance, animal);
            eggEntity.setItem(itemstack);
            eggEntity.shootFromRotation(playerIn, playerIn.getXRot(), playerIn.getYRot(), 0.0F, 1.5F, 1.0F);
            worldIn.addFreshEntity(eggEntity);
        }

        playerIn.awardStat(Stats.ITEM_USED.get(this));
        if (!playerIn.isCreative()) {
            itemstack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, worldIn.isClientSide()); // Consume or success?
    }

    public static void registerDispenser() {
        DispenseItemBehavior behavior = new AbstractProjectileDispenseBehavior() {
            @Override
            protected @NotNull Projectile getProjectile(@NotNull Level worldIn, @NotNull Position position, ItemStack itemStack) {
                GenericEggItem eggItem = (GenericEggItem) itemStack.getItem();
                GenericEggEntity entity = ModEntities.GENERIC_EGG.get().create(worldIn);
                assert (entity != null);
                entity.setEgg(eggItem.itemID, eggItem.spawnChance, eggItem.multiSpawnChance, eggItem.animal);
                entity.setItem(itemStack);
                entity.setPos(position.x(), position.y(), position.z());
                return entity;
            }
        };
        for (GenericEggItem egg : EGG_QUEUE) {
            DispenserBlock.registerBehavior(egg, behavior);
        }
    }
}
