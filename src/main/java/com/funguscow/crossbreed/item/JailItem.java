package com.funguscow.crossbreed.item;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.config.QuailConfig;
import com.funguscow.crossbreed.entity.QuailEntity;
import com.funguscow.crossbreed.init.ModEntities;
import com.funguscow.crossbreed.tileentities.NestTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
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

    public static final String JAILED_TAG_KEY = "Jailed";

    private final boolean reusable;

    public JailItem(Properties properties, boolean reusable) {
        super(properties);
        this.reusable = reusable;
    }

    public boolean isReusable() {
        return reusable;
    }

    public ItemStack captureQuail(ItemStack stack, QuailEntity quail) {
        CompoundTag jailTag = new CompoundTag();
        quail.addAdditionalSaveData(jailTag);
        quail.playSound(SoundEvents.ITEM_PICKUP, 1.0f, 1.0f);
        quail.remove(Entity.RemovalReason.DISCARDED);
        ItemStack jailed = new ItemStack(stack.getItem());
        jailed.addTagElement(JAILED_TAG_KEY, jailTag);
        return jailed;
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, @NotNull Player playerIn, @NotNull LivingEntity target, @NotNull InteractionHand hand) {
        CompoundTag jailTag = stack.getTagElement(JAILED_TAG_KEY);
        if(jailTag != null || !(target instanceof QuailEntity quail)){
            return InteractionResult.FAIL;
        }
        ItemStack jailed = captureQuail(stack, quail);
        stack.shrink(1);
        if (!playerIn.addItem(jailed))
            playerIn.drop(jailed, false);
        return stack.isEmpty() ? InteractionResult.PASS : InteractionResult.sidedSuccess(playerIn.level().isClientSide());
    }

    public boolean releaseQuail(Level world, BlockPos pos, ItemStack itemStack, Player player) {
        CompoundTag jailTag = itemStack.getTagElement(JAILED_TAG_KEY);
        if (jailTag == null)
            return false;
        QuailEntity released = (QuailEntity) ModEntities.QUAIL.get().spawn((ServerLevel) world, itemStack, player, pos, MobSpawnType.SPAWN_EGG, true, false);
        if(released != null)
            released.readAdditionalSaveData(jailTag);
        return true;
    }

    public boolean depositQuail(ItemStack itemStack, NestTileEntity nestEntity) {
        CompoundTag jailTag = itemStack.getTagElement(JAILED_TAG_KEY);
        assert jailTag != null;
        if(jailTag.getInt("Age") < 0 || nestEntity.numQuails() >= QuailConfig.COMMON.maxQuailsInNest.get()) // If a baby or full
            return false;
        nestEntity.putQuail(jailTag);
        return true;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        ItemStack itemStack = context.getItemInHand();

        Player player = context.getPlayer();
        if(player == null) {
            return InteractionResult.PASS;
        }

        CompoundTag jailTag = itemStack.getTagElement(JAILED_TAG_KEY);
        BlockPos clickedPos = context.getClickedPos();
        BlockEntity tileEntity = world.getBlockEntity(clickedPos);

        if(world.isClientSide()) {
            if (tileEntity instanceof NestTileEntity nest) {
                if (jailTag == null && nest.numQuails() > 0) {
                    player.playSound(SoundEvents.ITEM_PICKUP, 1.0f, 1.0f);
                } else if (jailTag != null && nest.numQuails() < QuailConfig.COMMON.maxQuailsInNest.get()) {
                    if (reusable || player.isCreative()) {
                        player.playSound(SoundEvents.ITEM_PICKUP, 1.0f, 1.0f);
                    } else {
                        player.playSound(SoundEvents.ITEM_BREAK, 0.8f, 0.8f);
                    }
                }
            } else if (jailTag != null){
                if (reusable || player.isCreative()) {
                    player.playSound(SoundEvents.ITEM_PICKUP, 1.0f, 1.0f);
                } else {
                    player.playSound(SoundEvents.ITEM_BREAK, 0.8f, 0.8f);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if(!(tileEntity instanceof NestTileEntity nestEntity)) { // When using to release a quail
            BlockPos spawnPos = clickedPos.relative(context.getClickedFace());
            if (!releaseQuail(world, spawnPos, itemStack, player)) {
                return InteractionResult.PASS;
            }
            ItemStack emptied = new ItemStack(itemStack.getItem());
            itemStack.shrink(1);
            if (reusable && !player.isCreative()) {
                emptied.removeTagKey(JAILED_TAG_KEY);
                if (!player.addItem(emptied))
                    player.drop(emptied, false);
            }
        }
        else{ // When using on a nest
            if(jailTag == null){ // Withdraw quail
                CompoundTag quail = nestEntity.getQuail();
                if(quail == null) // Empty so just quit
                    return InteractionResult.PASS;
                ItemStack jailed = new ItemStack(itemStack.getItem());
                jailed.addTagElement(JAILED_TAG_KEY, quail);
                if(!player.isCreative())
                    itemStack.shrink(1);
                if (!player.addItem(jailed))
                    player.drop(jailed, false);
            }
            else{ // Deposit a quail
                if (!depositQuail(itemStack, nestEntity)) {
                    return InteractionResult.PASS;
                }
                ItemStack emptied = new ItemStack(itemStack.getItem());
                itemStack.shrink(1);
                if (reusable && !player.isCreative()) {
                    emptied.removeTagKey(JAILED_TAG_KEY);
                    if (!player.addItem(emptied))
                        player.drop(emptied, false);
                }
            }
        }
        return itemStack.isEmpty() ? InteractionResult.PASS : InteractionResult.sidedSuccess(player.level().isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        CompoundTag jailTag = stack.getTagElement(JAILED_TAG_KEY);
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
                    translatable("text." + BreedMod.MODID + ".stat.fecundity", gene.getFloat("Fecundity")));
            int seconds = jailTag.getInt("EggLayTime") / 20;
            tooltip.add(
                    translatable("text." + BreedMod.MODID + ".stat.eggTimer", String.format("%d:%02d", seconds / 60, seconds % 60)));
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getTagElement(JAILED_TAG_KEY) != null;
    }
}
