package com.hexdragon.util.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class Packet {

    // 编码与解码方法
    // public abstract <T extends Packet> T decoder(PacketBuffer buffer);
    public abstract void encoder(PacketBuffer buf);

    // 服务端接收到数据包的事件处理
    public void onServerReceivePacketConsumer(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> onServerReceivePacket(ctx));
        ctx.get().setPacketHandled(true);
    }
    public abstract void onServerReceivePacket(Supplier<NetworkEvent.Context> ctx);

    // 将数据包发送至服务器
    public void sendToServer() {PacketManager.channel.sendToServer(this);}

}
