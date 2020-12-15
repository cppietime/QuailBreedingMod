package com.funguscow.crossbreed.block;

import com.funguscow.crossbreed.tileentities.NestTileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.List;

public class NestBlock extends ContainerBlock {

    public NestBlock(Properties builder) {
        super(builder);
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new NestTileEntity();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if(tileEntity instanceof NestTileEntity){
            NestTileEntity nest = (NestTileEntity)tileEntity;
            return Math.min(15, nest.numQuails());
        }
        return 0;
    }


    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        if (!worldIn.isRemote && te instanceof NestTileEntity) {
            NestTileEntity nestTileEntity = (NestTileEntity) te;
            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0) {
                nestTileEntity.spawnQuails(worldIn);
                worldIn.updateComparatorOutputLevel(pos, this);
            }
        }
    }

    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!worldIn.isRemote && player.isCreative() && worldIn.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof NestTileEntity) {
                NestTileEntity nestTileEntity = (NestTileEntity) tileentity;
                ItemStack itemstack = new ItemStack(this);
                boolean flag = nestTileEntity.numQuails() > 0;
                if (!flag) {
                    return;
                }

                CompoundNBT compoundnbt = new CompoundNBT();
                compoundnbt.put("Quails", nestTileEntity.getQuails());
                itemstack.setTagInfo("BlockEntityTag", compoundnbt);

                ItemEntity itementity = new ItemEntity(worldIn, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), itemstack);
                itementity.setDefaultPickupDelay();
                worldIn.addEntity(itementity);
            }
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }

    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        Entity entity = builder.get(LootParameters.THIS_ENTITY);
        if (entity instanceof TNTEntity || entity instanceof CreeperEntity || entity instanceof WitherSkullEntity || entity instanceof WitherEntity || entity instanceof TNTMinecartEntity) {
            TileEntity tileentity = builder.get(LootParameters.BLOCK_ENTITY);
            if (tileentity instanceof NestTileEntity) {
                NestTileEntity nestTileEntity = (NestTileEntity)tileentity;
                nestTileEntity.spawnQuails(builder.getWorld());
            }
        }

        return super.getDrops(state, builder);
    }
}
