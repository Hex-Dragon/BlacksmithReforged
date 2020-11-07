package com.hexdragon.enchrebirth.mixin;

import com.hexdragon.enchrebirth.block.GrindstoneContainerRe;
import net.minecraft.block.BlockState;
import net.minecraft.block.GrindstoneBlock;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GrindstoneBlock.class)
public abstract class GrindstoneBlockMixin extends HorizontalFaceBlock {
    public GrindstoneBlockMixin(Properties builder) {
        super(builder);
    }

    // 将砂轮的处理事件替换为 Mod 所提供的事件
    @Inject(method = "getContainer", at = @At(value = "HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void getContainer(BlockState state, World worldIn, BlockPos pos, CallbackInfoReturnable<INamedContainerProvider> cir) {
        cir.setReturnValue(new SimpleNamedContainerProvider(
                (id, inventory, player) -> new GrindstoneContainerRe(id, inventory, IWorldPosCallable.of(worldIn, pos)),
                new TranslationTextComponent("block.minecraft.grindstone")));
        cir.cancel();
    }

}
