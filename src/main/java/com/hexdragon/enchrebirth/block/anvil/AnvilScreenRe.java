package com.hexdragon.enchrebirth.block.anvil;

import com.hexdragon.corere.renderer.ItemRendererRe;
import com.hexdragon.enchrebirth.Main;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class AnvilScreenRe extends ContainerScreen<AnvilContainerRe> {
    private static final ResourceLocation ANVIL_RESOURCE = new ResourceLocation(Main.MODID, "textures/gui/container/anvil.png");

    public AnvilScreenRe(AnvilContainerRe container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.playerInventoryTitleY = 1000000; // 关闭 “物品栏” 三个字的显示
        this.titleX = 61; this.titleY = 12; this.ySize = 150;
    }

    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(ANVIL_RESOURCE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
        // 显示无法合成的红叉：输入不为空但输出为空
        if ((this.container.getSlot(0).getHasStack() || this.container.getSlot(1).getHasStack()) && !this.container.getSlot(2).getHasStack()) {
            this.blit(matrixStack, i + 103, j + 26, this.xSize, 0, 28, 21);
        }
        // TODO : 当铁砧破坏等级增加的时候存储的物品会掉出来
        // TODO : 带有腐朽附魔的物品在满耐久的时候和原料一起放入铁砧允许修复，而不是打上红叉
        // TODO : <验证> 检查使用 Main 获取 TileEntity 的方式是否会在多人造成兼容性问题
        // TODO : 尝试找到更简洁的当前打开的 Block 的获取方式
        // TODO : Timicasto 汇报的 Bug：按 Shift 转移物品容易崩溃？
        // 渲染当前的 TileEntity
        ItemRendererRe.renderItemModelIntoGUIScaled(itemRenderer, new ItemStack(Main.LastTileEntity.getBlockState().getBlock()), i + 21, j + 15, 64);
    }

    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        RenderSystem.disableBlend();
        super.drawGuiContainerForegroundLayer(matrixStack, x, y);
    }

    // 无需修改的方法
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        RenderSystem.disableBlend();
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }
    public void resize(Minecraft minecraft, int width, int height) {
        this.init(minecraft, width, height);
    }

}
