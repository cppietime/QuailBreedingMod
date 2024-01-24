package com.funguscow.crossbreed.item;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.entity.QuailEntity;
import com.funguscow.crossbreed.tileentities.NestTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.network.chat.Component.translatable;

public class MeterItem extends Item {

    public MeterItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, Player playerIn, @NotNull LivingEntity target, @NotNull InteractionHand hand) {
        if (playerIn.level().isClientSide())
            return InteractionResult.FAIL;
        if (!(target instanceof QuailEntity))
            return InteractionResult.FAIL;
        QuailEntity quail = (QuailEntity) target;
        QuailEntity.Gene gene = quail.getGene();
        int seconds = quail.getLayTimer() / 20;
        MutableComponent component =
                translatable("text." + BreedMod.MODID + ".breed." + quail.getBreedName())
                        .append(CommonComponents.NEW_LINE)
                        .append(
                                translatable("text." + BreedMod.MODID + ".stat.amount", String.format("%.3f", gene.layAmount)))
                        .append(CommonComponents.NEW_LINE)
                        .append(
                                translatable("text." + BreedMod.MODID + ".stat.amountRandom", String.format("%.3f", gene.layRandomAmount)))
                        .append(CommonComponents.NEW_LINE)
                        .append(
                                translatable("text." + BreedMod.MODID + ".stat.time", String.format("%.3f", gene.layTime)))
                        .append(CommonComponents.NEW_LINE)
                        .append(
                                translatable("text." + BreedMod.MODID + ".stat.timeRandom", String.format("%.3f", gene.layRandomTime)))
                        .append(CommonComponents.NEW_LINE)
                        .append(
                                translatable("text." + BreedMod.MODID + ".stat.fecundity", String.format("%.1f", gene.fecundity)))
                        .append(CommonComponents.NEW_LINE)
                        .append(
                                translatable("text." + BreedMod.MODID + ".stat.eggTimer", String.format("%d:%02d", seconds / 60, seconds % 60)));
        playerIn.displayClientMessage(component, false);
        return InteractionResult.PASS;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        if (world.isClientSide())
            return InteractionResult.FAIL;
        BlockEntity entity = world.getBlockEntity(pos);
        if (!(entity instanceof NestTileEntity))
            return InteractionResult.FAIL;
        NestTileEntity nest = (NestTileEntity) entity;
        nest.printQuails(context.getPlayer());
        return InteractionResult.FAIL;
    }
}
