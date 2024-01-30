package com.funguscow.crossbreed.worldgen.botany.leaves;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.init.ModBlocks;
import com.funguscow.crossbreed.init.ModCreativeTabs;
import com.funguscow.crossbreed.tileentities.GeneticTreeTileEntity;
import com.funguscow.crossbreed.worldgen.botany.TreeGene;
import com.funguscow.crossbreed.worldgen.botany.TreeSpecies;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class GeneticLeafBlock extends LeavesBlock implements EntityBlock {

    public static final BooleanProperty POLLINATED = BooleanProperty.create("pollinated");

    public static final List<RegistryObject<Block>> Leaves = new ArrayList<>();
    public static final Set<String> LEAF_TYPES = Set.of(
            "test_leaves"
    );

    public GeneticLeafBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState().setValue(POLLINATED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(POLLINATED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new GeneticTreeTileEntity(pPos, pState);
    }

    @Override
    public void onRemove(BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pState.is(pNewState.getBlock())) {
            return;
        }

        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof GeneticTreeTileEntity tree) {
            TreeGene gene = tree.getGene();

            // Drop sapling and/or fruit, possibly.
            tryDropSapling(pLevel, pPos, tree);
            tryDropFruit(pLevel, pPos, gene);
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public void playerWillDestroy(@NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, Player pPlayer) {
        Item held = pPlayer.getMainHandItem().getItem();
        if (held == Items.SHEARS && !pPlayer.isCreative()) {
            // Drop leaf block
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof GeneticTreeTileEntity treeEntity) {
                tryDropLeaf(pLevel, pPos, treeEntity);
            }
            pLevel.removeBlockEntity(pPos);
        }

        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    protected void tryDropSapling(Level level, BlockPos pos, GeneticTreeTileEntity tree) {
        RandomSource random = level.random;
        TreeGene gene = tree.getGene();
        if (random.nextFloat() > gene.fertility) {
            return;
        }

        TreeSpecies species = gene.species();
        Block sapling = Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(species.sapling));
        ItemStack itemStack = new ItemStack(sapling);
        tree.saveToItem(itemStack);
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemStack);
    }

    protected void tryDropFruit(Level level, BlockPos pos, TreeGene gene) {
        RandomSource random = level.random;
        if (random.nextFloat() > gene.yield) {
            return;
        }

        String fruitItem = gene.fruitItem;
        if (fruitItem == null) {
            return;
        }

        ResourceLocation fruitLocation = new ResourceLocation(fruitItem);
        Item fruit = Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(fruitLocation));
        ItemStack itemStack = new ItemStack(fruit);
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemStack);
    }

    protected void tryDropLeaf(Level level, BlockPos pos, GeneticTreeTileEntity tree) {
        ItemStack itemStack = new ItemStack(level.getBlockState(pos).getBlock().asItem());
        tree.saveToItem(itemStack);
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemStack);
    }

    public static void registerItems() {
        Supplier<Block> supplier = () -> new GeneticLeafBlock(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES));
        for (String leafType : LEAF_TYPES) {
            Leaves.add(ModBlocks.registerBlockAndItem(leafType,
                    supplier,
                    block -> new BlockItem(block, new Item.Properties()),
                    Optional.of(ModCreativeTabs.QUAIL_MOD_TAB)));
        }
    }

    @Override
    public @NotNull List<ItemStack> onSheared(@Nullable Player player, @NotNull ItemStack item, Level level, BlockPos pos, int fortune) {
        BreedMod.LOGGER.debug("Shearing...");
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof GeneticTreeTileEntity treeEntity)) {
            return List.of();
        }
        BlockState state = level.getBlockState(pos);
        ItemStack stack = new ItemStack(state.getBlock());
        treeEntity.saveToItem(stack);
        return List.of(stack);
    }
}
