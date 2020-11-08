package com.hexdragon.core.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ContainerScreenRe<T extends Container> extends ContainerScreen<T> {
    public ContainerScreenRe(Container screenContainer, PlayerInventory inv, ITextComponent titleIn) {super((T) screenContainer, inv, titleIn); }

    // 提供扩展的 showPlayerInventoryTitle 属性：是否显示 “物品栏” 这一文本
    public boolean showPlayerInventoryTitle = true;

    @Override protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        this.font.func_243248_b(matrixStack, this.title, (float) this.titleX, (float) this.titleY, 4210752);
        if (showPlayerInventoryTitle)
            this.font.func_243248_b(matrixStack, this.playerInventory.getDisplayName(), (float) this.playerInventoryTitleX, (float) this.playerInventoryTitleY, 4210752);
    }

}
