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
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Quaternion;

// 渲染铁砧中存储的物品
public class AnvilRenderer extends TileEntityRenderer<AnvilTileEntity> {
    public AnvilRenderer(TileEntityRendererDispatcher rendererDispatcherIn) { super(rendererDispatcherIn); }

    @Override public void render(AnvilTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        //        matrixStackIn.push();
        //        matrixStackIn.translate(1, 0, 0);
        //        BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();
        //        BlockState state = Blocks.CHEST.getDefaultState();
        //        blockRenderer.renderBlock(state, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE);
        //        matrixStackIn.pop();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        // 渲染左侧的物品
        ItemStack stackLeft = new ItemStack(Items.DIAMOND); //tileEntityIn.inventory.getStackInSlot(0);
        if (!stackLeft.isEmpty()) {
            matrixStackIn.push();
            matrixStackIn.translate(1, 3, 0);
            IBakedModel ibakedmodel = itemRenderer.getItemModelWithOverrides(stackLeft, tileEntityIn.getWorld(), null);
            itemRenderer.renderItem(stackLeft, ItemCameraTransforms.TransformType.FIXED, true, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ibakedmodel);
            matrixStackIn.pop();
        }

        // 渲染右侧的物品
        ItemStack stackRight = new ItemStack(Items.ACACIA_LOG); //tileEntityIn.inventory.getStackInSlot(1);
        if (!stackRight.isEmpty()) {
            matrixStackIn.push();
            matrixStackIn.translate(0, 3, 0);
            matrixStackIn.rotate(new Quaternion(0, 0, 1, 1));
            IBakedModel ibakedmodel = itemRenderer.getItemModelWithOverrides(stackRight, tileEntityIn.getWorld(), null);
            itemRenderer.renderItem(stackRight, ItemCameraTransforms.TransformType.FIXED, true, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ibakedmodel);
            matrixStackIn.pop();
        }
    }
}