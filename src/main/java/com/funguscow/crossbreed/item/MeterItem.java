package com.funguscow.crossbreed.item;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.entity.QuailEntity;
import com.funguscow.crossbreed.tileentities.NestTileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class MeterItem extends Item {

    public MeterItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        if(playerIn.world.isRemote)
            return ActionResultType.FAIL;
        if(!(target instanceof QuailEntity))
            return ActionResultType.FAIL;
        QuailEntity quail = (QuailEntity)target;
        QuailEntity.Gene gene = quail.getGene();
        playerIn.sendMessage(
                new TranslationTextComponent("text." + BreedMod.MODID + ".breed." + quail.getBreedName()),
                Util.DUMMY_UUID
        );
        playerIn.sendMessage(
                new TranslationTextComponent("text." + BreedMod.MODID + ".stat.amount", gene.layAmount),
                Util.DUMMY_UUID);
        playerIn.sendMessage(
                new TranslationTextComponent("text." + BreedMod.MODID + ".stat.amountRandom", gene.layRandomAmount),
                Util.DUMMY_UUID);
        playerIn.sendMessage(
                new TranslationTextComponent("text." + BreedMod.MODID + ".stat.time", gene.layTime),
                Util.DUMMY_UUID);
        playerIn.sendMessage(
                new TranslationTextComponent("text." + BreedMod.MODID + ".stat.timeRandom", gene.layRandomTime),
                Util.DUMMY_UUID);
        playerIn.sendMessage(
                new TranslationTextComponent("text." + BreedMod.MODID + ".stat.eggTimer", quail.getLayTimer() / 1200f),
                Util.DUMMY_UUID);
        return ActionResultType.PASS;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        BlockPos pos = context.getPos();
        World world = context.getWorld();
        if(world.isRemote)
            return ActionResultType.FAIL;
        TileEntity entity = world.getTileEntity(pos);
        if(!(entity instanceof NestTileEntity))
            return ActionResultType.FAIL;
        NestTileEntity nest = (NestTileEntity)entity;
        nest.printQuails(context.getPlayer());
        return ActionResultType.FAIL;
    }
}
