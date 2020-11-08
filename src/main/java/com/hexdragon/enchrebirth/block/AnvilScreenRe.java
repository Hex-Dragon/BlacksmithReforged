package com.hexdragon.enchrebirth.block;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class AnvilScreenRe extends ContainerScreen<AnvilContainerRe> {
    private static final ResourceLocation ANVIL_RESOURCE = new ResourceLocation("textures/gui/container/anvil.png");

    public AnvilScreenRe(AnvilContainerRe container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.playerInventoryTitleY = 1000000; // 关闭 “物品栏” 三个字的显示
        this.titleX = 60;
    }

    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(ANVIL_RESOURCE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
        // 显示无法合成的红叉：输入不为空但输出为空
        if ((this.container.getSlot(0).getHasStack() || this.container.getSlot(1).getHasStack()) && !this.container.getSlot(2).getHasStack()) {
            this.blit(matrixStack, i + 99, j + 45, this.xSize, 0, 28, 21);
        }
    }

    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        RenderSystem.disableBlend();
        super.drawGuiContainerForegroundLayer(matrixStack, x, y);
        // 花费等级的文本提示
        int cost = this.container.totalCost.get();
        if (cost > 0) {
            int fontColor = 8453920;
            ITextComponent itextcomponent;
            if (cost >= 100 && !this.minecraft.player.abilities.isCreativeMode) {
                itextcomponent = new TranslationTextComponent("container.repair.expensive");
                fontColor = 16736352;
            } else if (!this.container.getSlot(2).getHasStack()) {
                itextcomponent = null;
            } else {
                itextcomponent = new TranslationTextComponent("container.repair.cost", cost);
                if (!this.container.getSlot(2).canTakeStack(this.playerInventory.player)) {
                    fontColor = 16736352;
                }
            }
            if (itextcomponent != null) {
                int xpos = this.xSize - 8 - this.font.getStringPropertyWidth(itextcomponent) - 2;
                fill(matrixStack, xpos - 2, 67, this.xSize - 8, 79, 1325400064);
                this.font.func_243246_a(matrixStack, itextcomponent, (float) xpos, 69.0F, fontColor);
            }
        }
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
