package com.hexdragon.enchrebirth.block;

import com.hexdragon.core.gui.ContainerScreenRe;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class GrindstoneScreenRe extends ContainerScreenRe<GrindstoneContainerRe> {
    private static final ResourceLocation GRINDSTONE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/grindstone.png");

    public GrindstoneScreenRe(GrindstoneContainerRe container, PlayerInventory playerInventory, ITextComponent textComponent) {
        super(container, playerInventory, textComponent);
        this.showPlayerInventoryTitle = false;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        this.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GRINDSTONE_GUI_TEXTURES);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
        // 显示无法合成的红叉：输入、输出仅一方为空
        if (this.container.getSlot(0).getHasStack() ^ (this.container.getSlot(1).getHasStack() || this.container.getSlot(2).getHasStack())) {
            this.blit(matrixStack, i + 92, j + 31, this.xSize, 0, 28, 21);
        }
    }
}
