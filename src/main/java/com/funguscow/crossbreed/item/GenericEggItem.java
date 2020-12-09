package com.funguscow.crossbreed.item;

import com.funguscow.crossbreed.entity.GenericEggEntity;
import com.funguscow.crossbreed.init.ModEntities;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class GenericEggItem extends Item {

    private static final List<GenericEggItem> EGG_QUEUE = new ArrayList<>();

    private int spawnChance;
    private int multiSpawnChance;
    private String animal;
    private String itemID;

    public GenericEggItem(Properties properties, int spawnChance, int multiSpawnChance, String animal, String itemID) {
        super(properties);
        this.spawnChance = spawnChance;
        this.multiSpawnChance = multiSpawnChance;
        this.animal = animal;
        this.itemID = itemID;
        EGG_QUEUE.add(this);
    }

    public void updateOdds(int chance, int multi){
        spawnChance = chance;
        multiSpawnChance = multi;
    }

    public int getSpawnChance(){
        return spawnChance;
    }

    public int getMultiSpawnChance(){
        return multiSpawnChance;
    }

    /**
     * Throw the egg
     * @param worldIn
     * @param playerIn
     * @param handIn
     * @return
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        worldIn.playSound((PlayerEntity)null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
        if (!worldIn.isRemote) {
            GenericEggEntity eggentity = ModEntities.GENERIC_EGG.get().create(worldIn).setEgg(itemID, spawnChance, multiSpawnChance, animal);
            eggentity.setItem(itemstack);
            eggentity.setPosition(playerIn.getPosX(), playerIn.getPosYEye() - 0.1, playerIn.getPosZ());
            eggentity.func_234612_a_(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
            worldIn.addEntity(eggentity);
        }

        playerIn.addStat(Stats.ITEM_USED.get(this));
        if (!playerIn.abilities.isCreativeMode) {
            itemstack.shrink(1);
        }

        return ActionResult.func_233538_a_(itemstack, worldIn.isRemote());
    }

    public static void registerDispenser(){
        ProjectileDispenseBehavior behavior = new ProjectileDispenseBehavior(){
            @Override
            protected ProjectileEntity getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
                GenericEggItem eggItem = (GenericEggItem)stackIn.getItem();
                GenericEggEntity entity = ModEntities.GENERIC_EGG.get().create(worldIn).setEgg(eggItem.itemID, eggItem.spawnChance, eggItem.multiSpawnChance, eggItem.animal);
                entity.setPosition(position.getX(), position.getY(), position.getZ());
                return entity;
            }
        };
        for(GenericEggItem egg : EGG_QUEUE){
            DispenserBlock.registerDispenseBehavior(egg, behavior);
        }
    }
}
