package com.hexdragon.enchrebirth.item.name_tag;

import com.hexdragon.enchrebirth.Main;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

public class NameTagScreen extends Screen {

    String defaultName;
    public NameTagScreen(String defaultName) {
        super(new TranslationTextComponent("gui.name_tag.title"));
        this.defaultName = defaultName;
    }
    final ResourceLocation GUI_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/name_tag.png");
    TextFieldWidget textField;

    @Override
    protected void init() {
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        // (x,y), (width, height)
        this.textField = new TextFieldWidget(this.font, guiLeft + 61, guiTop + 27, 95, 16, new TranslationTextComponent("item.name_tag.name"));
        this.textField.setText(defaultName);
        this.children.add(this.textField);
        super.init();
    }

    int xSize = 176, ySize = 56;
    int titleX = 61, titleY = 12;
    int guiLeft, guiTop;

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI_TEXTURE);
        this.blit(matrixStack, guiLeft, guiTop, 0, 0, this.xSize, this.ySize);

        this.font.func_243248_b(matrixStack, this.title, (float) this.titleX + guiLeft, (float) this.titleY + guiTop, 4210752);
        this.textField.render(matrixStack, mouseX, mouseY, partialTicks);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public boolean isPauseScreen() {
        return false;
    }
    public void tick() {
        super.tick();
        if (!this.minecraft.player.isAlive() || this.minecraft.player.removed) {
            this.minecraft.player.closeScreen();
        }
    }
    public void removed() {
        this.minecraft.keyboardListener.enableRepeatEvents(false);
    }
    public void closeScreen() {
        this.minecraft.player.closeScreen();
        super.closeScreen();
    }
    public void onClose() {
        Main.LOGGER.warn("CLOSE: " + textField.getText());

    }
}
