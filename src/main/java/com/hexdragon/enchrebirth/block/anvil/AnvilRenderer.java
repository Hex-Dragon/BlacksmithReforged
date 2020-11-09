package com.hexdragon.enchrebirth.block.anvil;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

// 渲染铁砧中存储的物品
@OnlyIn(Dist.CLIENT) public class AnvilRenderer extends TileEntityRenderer<AnvilTileEntity> {
    public AnvilRenderer(TileEntityRendererDispatcher rendererDispatcherIn) { super(rendererDispatcherIn); }

    @Override public void render(AnvilTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        // 渲染左侧的物品
        ItemStack stackLeft = tileEntityIn.getStackInSlot(0);
        if (!stackLeft.isEmpty()) {
            matrixStackIn.push();
            matrixStackIn.translate(0.7, 1.01, 0.52);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0F));
            matrixStackIn.rotate(Vector3f.ZN.rotationDegrees(5.0F));
            matrixStackIn.scale(0.375F, 0.375F, 0.375F);
            IBakedModel ibakedmodel = itemRenderer.getItemModelWithOverrides(stackLeft, tileEntityIn.getWorld(), null);
            itemRenderer.renderItem(stackLeft, ItemCameraTransforms.TransformType.FIXED, true, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ibakedmodel);
            matrixStackIn.pop();
        }

        // 渲染右侧的物品
        ItemStack stackRight = tileEntityIn.getStackInSlot(1);
        if (!stackRight.isEmpty()) {
            matrixStackIn.push();
            matrixStackIn.translate(0.27, 1.01, 0.45);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0F));
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(15.0F));
            matrixStackIn.scale(0.375F, 0.375F, 0.375F);
            IBakedModel ibakedmodel = itemRenderer.getItemModelWithOverrides(stackRight, tileEntityIn.getWorld(), null);
            itemRenderer.renderItem(stackRight, ItemCameraTransforms.TransformType.FIXED, true, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ibakedmodel);
            matrixStackIn.pop();
        }

    }
}