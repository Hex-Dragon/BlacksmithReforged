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
import net.minecraft.util.math.vector.Quaternion;

// 渲染铁砧中存储的物品
public class AnvilRenderer extends TileEntityRenderer<AnvilTileEntity> {
    public AnvilRenderer(TileEntityRendererDispatcher rendererDispatcherIn) { super(rendererDispatcherIn); }

    @Override public void render(AnvilTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        AnvilContainerRe container = (AnvilContainerRe) tileEntityIn.container;
        if (container == null) return;
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        // 渲染左侧的物品
        ItemStack stackLeft = container.inputInventory.getStackInSlot(0);
        if (!stackLeft.isEmpty()) {
            matrixStackIn.push();
            matrixStackIn.translate(2, 1.01, 0);
            IBakedModel ibakedmodel = itemRenderer.getItemModelWithOverrides(stackLeft, tileEntityIn.getWorld(), null);
            itemRenderer.renderItem(stackLeft, ItemCameraTransforms.TransformType.FIXED, true, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ibakedmodel);
            matrixStackIn.pop();
        }

        // 渲染右侧的物品
        ItemStack stackRight = container.inputInventory.getStackInSlot(1);
        if (!stackRight.isEmpty()) {
            matrixStackIn.push();
            matrixStackIn.translate(0, 1.01, 0);
            matrixStackIn.rotate(new Quaternion(0, 1, 1, (float) Math.PI));
            IBakedModel ibakedmodel = itemRenderer.getItemModelWithOverrides(stackRight, tileEntityIn.getWorld(), null);
            itemRenderer.renderItem(stackRight, ItemCameraTransforms.TransformType.FIXED, true, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ibakedmodel);
            matrixStackIn.pop();
        }
    }
}