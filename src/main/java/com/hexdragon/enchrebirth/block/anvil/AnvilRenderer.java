package com.hexdragon.enchrebirth.block.anvil;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.AnvilBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

// 渲染铁砧中存储的物品
@OnlyIn(Dist.CLIENT) public class AnvilRenderer extends TileEntityRenderer<AnvilTileEntity> {
    public AnvilRenderer(TileEntityRendererDispatcher rendererDispatcherIn) { super(rendererDispatcherIn); }

    @Override public void render(AnvilTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        Direction direction = tileEntityIn.getBlockState().get(AnvilBlock.FACING);

        // 渲染左侧的物品
        ItemStack stackLeft = tileEntityIn.getStackInSlot(0);
        if (!stackLeft.isEmpty()) {
            matrixStackIn.push();
            // 根据绝对位置平移到铁砧顶部（必须是完整的 1 格，不能作任何相对移动，否则在旋转后会出现问题）
            matrixStackIn.translate(0.5, 1, 0.5);
            // 随着方块朝向一起旋转
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90 - direction.getHorizontalAngle()));
            // 将物品从竖直摆放旋转到水平
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
            // 根据相对位置重新平移（坐标轴受到 rotate 影响，现在的 xyz 对应实际的 左/前/底）
            // 由于铁砧改为了 15/16 格高，物品还需要下移 0.05 格（正好就是 0.05 格，我也不知道为啥）
            matrixStackIn.translate(0.2, 0.04, 0.05);
            // 装饰性的左右旋转
            matrixStackIn.rotate(Vector3f.ZN.rotationDegrees(5.0F));
            // 缩小到 0.375 倍（渲染原本均为 1 格大小）
            matrixStackIn.scale(0.375F, 0.375F, 0.375F);
            IBakedModel ibakedmodel = itemRenderer.getItemModelWithOverrides(stackLeft, tileEntityIn.getWorld(), null);
            itemRenderer.renderItem(stackLeft, ItemCameraTransforms.TransformType.FIXED, true, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ibakedmodel);
            matrixStackIn.pop();
        }

        // 渲染右侧的物品
        ItemStack stackRight = tileEntityIn.getStackInSlot(1);
        if (!stackRight.isEmpty()) {
            matrixStackIn.push();
            matrixStackIn.translate(0.5, 1, 0.5);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90 - direction.getHorizontalAngle()));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
            matrixStackIn.translate(-0.23, -0.06, 0.05);
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(15.0F));
            matrixStackIn.scale(0.375F, 0.375F, 0.375F);
            IBakedModel ibakedmodel = itemRenderer.getItemModelWithOverrides(stackRight, tileEntityIn.getWorld(), null);
            itemRenderer.renderItem(stackRight, ItemCameraTransforms.TransformType.FIXED, true, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ibakedmodel);
            matrixStackIn.pop();
        }

    }
}