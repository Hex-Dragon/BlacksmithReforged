package com.hexdragon.enchrebirth.mixin.block;

import com.hexdragon.enchrebirth.block.anvil.AnvilTileEntity;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
        } else {
            return new AnvilTileEntity.DamagedAnvilTileEntity();
        }
    }

    // 将同步事件传递到客户端
    @Override public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
        super.eventReceived(state, worldIn, pos, id, param);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(id, param);
    }

    // 当破坏时掉落内容物
    @Override public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.isIn(newState.getBlock())) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof AnvilTileEntity) {
                InventoryHelper.dropItems(worldIn, pos, ((AnvilTileEntity) tileentity).getItems());
            }
            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

}
