package com.hexdragon.blacksmithre.mixin.block;

import com.hexdragon.blacksmithre.block.anvil.AnvilTileEntity;
import com.hexdragon.blacksmithre.registry.RegMain;
import net.minecraft.block.*;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AnvilBlock.class)
public abstract class AnvilBlockMixin extends FallingBlock implements IForgeBlock {
    public AnvilBlockMixin(Properties properties) {super(properties);}

    // 将铁砧的处理事件替换为 Mod 所提供的事件
    @Inject(method = "getContainer", at = @At(value = "HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void getContainer(BlockState state, World worldIn, BlockPos pos, CallbackInfoReturnable<INamedContainerProvider> cir) {
        cir.setReturnValue((INamedContainerProvider) worldIn.getTileEntity(pos));
        cir.cancel();
    }

    // 注册 TileEntity
    @Override public boolean hasTileEntity(BlockState state) {
        return true;
    }
    @Override public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        if (state.isIn(Blocks.ANVIL)) {
            return new AnvilTileEntity.PerfectAnvilTileEntity();
        } else if (state.isIn(Blocks.CHIPPED_ANVIL)) {
            return new AnvilTileEntity.ChippedAnvilTileEntity();
        } else if (state.isIn(Blocks.DAMAGED_ANVIL)) {
            return new AnvilTileEntity.DamagedAnvilTileEntity();
        } else if (state.isIn(RegMain.blockPerfectNetheriteAnvil.get())) {
            return new AnvilTileEntity.PerfectNetheriteAnvilTileEntity();
        } else if (state.isIn(RegMain.blockChippedNetheriteAnvil.get())) {
            return new AnvilTileEntity.ChippedNetheriteAnvilTileEntity();
        } else {
            return new AnvilTileEntity.DamagedNetheriteAnvilTileEntity();
        }
    }

    // 当破坏时掉落内容物
    @Override public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.isIn(newState.getBlock())) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof AnvilTileEntity)
                InventoryHelper.dropItems(worldIn, pos, ((AnvilTileEntity) tileentity).getItems());
            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    // 将铁砧的碰撞箱降低 1 像素
    @Shadow public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    private static final VoxelShape PART_BASE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
    private static final VoxelShape PART_LOWER_X = Block.makeCuboidShape(3.0D, 4.0D, 4.0D, 13.0D, 5.0D, 12.0D);
    private static final VoxelShape PART_MID_X = Block.makeCuboidShape(4.0D, 5.0D, 6.0D, 12.0D, 10.0D, 10.0D);
    private static final VoxelShape PART_UPPER_X = Block.makeCuboidShape(0.0D, 10.0D, 3.0D, 16.0D, 15.0D, 13.0D);
    private static final VoxelShape PART_LOWER_Z = Block.makeCuboidShape(4.0D, 4.0D, 3.0D, 12.0D, 5.0D, 13.0D);
    private static final VoxelShape PART_MID_Z = Block.makeCuboidShape(6.0D, 5.0D, 4.0D, 10.0D, 10.0D, 12.0D);
    private static final VoxelShape PART_UPPER_Z = Block.makeCuboidShape(3.0D, 10.0D, 0.0D, 13.0D, 15.0D, 16.0D);
    private static final VoxelShape X_AXIS_AABB = VoxelShapes.or(PART_BASE, PART_LOWER_X, PART_MID_X, PART_UPPER_X);
    private static final VoxelShape Z_AXIS_AABB = VoxelShapes.or(PART_BASE, PART_LOWER_Z, PART_MID_Z, PART_UPPER_Z);
    @Inject(method = "getShape", at = @At(value = "HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        cir.setReturnValue(state.get(FACING).getAxis() == Direction.Axis.X ? X_AXIS_AABB : Z_AXIS_AABB);
        cir.cancel();
    }

    // 添加 MATERIAL BlockState
    @Inject(method = "fillStateContainer", at = @At(value = "HEAD"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder, CallbackInfo cir) {
        builder.add(RegMain.blockStateMaterial);
    }

    // 设置下界合金砧的损坏
    @Inject(method = "damage", at = @At(value = "HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void damage(BlockState state, CallbackInfoReturnable<BlockState> cir) {
        if (state.isIn(RegMain.blockPerfectNetheriteAnvil.get())) {
            cir.setReturnValue(RegMain.blockChippedNetheriteAnvil.get().getDefaultState().with(FACING, state.get(FACING)));
            cir.cancel();
        } else if (state.isIn(RegMain.blockChippedNetheriteAnvil.get())) {
            cir.setReturnValue(RegMain.blockDamagedNetheriteAnvil.get().getDefaultState().with(FACING, state.get(FACING)));
            cir.cancel();
        } else if (state.isIn(RegMain.blockDamagedNetheriteAnvil.get())) {
            cir.setReturnValue(null);
            cir.cancel();
        }
    }

    // 设置红石比较器输出
    public boolean hasComparatorInputOverride(BlockState state) {return true;}
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {return Container.calcRedstone(worldIn.getTileEntity(pos));}


    // TODO : <验证> 多个玩家同时打开物品栏是否有刷物品 Bug 的可能？
    // TODO : [010] 绘制下界合金砧的材质（也可以修改原版铁砧的材质以让它有更大的区分度？）
    // TODO : [010] 开裂的下界合金砧等依然使用的原本模型

}
