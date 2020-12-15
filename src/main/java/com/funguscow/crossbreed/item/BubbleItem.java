package com.funguscow.crossbreed.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BubbleItem extends Item {

    private final Fluid fluid;

    public BubbleItem(Properties properties, Fluid fluid) {
        super(properties);
        this.fluid = fluid;
    }

    /**
     * Place the fluid
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        BlockRayTraceResult raytraceresult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.NONE);
        ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onBucketUse(playerIn, worldIn, itemstack, raytraceresult);
        if (ret != null) return ret;
        if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
            return ActionResult.resultPass(itemstack);
        } else if (raytraceresult.getType() != RayTraceResult.Type.BLOCK) {
            return ActionResult.resultPass(itemstack);
        } else {
            BlockPos blockpos = raytraceresult.getPos();
            Direction direction = raytraceresult.getFace();
            BlockPos blockpos1 = blockpos.offset(direction);
            if (worldIn.isBlockModifiable(playerIn, blockpos) && playerIn.canPlayerEdit(blockpos1, direction, itemstack)) {
                    BlockState blockstate = worldIn.getBlockState(blockpos);
                    BlockPos blockpos2 = canBlockContainFluid(worldIn, blockpos, blockstate) ? blockpos : blockpos1;
                    if (this.tryPlaceContainedLiquid(playerIn, worldIn, blockpos2, raytraceresult)) {
                        if (playerIn instanceof ServerPlayerEntity) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)playerIn, blockpos2, itemstack);
                        }

                        playerIn.addStat(Stats.ITEM_USED.get(this));
                        if(!playerIn.isCreative())
                            itemstack.shrink(1);
                        return ActionResult.func_233538_a_(itemstack, worldIn.isRemote());
                    } else {
                        return ActionResult.resultFail(itemstack);
                    }
            } else {
                return ActionResult.resultFail(itemstack);
            }
        }
    }

    public int getBurnTime(ItemStack stack){
        return fluid == Fluids.LAVA ? 20_000 : 0;
    }

    public boolean tryPlaceContainedLiquid(@Nullable PlayerEntity player, World worldIn, BlockPos posIn, @Nullable BlockRayTraceResult rayTrace) {
        if (!(this.fluid instanceof FlowingFluid)) {
            return false;
        } else {
            BlockState blockstate = worldIn.getBlockState(posIn);
            Block block = blockstate.getBlock();
            Material material = blockstate.getMaterial();
            boolean flag = blockstate.isReplaceable(this.fluid);
            boolean flag1 = blockstate.isAir() || flag || block instanceof ILiquidContainer && ((ILiquidContainer)block).canContainFluid(worldIn, posIn, blockstate, this.fluid);
            if (!flag1) {
                return rayTrace != null && this.tryPlaceContainedLiquid(player, worldIn, rayTrace.getPos().offset(rayTrace.getFace()), null);
            } else if (worldIn.getDimensionType().isUltrawarm() && this.fluid.isIn(FluidTags.WATER)) {
                int i = posIn.getX();
                int j = posIn.getY();
                int k = posIn.getZ();
                worldIn.playSound(player, posIn, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

                for(int l = 0; l < 8; ++l) {
                    worldIn.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0D, 0.0D, 0.0D);
                }

                return true;
            } else if (block instanceof ILiquidContainer && ((ILiquidContainer)block).canContainFluid(worldIn,posIn,blockstate,fluid)) {
                ((ILiquidContainer)block).receiveFluid(worldIn, posIn, blockstate, ((FlowingFluid)this.fluid).getStillFluidState(false));
                this.playEmptySound(player, worldIn, posIn);
                return true;
            } else {
                if (!worldIn.isRemote && flag && !material.isLiquid()) {
                    worldIn.destroyBlock(posIn, true);
                }

                if (!worldIn.setBlockState(posIn, this.fluid.getDefaultState().getBlockState(), 11) && !blockstate.getFluidState().isSource()) {
                    return false;
                } else {
                    this.playEmptySound(player, worldIn, posIn);
                    return true;
                }
            }
        }
    }

    protected void playEmptySound(@Nullable PlayerEntity player, IWorld worldIn, BlockPos pos) {
        SoundEvent soundevent = this.fluid.getAttributes().getEmptySound();
        if(soundevent == null) soundevent = this.fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
        worldIn.playSound(player, pos, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    private boolean canBlockContainFluid(World worldIn, BlockPos posIn, BlockState blockstate)
    {
        return blockstate.getBlock() instanceof ILiquidContainer && ((ILiquidContainer)blockstate.getBlock()).canContainFluid(worldIn, posIn, blockstate, this.fluid);
    }
}
