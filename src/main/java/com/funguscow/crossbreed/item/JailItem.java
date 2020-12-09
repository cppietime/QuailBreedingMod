package com.funguscow.crossbreed.item;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.entity.QuailEntity;
import com.funguscow.crossbreed.init.ModEntities;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class JailItem extends Item {

    private boolean reusable;

    public JailItem(Properties properties, boolean reusable) {
        super(properties);
        this.reusable = reusable;
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        CompoundNBT jailTag = stack.getChildTag("Jailed");
        if(jailTag != null || !(target instanceof QuailEntity)){
            return ActionResultType.FAIL;
        }
        QuailEntity quail = (QuailEntity)target;
        jailTag = new CompoundNBT();
        quail.writeAdditional(jailTag);
        quail.remove(false);
        ItemStack jailed = new ItemStack(stack.getItem());
        jailed.setTagInfo("Jailed", jailTag);
        stack.shrink(1);
        if (!playerIn.addItemStackToInventory(jailed))
            playerIn.dropItem(jailed, false);
        return stack.isEmpty() ? ActionResultType.PASS : ActionResultType.func_233537_a_(playerIn.world.isRemote);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if(!(context.getWorld() instanceof ServerWorld))
            return ActionResultType.SUCCESS;
        ItemStack itemStack = context.getItem();
        PlayerEntity player = context.getPlayer();
        CompoundNBT jailTag = itemStack.getChildTag("Jailed");
        if(jailTag == null)
            return ActionResultType.FAIL;
        QuailEntity released = (QuailEntity)ModEntities.QUAIL.get().spawn((ServerWorld)context.getWorld(), itemStack, player, context.getPos().offset(context.getFace()), SpawnReason.SPAWN_EGG, true, false);
        released.readAdditional(jailTag);
        ItemStack emptied = new ItemStack(itemStack.getItem());
        itemStack.shrink(1);
        if(reusable && !player.isCreative()){
            emptied.removeChildTag("Jailed");
            if(!player.addItemStackToInventory(emptied))
                player.dropItem(emptied, false);
        }
        return itemStack.isEmpty() ? ActionResultType.PASS : ActionResultType.func_233537_a_(player.world.isRemote);
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        CompoundNBT jailTag = stack.getChildTag("Jailed");
        if(jailTag == null){
            tooltip.add(new TranslationTextComponent("text." + BreedMod.MODID + ".empty"));
        }
        else{
            String breed = jailTag.getString("Breed");
            tooltip.add(new TranslationTextComponent("text." + BreedMod.MODID + ".breed." + breed));
        }
        if(!reusable)
            tooltip.add(new TranslationTextComponent("text." + BreedMod.MODID + ".onetime"));
        else if(jailTag != null){
            CompoundNBT a = jailTag.getCompound("AlleleA"), b = jailTag.getCompound("AlleleB");
            CompoundNBT gene = a.getFloat("Dominance") >= b.getFloat("Dominance") ? a : b;
            tooltip.add(
                    new TranslationTextComponent("text." + BreedMod.MODID + ".stat.amount", gene.getFloat("LayAmount")));
            tooltip.add(
                    new TranslationTextComponent("text." + BreedMod.MODID + ".stat.amountRandom", gene.getFloat("LayRandomAmount")));
            tooltip.add(
                    new TranslationTextComponent("text." + BreedMod.MODID + ".stat.time", gene.getFloat("LayTime")));
            tooltip.add(
                    new TranslationTextComponent("text." + BreedMod.MODID + ".stat.timeRandom", gene.getFloat("LayRandomTime")));
            tooltip.add(
                    new TranslationTextComponent("text." + BreedMod.MODID + ".stat.eggTimer", jailTag.getInt("EggLayTime") / 1200f));
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return stack.getChildTag("Jailed") != null;
    }
}
