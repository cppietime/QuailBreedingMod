package com.funguscow.crossbreed.block;

import com.funguscow.crossbreed.config.QuailConfig;
import com.funguscow.crossbreed.init.ModTileEntities;
import com.funguscow.crossbreed.tileentities.NestTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NestBlock extends BaseEntityBlock {

    private static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    public NestBlock(Properties builder) {
        super(builder);
        registerDefaultState(getStateDefinition().any().setValue(TRIGGERED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TRIGGERED);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new NestTileEntity(pos, state);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState blockState, Level worldIn, @NotNull BlockPos pos) {
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);
        if (tileEntity instanceof NestTileEntity) {
            NestTileEntity nest = (NestTileEntity) tileEntity;
            int numQuails = nest.numQuails();
            if (numQuails == 0) {
                return 0;
            }
            int ratio = (numQuails * 15) / QuailConfig.COMMON.maxQuailsInNest.get();
            return Math.max(ratio, 1);
        }
        return 0;
    }


    @Override
    public void playerDestroy(@NotNull Level worldIn, @NotNull Player player, @NotNull BlockPos pos, @NotNull BlockState state, BlockEntity te, @NotNull ItemStack stack) {
        super.playerDestroy(worldIn, player, pos, state, te, stack);
        if (!worldIn.isClientSide() && te instanceof NestTileEntity) {
            NestTileEntity nestTileEntity = (NestTileEntity) te;
            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0) {
                nestTileEntity.spawnQuails(worldIn);
                worldIn.updateNeighbourForOutputSignal(pos, this);
            }
        }
    }

    public void playerWillDestroy(Level worldIn, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        if (!worldIn.isClientSide && player.isCreative() && worldIn.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            BlockEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof NestTileEntity) {
                NestTileEntity nestTileEntity = (NestTileEntity) tileentity;
                ItemStack itemstack = new ItemStack(this);
                boolean flag = nestTileEntity.numQuails() > 0;
                if (!flag) {
                    return;
                }

                CompoundTag compoundnbt = new CompoundTag();
                compoundnbt.put("Quails", nestTileEntity.getQuails());
                itemstack.addTagElement("BlockEntityTag", compoundnbt);

                ItemEntity itementity = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), itemstack);
                itementity.setDefaultPickUpDelay();
                worldIn.addFreshEntity(itementity);
            }
        }

        super.playerWillDestroy(worldIn, pos, state, player);
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.Builder builder) {
        Entity entity = builder.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (entity instanceof PrimedTnt || entity instanceof Creeper || entity instanceof WitherSkull || entity instanceof WitherBoss || entity instanceof MinecartTNT) {
            BlockEntity tileentity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
            if (tileentity instanceof NestTileEntity) {
                NestTileEntity nestTileEntity = (NestTileEntity) tileentity;
                nestTileEntity.spawnQuails(builder.getLevel());
            }
        }

        return super.getDrops(state, builder);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos neighborPos, boolean isMoving) {
        boolean powered = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
        boolean triggered = state.getValue(TRIGGERED);
        if (powered && !triggered) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof NestTileEntity) {
                NestTileEntity nestEntity = (NestTileEntity) entity;
//                nestEntity.breedOne(level.random);
                nestEntity.killOne(level);
            }
            level.setBlock(pos, state.setValue(TRIGGERED, true), 4);
        } else if (!powered && triggered) {
            level.setBlock(pos, state.setValue(TRIGGERED, false), 4);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return (pBlockEntityType == ModTileEntities.QUAIL_NEST.get()) ? NestTileEntity::tick : null;
    }
}
