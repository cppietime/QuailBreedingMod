package com.funguscow.crossbreed.item;

import com.funguscow.crossbreed.block.GeneticLeafBlock;
import com.funguscow.crossbreed.tileentities.GeneticTreeTileEntity;
import com.funguscow.crossbreed.worldgen.botany.TreeGene;
import com.funguscow.crossbreed.worldgen.botany.TreeSpecies;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class SwabItem extends Item {
    public SwabItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return pStack.getTagElement("Allele") != null;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        CompoundTag allele = pStack.getTagElement("Allele");
        if (allele != null) {
            pTooltipComponents.add(Component.translatable("text.breesources.stat.species", Component.translatable("text.breesources.species." + allele.getString("Species"))));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    @NotNull
    public InteractionResult useOn(UseOnContext pContext) {
        BlockPos position = pContext.getClickedPos();
        Level level = pContext.getLevel();
        Player player = Objects.requireNonNull(pContext.getPlayer());

        BlockState blockState = level.getBlockState(position);
        ItemStack itemStack = pContext.getItemInHand();
        RandomSource random = level.random;
        if (!(blockState.getBlock() instanceof LeavesBlock leaf)) {
            return super.useOn(pContext);
        }
        BlockEntity blockEntity = level.getBlockEntity(position);
        CompoundTag pollen = itemStack.getTagElement("Allele");
        if (blockEntity instanceof GeneticTreeTileEntity treeEntity) {
            // Modded leaves
            if (!(leaf instanceof GeneticLeafBlock)) {
                return InteractionResult.FAIL;
            }
            if (blockState.getValue(GeneticLeafBlock.POLLINATED)) {
                // Already pollinated, quit
                return InteractionResult.FAIL;
            }
            if (pollen == null) {
                // Sample leaves
                if (level.isClientSide()) {
                    player.playSound(SoundEvents.ITEM_PICKUP);
                    return InteractionResult.SUCCESS;
                }
                TreeGene gene = random.nextBoolean() ? treeEntity.getAlleleA() : treeEntity.getAlleleB();
                itemStack.addTagElement("Allele", gene.save());
            } else {
                // Attempt to pollinate leaves
                if (level.isClientSide()) {
                    if (!player.isCreative()) {
                        player.playSound(SoundEvents.ITEM_BREAK);
                    }
                    return InteractionResult.SUCCESS;
                }
                level.setBlock(position, blockState.setValue(GeneticLeafBlock.POLLINATED, Boolean.TRUE), Block.UPDATE_ALL | Block.UPDATE_KNOWN_SHAPE);
                TreeGene allele = TreeGene.of(pollen);
                treeEntity.pollinate(allele, random);
                itemStack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        } else {
            // Vanilla leaves
            TreeSpecies species = TreeSpecies.VanillaSpecies.get(blockState.getBlock());
            if (species == null) {
                return InteractionResult.PASS;
            }
            TreeGene defaultGene = species.defaultGene;
            if (pollen == null) {
                // Create pollen
                if (level.isClientSide()) {
                    player.playSound(SoundEvents.ITEM_PICKUP);
                    return InteractionResult.SUCCESS;
                }
                itemStack.addTagElement("Allele", defaultGene.save());
                return InteractionResult.SUCCESS;
            } else {
                ResourceLocation leafKey = species.leafBlock;
                Block leafBlock = Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(leafKey));
                level.setBlock(position,
                        leafBlock.defaultBlockState()
                                .trySetValue(GeneticLeafBlock.POLLINATED, Boolean.TRUE)
                                .trySetValue(LeavesBlock.DISTANCE, blockState.getValue(LeavesBlock.DISTANCE)),
                        Block.UPDATE_ALL | Block.UPDATE_KNOWN_SHAPE);
                BlockEntity newEntity = level.getBlockEntity(position);
                if (newEntity instanceof GeneticTreeTileEntity treeEntity) {
                    if (level.isClientSide()) {
                        if (!player.isCreative()) {
                            player.playSound(SoundEvents.ITEM_BREAK);
                        }
                        return InteractionResult.SUCCESS;
                    }
                    CompoundTag nbt = new CompoundTag();
                    treeEntity.saveAdditional(nbt);
                    nbt.put("AlleleA", defaultGene.save());
                    nbt.put("AlleleB", defaultGene.save());
                    treeEntity.load(nbt);
                    treeEntity.pollinate(TreeGene.of(pollen), random);
                    itemStack.shrink(1);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }
}
