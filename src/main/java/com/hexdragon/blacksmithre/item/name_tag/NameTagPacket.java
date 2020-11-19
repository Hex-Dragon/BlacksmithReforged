package com.hexdragon.blacksmithre.item.name_tag;

import com.hexdragon.corere.network.PacketRe;
import com.hexdragon.blacksmithre.Main;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;

public class NameTagPacket extends PacketRe {

    // 数据包内容
    private final Hand hand; // 修改哪只手上的物品
    private final String displayName; // 物品的新名称
    public NameTagPacket(String displayName, Hand hand) {
        this.displayName = displayName;
        this.hand = hand;
    }

    // 数据包的编码解码
    public static NameTagPacket decoder(PacketBuffer buffer) {
        return new NameTagPacket(buffer.readString(Short.MAX_VALUE), buffer.readEnumValue(Hand.class));
    }
    @Override public void encoder(PacketBuffer buf) {
        buf.writeString(this.displayName);
        buf.writeEnumValue(this.hand);
    }

    // 接收到数据包的事件处理
    @Override public void onServerReceivePacket(@Nullable ServerPlayerEntity senderPlayer) {
        ItemStack item = senderPlayer.getHeldItem(this.hand);
        if (item.getItem() == Items.NAME_TAG) { // 检查物品，避免客户端通过伪造数据包作弊
            item.setDisplayName(new StringTextComponent(this.displayName));
        } else {
            Main.LOGGER.warn("接收到修改命名牌名称的数据包，但玩家并未手持命名牌");
        }
    }

}
