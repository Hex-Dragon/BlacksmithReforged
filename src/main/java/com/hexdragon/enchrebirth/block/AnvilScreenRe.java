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

@OnlyIn(Dist.CLIENT)
public class AnvilScreenRe extends ContainerScreen<AnvilContainerRe> {

    public AnvilScreenRe(AnvilContainerRe container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.playerInventoryTitleY = 1000000; // 关闭 “物品栏” 三个字的显示
        this.titleX = 60;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        RenderSystem.disableBlend();
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(this.ANVIL_RESOURCE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
        // this.blit(matrixStack, i + 59, j + 20, 0, this.ySize + (this.container.getSlot(0).getHasStack() ? 0 : 16), 110, 16);
        // 显示无法合成的红叉：输入不为空但输出为空
        if ((this.container.getSlot(0).getHasStack() || this.container.getSlot(1).getHasStack()) && !this.container.getSlot(2).getHasStack()) {
            this.blit(matrixStack, i + 99, j + 45, this.xSize, 0, 28, 21);
        }

    }

    private static final ResourceLocation ANVIL_RESOURCE = new ResourceLocation("textures/gui/container/anvil.png");
    private static final ITextComponent field_243333_B = new TranslationTextComponent("container.repair.expensive");

    public void resize(Minecraft minecraft, int width, int height) {
        this.init(minecraft, width, height);
    }

    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        RenderSystem.disableBlend();
        super.drawGuiContainerForegroundLayer(matrixStack, x, y);
        int i = this.container.getMaximumCost();
        if (i > 0) {
            int j = 8453920;
            ITextComponent itextcomponent;
            if (i >= 40 && !this.minecraft.player.abilities.isCreativeMode) {
                itextcomponent = field_243333_B;
                j = 16736352;
            } else if (!this.container.getSlot(2).getHasStack()) {
                itextcomponent = null;
            } else {
                itextcomponent = new TranslationTextComponent("container.repair.cost", i);
                if (!this.container.getSlot(2).canTakeStack(this.playerInventory.player)) {
                    j = 16736352;
                }
            }

            if (itextcomponent != null) {
                int k = this.xSize - 8 - this.font.getStringPropertyWidth(itextcomponent) - 2;
                int l = 69;
                fill(matrixStack, k - 2, 67, this.xSize - 8, 79, 1325400064);
                this.font.func_243246_a(matrixStack, itextcomponent, (float) k, 69.0F, j);
            }
        }

    }

}
