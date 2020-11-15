package com.hexdragon.enchrebirth.item.name_tag;

import com.hexdragon.enchrebirth.Main;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class NameTagPacket {

    // 数据包内容
    private final Hand hand; // 修改哪只手上的物品
    private final String displayName; // 物品的新名称
    public NameTagPacket(String displayName, Hand hand) {
        this.displayName = displayName;
        this.hand = hand;
    }

    // 数据包实例与字节流的互相转化
    public NameTagPacket(PacketBuffer buffer) {
        hand = buffer.readEnumValue(Hand.class);
        displayName = buffer.readString(Short.MAX_VALUE);
    }
    public void toBytes(PacketBuffer buf) {
        buf.writeEnumValue(this.hand);
        buf.writeString(this.displayName);
    }

    public void handler(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // 服务端接收到数据包的事件处理
            ServerPlayerEntity player = ctx.get().getSender();
            ItemStack item = player.getHeldItem(this.hand);
            if (item.getItem() == Items.NAME_TAG) { // 检查物品，避免客户端通过伪造数据包作弊
                item.setDisplayName(new StringTextComponent(this.displayName));
            } else {
                Main.LOGGER.warn("接收到修改命名牌名称的数据包，但玩家并未手持命名牌：" + this.displayName);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
