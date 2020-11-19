package com.hexdragon.blacksmithre.item.name_tag;

import com.hexdragon.corere.renderer.ItemRendererRe;
import com.hexdragon.blacksmithre.Main;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class NameTagScreen extends Screen {

    String defaultName;
    Hand hand;
    public NameTagScreen(String defaultName, Hand hand) {
        super(new TranslationTextComponent("gui.name_tag.title"));
        // 从右键事件中获取命名牌的原名与使用的手
        this.defaultName = defaultName; this.hand = hand;
    }

    TextFieldWidget textField;
    int guiLeft, guiTop;
    @Override protected void init() {
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        // 计算不知道有啥用的 GUI 边距
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        // 新建文本框；注意需要在这里指定 (x, y, width, height) 而不是在 render 中指定
        this.textField = new TextFieldWidget(this.font, guiLeft + 61, guiTop + 27, 95, 16, new StringTextComponent(defaultName));
        this.textField.setText(defaultName); // 需要使用 setText 设置文本，构建函数的最后一个参数不知道有啥用
        this.children.add(this.textField);
        super.init();
    }
    public void removed() {
        this.minecraft.keyboardListener.enableRepeatEvents(false);
    }

    int xSize = 176, ySize = 56, titleX = 61, titleY = 12; // GUI 大小与标题位置
    @Override public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        // 绘制背景图片
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(new ResourceLocation(Main.MODID, "textures/gui/name_tag.png"));
        this.blit(matrixStack, guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
        // 绘制标题
        this.font.func_243248_b(matrixStack, this.title, (float) this.titleX + guiLeft, (float) this.titleY + guiTop, 4210752);
        // 绘制文本框
        this.textField.render(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        // 渲染背景物品
        ItemRendererRe.renderItemModelIntoGUIScaled(itemRenderer, new ItemStack(Items.NAME_TAG), guiLeft + 25, guiTop + 19, 34);
    }

    // 在玩家按下物品栏键或回车时，主动关闭 GUI
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        InputMappings.Input mouseKey = InputMappings.getInputByCode(keyCode, scanCode);
        if (keyCode == 257 || // 玩家按下回车键
                (!textField.isFocused() && minecraft.gameSettings.keyBindInventory.isActiveAndMatches(mouseKey))) {
            this.closeScreen();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    // 在关闭 GUI 时根据文本框内容更新命名牌的 DisplayName
    // 必须通过网络发包的方式提交更新，否则只是客户端 “认为” 名字变了，把物品换个格子就又改回去了
    public void onClose() {
        new NameTagPacket(textField.getText(), hand).sendToServer();
    }

    // 让游戏在打开 GUI 时不会暂停
    public boolean isPauseScreen() {return false;}

    // 在玩家挂掉的时候关闭 GUI
    public void tick() {
        super.tick();
        if (!this.minecraft.player.isAlive() || this.minecraft.player.removed) this.minecraft.player.closeScreen();
    }
    public void closeScreen() {
        this.minecraft.player.closeScreen();
        super.closeScreen();
    }

}
