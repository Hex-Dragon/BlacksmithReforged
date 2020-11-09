package com.hexdragon.enchrebirth.mixin.block;

import com.hexdragon.enchrebirth.block.AnvilContainerRe;
import com.hexdragon.enchrebirth.block.AnvilTileEntity;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
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
        cir.setReturnValue(new SimpleNamedContainerProvider(
                (id, inventory, player) -> new AnvilContainerRe(id, inventory, IWorldPosCallable.of(worldIn, pos)),
                new TranslationTextComponent("block.minecraft.anvil")));
        cir.cancel();
    }

    // 注册 TileEntity
    @Override public boolean hasTileEntity(BlockState state) {
        return true;
    }
    @Override public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new AnvilTileEntity();
    }

}
