package com.funguscow.crossbreed.item;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.config.QuailConfig;
import com.funguscow.crossbreed.entity.QuailEntity;
import com.funguscow.crossbreed.init.ModEntities;
import com.funguscow.crossbreed.tileentities.NestTileEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.minecraft.network.chat.Component.translatable;

public class JailItem extends Item {

    private final boolean reusable;

    public JailItem(Properties properties, boolean reusable) {
        super(properties);
        this.reusable = reusable;
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, @NotNull Player playerIn, @NotNull LivingEntity target, @NotNull InteractionHand hand) {
        CompoundTag jailTag = stack.getTagElement("Jailed");
        if(jailTag != null || !(target instanceof QuailEntity)){
            return InteractionResult.FAIL;
        }
        QuailEntity quail = (QuailEntity)target;
        jailTag = new CompoundTag();
        quail.addAdditionalSaveData(jailTag);
        quail.remove(Entity.RemovalReason.DISCARDED);
        ItemStack jailed = new ItemStack(stack.getItem());
        jailed.addTagElement("Jailed", jailTag);
        stack.shrink(1);
        if (!playerIn.addItem(jailed))
            playerIn.drop(jailed, false);
        return stack.isEmpty() ? InteractionResult.PASS : InteractionResult.sidedSuccess(playerIn.level().isClientSide());
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if(world.isClientSide())
            return InteractionResult.SUCCESS;
        ItemStack itemStack = context.getItemInHand();
        Player player = context.getPlayer();
        if(player == null)
            return InteractionResult.PASS;
        CompoundTag jailTag = itemStack.getTagElement("Jailed");
        BlockEntity tileEntity = world.getBlockEntity(context.getClickedPos());
        if(!(tileEntity instanceof NestTileEntity)) { // When using to release a quail
            if (jailTag == null)
                return InteractionResult.PASS;
            QuailEntity released = (QuailEntity) ModEntities.QUAIL.get().spawn((ServerLevel) world, itemStack, player, context.getClickedPos().relative(context.getClickedFace()), MobSpawnType.SPAWN_EGG, true, false);
            if(released != null)
                released.readAdditionalSaveData(jailTag);
            ItemStack emptied = new ItemStack(itemStack.getItem());
            itemStack.shrink(1);
            if (reusable && !player.isCreative()) {
                emptied.removeTagKey("Jailed");
                if (!player.addItem(emptied))
                    player.drop(emptied, false);
            }
            return itemStack.isEmpty() ? InteractionResult.PASS : InteractionResult.sidedSuccess(player.level().isClientSide());
        }
        else{ // When using on a nest
            NestTileEntity nestEntity = (NestTileEntity)tileEntity;
            if(jailTag == null){ // Withdraw quail
                CompoundTag quail = nestEntity.getQuail();
                if(quail == null) // Empty so just quit
                    return InteractionResult.PASS;
                ItemStack jailed = new ItemStack(itemStack.getItem());
                jailed.addTagElement("Jailed", quail);
                if(!player.isCreative())
                    itemStack.shrink(1);
                if (!player.addItem(jailed))
                    player.drop(jailed, false);
                return itemStack.isEmpty() ? InteractionResult.PASS : InteractionResult.sidedSuccess(player.level().isClientSide());
            }
            else{ // Deposit a quail
                if(jailTag.getInt("Age") < 0 || nestEntity.numQuails() >= QuailConfig.COMMON.maxQuailsInNest.get()) // If a baby or full
                    return InteractionResult.PASS;
                nestEntity.putQuail(jailTag);
                ItemStack emptied = new ItemStack(itemStack.getItem());
                itemStack.shrink(1);
                if (reusable && !player.isCreative()) {
                    emptied.removeTagKey("Jailed");
                    if (!player.addItem(emptied))
                        player.drop(emptied, false);
                }
                return itemStack.isEmpty() ? InteractionResult.PASS : InteractionResult.sidedSuccess(player.level().isClientSide());
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        CompoundTag jailTag = stack.getTagElement("Jailed");
        if(jailTag == null){
            tooltip.add(translatable("text." + BreedMod.MODID + ".empty"));
        }
        else{
            String breed = jailTag.getString("Breed");
            tooltip.add(translatable("text." + BreedMod.MODID + ".breed." + breed));
        }
        if(!reusable)
            tooltip.add(translatable("text." + BreedMod.MODID + ".onetime"));
        else if(jailTag != null){
            CompoundTag a = jailTag.getCompound("AlleleA"), b = jailTag.getCompound("AlleleB");
            CompoundTag gene = a.getFloat("Dominance") >= b.getFloat("Dominance") ? a : b;
            tooltip.add(
                    translatable("text." + BreedMod.MODID + ".stat.amount", gene.getFloat("LayAmount")));
            tooltip.add(
                    translatable("text." + BreedMod.MODID + ".stat.amountRandom", gene.getFloat("LayRandomAmount")));
            tooltip.add(
                    translatable("text." + BreedMod.MODID + ".stat.time", gene.getFloat("LayTime")));
            tooltip.add(
                    translatable("text." + BreedMod.MODID + ".stat.timeRandom", gene.getFloat("LayRandomTime")));
            tooltip.add(
                    translatable("text." + BreedMod.MODID + ".stat.eggTimer", jailTag.getInt("EggLayTime") / 1200f));
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getTagElement("Jailed") != null;
    }
}
